package com.lzq.faceserver;

import org.bytedeco.javacv.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * todo 视频保存尚未做
 */
public class RTMP  {
    interface CallBack {
        void onSound();

        void onImage(Frame frame);
    }

    CallBack callBack;
    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }


    public static RTMP create(String filepath) {
        return new RTMP(filepath);
    }

    FFmpegFrameGrabber grabber;

    public FFmpegFrameGrabber getGrabber() {
        return grabber;
    }

    ExecutorService executor = Executors.newSingleThreadExecutor();
    boolean isPlaySound = true;

    public boolean isPlaySound() {
        return isPlaySound;
    }

    public void setPlaySound(boolean playSound) {
        isPlaySound = playSound;
    }

    AudioFormat audioFormat;
    DataLine.Info info;
    SourceDataLine soundLine;
    String videoPath;
    private RTMP(String filepath) {
        videoPath = filepath;
    }


    public void run() {
        try {
            startPlay();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 准备
     */
    boolean read() {
        try {
            if (grabber != null) {
                System.out.println("RTMP已就绪");
                return false;
            }
            //开始抓取
            grabber = new FFmpegFrameGrabber(videoPath);
            grabber . setVideoBitrate(40000);
            grabber.start();
            //初始化扬声器
            audioFormat = new AudioFormat(grabber.getSampleRate(), 16, grabber.getAudioChannels(), true, true);
            info = new DataLine.Info(SourceDataLine.class, audioFormat);
            soundLine = (SourceDataLine) AudioSystem.getLine(info);
            soundLine.open(audioFormat);
            if (isPlaySound) {
                soundLine.start();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            System.out.println("RTMP准备失败");
            return false;
        }
        System.out.println("RTMP准备完成");
        return true;
    }

    void stopSoud() {
        System.out.println("RTMP静音");
        soundLine.stop();
    }

    void stopPlay() throws InterruptedException, FrameGrabber.Exception {
        if (grabber != null) {
            System.out.println("RTMP播放停止");
            soundLine.stop();
            grabber.stop();
            grabber.release();
        }
    }
    /*
    会阻断当前线程
     */


    void startPlay() throws InterruptedException, FrameGrabber.Exception {
        System.out.println("RTMP播放启动");
        File file = new File("E:\\face\\video\\");


        try {
                    while (!Thread.interrupted()) {
                        if(grabber==null){
                            break;
                        }

                         Frame     frame = grabber.grab();
                        try {
                            FileOutputStream fileOutputStream=new FileOutputStream(file);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }


                        if (frame == null) {
                            System.out.println("null");
                            break;
                        }
                        //图像帧
                        if (frame.image != null) {
                            if (callBack != null) {
                                callBack.onImage(frame);
                            }
                        } else if (frame.samples != null) {
                            //声音帧
                            final ShortBuffer channelSamplesShortBuffer = (ShortBuffer) frame.samples[0];
                            channelSamplesShortBuffer.rewind();
                            final ByteBuffer outBuffer = ByteBuffer.allocate(channelSamplesShortBuffer.capacity() * 2);
                            for (int i = 0; i < channelSamplesShortBuffer.capacity(); i++) {
                                short val = channelSamplesShortBuffer.get(i);
                                outBuffer.putShort(val);
                            }
                            // 写入到扬声器并准备下一次读取
                            try {
                                executor.submit(new Runnable() {
                                    public void run() {
                                        if (callBack != null) {
                                            callBack.onSound();
                                        }

                                        soundLine.write(outBuffer.array(), 0, outBuffer.capacity());
                                        outBuffer.clear();
                                    }
                                }).get();
                            } catch (InterruptedException interruptedException) {
                                Thread.currentThread().interrupt();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                } catch (FrameGrabber.Exception e) {
                    e.printStackTrace();
                }




        stopPlay();
        executor.shutdownNow();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}
