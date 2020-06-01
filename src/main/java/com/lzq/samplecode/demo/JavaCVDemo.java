package com.lzq.samplecode.demo;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.stream.FileImageOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;

import com.arcsoft.face.EngineConfiguration;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FunctionConfiguration;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import com.arcsoft.face.enums.ImageFormat;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.CvPoint;
import org.bytedeco.opencv.opencv_core.CvScalar;
import org.bytedeco.opencv.opencv_core.IplImage;

/**
 * hls播放实时视频慢，一是协议上的问题，十几秒的误差用hls协议，
 * 秒级的误差用rtmp协议，
 * 毫秒级的误差用rtsp
 * ，二是这个模块的缓存问题，可以尝试降低缓存以及减少推流时的缓存。
 *
 */
//https://blog.csdn.net/qq_42873492/article/details/104214476?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase
//https://blog.csdn.net/xxxlllbbb/article/details/104819683
public class JavaCVDemo {
    public static void main(String[] args) {
        //从rtmp服务器拉流 通过nginx的rtmp模块可迅速简单搭建 单纯的rtmp速度也有3s左右的误差 改为通过websocket传输
//        play("rtmp://192.168.0.1/vod/1.mp4");
        //流直播
        String url =  "http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8";//cctv6
        url="rtmp://58.200.131.2:1935/livetv/hunantv";
        play(url);
    }
    public static void play(final String filepath) {
        String appId = "Enm1vzGgM8et2QsVEKjH3sCqKxATSrXqo87tKJXdDjTP";
        String sdkKey = "D7tE4UNb7uto9f11FTWqVYJSXNcVax5U9NpwbeZxFH1A";
        String path = System.getProperty("user.dir");

        final FaceEngine faceEngine = new FaceEngine(path+"\\arcsoft-lib");
        //激活引擎
        int activeCode = faceEngine.activeOnline(appId, sdkKey);
        if (activeCode != ErrorInfo.MOK.getValue() && activeCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("引擎激活失败");
        }
        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_0_ONLY);
        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);
        //初始化引擎
        int initCode = faceEngine.init(engineConfiguration);
        Thread playThread = new Thread(new Runnable() {
            public void run() {
                try {
                    //开始抓取
                    @SuppressWarnings("resource") final FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filepath);
                    grabber.start();
                    System.out.println(grabber.getGamma());
                    double v=CanvasFrame.getDefaultGamma() / grabber.getGamma();
                    System.out.println(v);
                    //初始化视图
                    final CanvasFrame canvas = new CanvasFrame("云端视频流", v);//新建一个窗口
                     canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    //初始化扬声器
                    final AudioFormat audioFormat = new AudioFormat(grabber.getSampleRate(), 16, grabber.getAudioChannels(), true, true);
                    final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                    final SourceDataLine soundLine = (SourceDataLine) AudioSystem.getLine(info);
                     soundLine.open(audioFormat);
                     soundLine.start();
                    //图像转换器 将帧中的image相关信息提取出来
                    final Java2DFrameConverter converter = new Java2DFrameConverter();
                    //转换器
                    OpenCVFrameConverter.ToIplImage toIplImage = new OpenCVFrameConverter.ToIplImage();
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    while (!Thread.interrupted()) {
                        final Frame frame = grabber.grab();
                        if (frame == null) {
                            break;
                        }
                        //图像帧
                        if (frame.image != null) {
                            IplImage iplImage = toIplImage.convert(frame);
                            byte[] imageData =new byte[iplImage.imageSize()];
                            iplImage.imageData().get(imageData);
                            //识别成功后的人脸信息结果
                            List<FaceInfo> faceInfos =new LinkedList<FaceInfo>();
                            //人脸识别
                            int res=    faceEngine.detectFaces(imageData,iplImage.width(),iplImage.height(), ImageFormat.CP_PAF_BGR24,faceInfos);
                          System.out.println("识别结果"+res);
                            //画人脸位置
                            for (FaceInfo faceInfo : faceInfos) {
                                int left = faceInfo.getRect().getLeft();
                                int top = faceInfo.getRect().getTop();
                                int right = faceInfo.getRect().getRight();
                                int bottom = faceInfo.getRect().getBottom();
                                CvScalar cvScalar = opencv_core.cvScalar(0, 0, 255, 0);
                                CvPoint cvPoint = opencv_core.cvPoint(left, top);
                                CvPoint cvPoint1 = opencv_core.cvPoint(right, bottom);
                                opencv_imgproc.cvRectangle(iplImage,cvPoint,cvPoint1,cvScalar,1,4,0); 
                            }

                             canvas.showImage(      frame);
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
                                        soundLine.write(outBuffer.array(), 0, outBuffer.capacity());
                                        outBuffer.clear();
                                    }
                                }).get();
                            } catch (InterruptedException interruptedException) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }

                    executor.shutdownNow();
                    executor.awaitTermination(10, TimeUnit.SECONDS);
                    soundLine.stop();
                    grabber.stop();
                    grabber.release();
                } catch (Exception exception) {
                    System.exit(1);
                }
            }


        });
        playThread.start();
    }

}