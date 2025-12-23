package com.eseeiot.option;

import android.content.Context;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaCodec.BufferInfo;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import androidx.annotation.NonNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class VoiceRecordHelper {
   public static final String FILE_RECORD_NAME = "PushAudioRecord";
   public static final String FILE_SYSTEM_AUDIO_NAME = "PushAudioSystem";
   private static final String TAG = "VoiceRecordHelper";
   public static final int RATE_8K = 8000;
   public static final int RATE_16K = 16000;
   private static final int AUDIO_DATA_8K = 320;
   private static final int AUDIO_DATA_16K = 640;
   private int mAudioSampleRate = 8000;
   private static final int AUDIO_FORMAT = 2;
   private static final int AUDIO_CHANNEL_CONFIG = 16;
   private static final int AUDIO_RESOURCE = 7;
   private int mRecordBufferSize;
   private byte[] mAudioData;
   private boolean isRecording = false;
   private AudioRecord mAudioRecorder;
   private boolean isEnableAAc;
   private boolean isRunning;
   private VoiceRecordHelper.VoiceRecode mVoiceRecode = null;
   private ThreadPoolExecutor mExecutor;
   private Context mContext;

   public VoiceRecordHelper() {
      this.mExecutor = new ThreadPoolExecutor(2, 2, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
   }

   public static VoiceRecordHelper create() {
      return create(8000);
   }

   public void setContext(Context mContext) {
      this.mContext = mContext;
   }

   public static VoiceRecordHelper create(int sampleRate) {
      VoiceRecordHelper mHelper = new VoiceRecordHelper();
      mHelper.initRecorder(sampleRate);
      return mHelper;
   }

   private void initRecorder(int sampleRate) {
      this.mAudioSampleRate = sampleRate;
      this.mRecordBufferSize = AudioRecord.getMinBufferSize(this.mAudioSampleRate, 16, 2);
      this.mAudioData = new byte[sampleRate == 16000 ? 640 : 320];
      int audioSource = 7;
      if (Build.BRAND.equalsIgnoreCase("samsung") || Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
         audioSource = 1;
      }

      this.mAudioRecorder = new AudioRecord(audioSource, this.mAudioSampleRate, 16, 2, this.mRecordBufferSize);
   }

   public void startRecord() throws IllegalStateException {
      if (this.mAudioRecorder == null) {
         this.initRecorder(this.mAudioSampleRate);
      }

      try {
         this.mAudioRecorder.startRecording();
      } catch (IllegalStateException var3) {
         this.mAudioRecorder = null;
         throw var3;
      }

      this.isRecording = true;
      final File tmpFile = this.createFile("PushAudioRecord.pcm");
      final File tmpOutFile = this.createFile("PushAudioRecord.wav");
      this.mExecutor.execute(new Runnable() {
         public void run() {
            try {
               FileOutputStream fileOutputStream = new FileOutputStream(tmpFile.getAbsoluteFile());

               while(VoiceRecordHelper.this.isRecording) {
                  int readsize = VoiceRecordHelper.this.mAudioRecorder.read(VoiceRecordHelper.this.mAudioData, 0, VoiceRecordHelper.this.mAudioData.length);
                  fileOutputStream.write(VoiceRecordHelper.this.mAudioData);
               }

               fileOutputStream.close();
               VoiceRecordHelper.pcmToWave(tmpFile.getAbsolutePath(), tmpOutFile.getAbsolutePath(), VoiceRecordHelper.this.mRecordBufferSize, VoiceRecordHelper.this.mAudioSampleRate);
               if (VoiceRecordHelper.this.mVoiceRecode != null) {
                  VoiceRecordHelper.this.mVoiceRecode.stopEncode();
               }

               VoiceRecordHelper.this.mVoiceRecode = null;
               if (VoiceRecordHelper.this.isEnableAAc) {
                  File tmpAAcFile = VoiceRecordHelper.this.createFile("PushAudioRecord.aac");
                  VoiceRecordHelper.this.mVoiceRecode = VoiceRecordHelper.this.pcmToAAc(tmpOutFile.getAbsolutePath(), tmpAAcFile.getAbsolutePath());
                  VoiceRecordHelper.this.mVoiceRecode.startEncode();
               }
            } catch (FileNotFoundException var3) {
               var3.printStackTrace();
            } catch (IOException var4) {
               var4.printStackTrace();
            }

         }
      });
   }

   public void stopRecord() {
      this.isRecording = false;
      if (this.mAudioRecorder != null) {
         try {
            this.mAudioRecorder.stop();
            this.mAudioRecorder.release();
         } catch (RuntimeException var2) {
            var2.printStackTrace();
            this.mAudioRecorder = null;
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

      this.mVoiceRecode = null;
      this.mAudioRecorder = null;
   }

   public void setEnableAAc(boolean enableAAc) {
      this.isEnableAAc = enableAAc;
   }

   public boolean isRecording() {
      return this.isRecording;
   }

   public static void pcmToWave(String pcmPath, String wavPath, int bufferSize, int sampleRate) {
      FileInputStream in = null;
      FileOutputStream out = null;
      long totalAudioLen = 0L;
      long totalDataLen = totalAudioLen + 36L;
      int channels = 1;
      long byteRate = (long)(16 * sampleRate * channels / 8);
      byte[] data = new byte[bufferSize];

      try {
         in = new FileInputStream(pcmPath);
         out = new FileOutputStream(wavPath);
         totalAudioLen = in.getChannel().size();
         totalDataLen = totalAudioLen + 36L;
         writeWaveFileHeader(out, totalAudioLen, totalDataLen, (long)sampleRate, channels, byteRate);

         while(in.read(data) != -1) {
            out.write(data);
         }

         in.close();
         out.close();
      } catch (FileNotFoundException var15) {
         var15.printStackTrace();
      } catch (IOException var16) {
         var16.printStackTrace();
      }

   }

   private VoiceRecordHelper.VoiceRecode pcmToAAc(String sourcePath, String aacPath) {
      int channels = 1;
      MediaFormat audioFormat = MediaFormat.createAudioFormat("audio/mp4a-latm", this.mAudioSampleRate, channels);
      int byteRate = 16 * this.mAudioSampleRate * channels / 8;
      audioFormat.setString("mime", "audio/mp4a-latm");
      audioFormat.setInteger("bitrate", byteRate);
      audioFormat.setInteger("channel-count", channels);
      audioFormat.setInteger("channel-mask", 16);
      audioFormat.setInteger("aac-profile", 2);
      audioFormat.setInteger("max-input-size", 262144);
      VoiceRecordHelper.VoiceRecode recode = new VoiceRecordHelper.VoiceRecode(sourcePath, aacPath, "audio/mp4a-latm", audioFormat, this.mAudioSampleRate);
      return recode;
   }

   private File createFile(String fileName) {
      File file = new File(this.mContext.getExternalFilesDir("").getAbsolutePath() + "/downloads/audio/");
      if (!file.exists()) {
         boolean tmp = file.mkdir();
         if (!tmp) {
            file.mkdirs();
         }
      }

      File audioFile = new File(file.getAbsolutePath() + "/" + fileName);
      if (audioFile.exists()) {
         audioFile.delete();
      }

      try {
         audioFile.createNewFile();
         return audioFile;
      } catch (IOException var5) {
         var5.printStackTrace();
         return null;
      }
   }

   public String getFilePath(String fileName) {
      File file = new File(this.mContext.getExternalFilesDir("").getAbsolutePath() + "/downloads/audio/");
      return file.getAbsolutePath() + "/" + fileName;
   }

   private static void writeWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate, int channels, long byteRate) throws IOException {
      byte[] header = new byte[]{82, 73, 70, 70, (byte)((int)(totalDataLen & 255L)), (byte)((int)(totalDataLen >> 8 & 255L)), (byte)((int)(totalDataLen >> 16 & 255L)), (byte)((int)(totalDataLen >> 24 & 255L)), 87, 65, 86, 69, 102, 109, 116, 32, 16, 0, 0, 0, 1, 0, (byte)channels, 0, (byte)((int)(longSampleRate & 255L)), (byte)((int)(longSampleRate >> 8 & 255L)), (byte)((int)(longSampleRate >> 16 & 255L)), (byte)((int)(longSampleRate >> 24 & 255L)), (byte)((int)(byteRate & 255L)), (byte)((int)(byteRate >> 8 & 255L)), (byte)((int)(byteRate >> 16 & 255L)), (byte)((int)(byteRate >> 24 & 255L)), 2, 0, 16, 0, 100, 97, 116, 97, (byte)((int)(totalAudioLen & 255L)), (byte)((int)(totalAudioLen >> 8 & 255L)), (byte)((int)(totalAudioLen >> 16 & 255L)), (byte)((int)(totalAudioLen >> 24 & 255L))};
      out.write(header, 0, 44);
   }

   public static class VoiceRecode {
      final int TIMEOUT_USEC = 10000;
      boolean inputDone;
      boolean outputDone;
      private MediaCodec codec;
      private boolean mIsRunning;
      private FileOutputStream os;
      private MediaExtractor extractor;
      private boolean needHeader = true;
      private int sampleRate;

      public VoiceRecode(@NonNull String sourcePath, @NonNull String outPath, String outputMime, @NonNull MediaFormat outputFormat, int sampleRate) {
         try {
            this.os = new FileOutputStream(new File(outPath));
            this.initMediaCodec(sourcePath, outputMime, outputFormat);
            this.sampleRate = sampleRate;
         } catch (FileNotFoundException var7) {
            var7.printStackTrace();
         } catch (Exception var8) {
            var8.printStackTrace();
         }

      }

      public void startEncode() {
         if (this.codec != null) {
            this.mIsRunning = true;
            (new Thread() {
               public void run() {
                  while(true) {
                     try {
                        if (VoiceRecode.this.mIsRunning) {
                           VoiceRecode.this.doExtract();
                           VoiceRecode.this.encode();
                           if (!VoiceRecode.this.inputDone || !VoiceRecode.this.outputDone) {
                              continue;
                           }
                        }
                     } catch (Exception var18) {
                        var18.printStackTrace();
                     } finally {
                        if (VoiceRecode.this.os != null) {
                           try {
                              VoiceRecode.this.os.close();
                           } catch (IOException var17) {
                              var17.printStackTrace();
                           }
                        }

                        if (VoiceRecode.this.codec != null) {
                           try {
                              VoiceRecode.this.codec.release();
                           } catch (Exception var16) {
                              var16.printStackTrace();
                           }
                        }

                        if (VoiceRecode.this.extractor != null) {
                           try {
                              VoiceRecode.this.extractor.release();
                           } catch (Exception var15) {
                              var15.printStackTrace();
                           }
                        }

                     }

                     return;
                  }
               }
            }).start();
         }
      }

      public void stopEncode() {
         this.mIsRunning = false;
      }

      private void initMediaCodec(String sourcePath, String outputMime, MediaFormat outputFormat) {
         try {
            this.codec = MediaCodec.createEncoderByType(outputMime);
            this.codec.configure(outputFormat, (Surface)null, (MediaCrypto)null, 1);
            this.codec.start();
            this.extractor = new MediaExtractor();
            this.extractor.setDataSource(sourcePath);
            int audioTrackIndex = this.selectTrack(this.extractor, true);
            this.extractor.selectTrack(audioTrackIndex);
         } catch (IOException var5) {
            var5.printStackTrace();
         } catch (Exception var6) {
            var6.printStackTrace();
         }

      }

      private int selectTrack(MediaExtractor extractor, boolean isaudio) {
         int numTracks = extractor.getTrackCount();

         for(int i = 0; i < numTracks; ++i) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString("mime");
            if (mime.startsWith("video/") && !isaudio) {
               return i;
            }

            if (mime.startsWith("audio/") && isaudio) {
               return i;
            }
         }

         return -1;
      }

      private void doExtract() {
         if (!this.inputDone && this.extractor != null && this.codec != null) {
            int inputBufferIndex = this.codec.dequeueInputBuffer(10000L);
            if (inputBufferIndex < 0) {
               Log.d("VoiceRecordHelper", "no input buffer available");
               return;
            }

            this.codec.getInputBuffers()[inputBufferIndex].clear();
            int sampleSize = this.extractor.readSampleData(this.codec.getInputBuffers()[inputBufferIndex], 0);
            Log.i("VoiceRecordHelper", "doExtract size" + sampleSize);
            if (sampleSize >= 0) {
               this.codec.queueInputBuffer(inputBufferIndex, 0, sampleSize, this.extractor.getSampleTime(), 0);
            } else {
               this.inputDone = true;
               this.codec.queueInputBuffer(inputBufferIndex, 0, 0, 0L, 4);
            }

            this.extractor.advance();
         }

      }

      private void encode() {
         BufferInfo bufferInfo = new BufferInfo();
         ByteBuffer outputBuffer = null;
         if (this.mIsRunning) {
            if (!this.outputDone) {
               int encoderStatus = this.codec.dequeueOutputBuffer(bufferInfo, 0L);
               if (encoderStatus == -1) {
                  Log.d("VoiceRecordHelper", "no output from decoder available");
               } else if (encoderStatus == -3) {
                  Log.d("VoiceRecordHelper", "output buffers changed");
               } else if (encoderStatus == -2) {
                  MediaFormat newFormat = this.codec.getOutputFormat();
                  Log.d("VoiceRecordHelper", "output format changed: " + newFormat);
               } else if (encoderStatus >= 0) {
                  if ((bufferInfo.flags & 4) != 0) {
                     this.outputDone = true;
                  }

                  try {
                     if (bufferInfo.size != 0 && this.os != null) {
                        byte[] header = new byte[7];
                        outputBuffer = this.codec.getOutputBuffers()[encoderStatus];
                        outputBuffer.position(bufferInfo.offset);
                        outputBuffer.limit(bufferInfo.offset + bufferInfo.size);
                        byte[] data = new byte[outputBuffer.remaining()];
                        outputBuffer.get(data);
                        if (this.needHeader) {
                           this.addADTStoPacket(header, data.length + header.length);
                           this.os.write(header);
                        }

                        this.os.write(data);
                        this.codec.releaseOutputBuffer(encoderStatus, false);
                     }
                  } catch (IOException var6) {
                     var6.printStackTrace();
                  }
               }
            }

         }
      }

      private void addADTStoPacket(byte[] packet, int packetLen) {
         int profile = 2;
         int freqIdx = this.sampleRate == 16000 ? 8 : 11;
         int chanCfg = 1;
         packet[0] = -1;
         packet[1] = -7;
         packet[2] = (byte)((profile - 1 << 6) + (freqIdx << 2) + (chanCfg >> 2));
         packet[3] = (byte)(((chanCfg & 3) << 6) + (packetLen >> 11));
         packet[4] = (byte)((packetLen & 2047) >> 3);
         packet[5] = (byte)(((packetLen & 7) << 5) + 31);
         packet[6] = -4;
      }
   }
}
