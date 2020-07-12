package com.lzq.faceserver;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.ImageFormat;
import com.lzq.faceserver.bean.FrameResut;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.CvPoint;
import org.bytedeco.opencv.opencv_core.CvScalar;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * hls播放实时视频慢，一是协议上的问题，十几秒的误差用hls协议，
 * 秒级的误差用rtmp协议，
 * 毫秒级的误差用rtsp
 * ，二是这个模块的缓存问题，可以尝试降低缓存以及减少推流时的缓存。
 * //从rtmp服务器拉流 通过nginx的rtmp模块可迅速简单搭建 单纯的rtmp速度也有3s左右的误差 改为通过websocket传输
 * //        play("rtmp://192.168.0.1/vod/1.mp4");
 */
//https://blog.csdn.net/qq_42873492/article/details/104214476?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.nonecase
//https://blog.csdn.net/xxxlllbbb/article/details/104819683

/**
 * 最原始方案
 */
//todo 播放卡顿 ， 人脸检索
public class UIFace extends Thread {
    /**
     * 根据坐标获取对应的脸部信息
     * @param x
     * @param y
     * @return
     */
    public FaceInfo getUser(int x, int y) {
        for (FaceInfo faceInfo : currentShowFace) {
            int left = faceInfo.getRect().getLeft();
            int top = faceInfo.getRect().getTop();
            int right = faceInfo.getRect().getRight();
            int bottom = faceInfo.getRect().getBottom();
            boolean b1 = x >= left;
            boolean b2 = x <= right;
            boolean b3 = y >= top;
            boolean b4 = y <= bottom;
            System.out.println("b1" + b1 + "b2" + b2 + "b3" + b3 + "b4" + b4);
            if (b1 && b2 && b3 && b4) {
                return faceInfo;
            }
        }
        return null;
    }
    static FaceCore faceCore = new FaceCore();
    static FaceEngine faceEngine = faceCore.init();
    static List<FaceInfo> currentShowFace = new LinkedList<FaceInfo>();
   CanvasFrame rootFrame;
    void initView(double v) {
        String name = Thread.currentThread().getName();
        System.out.println(name);
        rootFrame = new CanvasFrame("云端视频流", v);//新建一个窗口
        rootFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rootFrame.setAlwaysOnTop(true);
        rootFrame.setResizable(true);
        // 获取canvas
        Canvas canvas = rootFrame.getCanvas();
        // 对canvas设置鼠标监听事件
        canvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                // 控制台输出点击的坐标
                FaceInfo user = getUser(e.getX(), e.getY());
                if (user != null) {
                    System.out.println("点击的用户：" + user.toString());
                }
            }
        });
    }

    @Override
    public void run() {
        super.run();
        //流直播
        String url = "http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8";//cctv6
          url = "rtmp://58.200.131.2:1935/livetv/hunantv";
              //url = "rtmp://rtmp01open.ys7.com/openlive/5f49b6c11b7b4b6eafa1bda90337d05d.hd";
        final String finalUrl = url;
                RTMP rtmp = RTMP.create(finalUrl);
                rtmp.isPlaySound = true;
                rtmp.read();
                //初始化视图
                initView(CanvasFrame.getDefaultGamma() / rtmp.getGrabber().getGamma());
                rtmp.setCallBack(new RTMP.CallBack() {
                    public void onSound() {
                    }

                    public void onImage(Frame frame) {
                        Frame frameOrg = frame.clone();
                        IplImage iplImage = faceCore.frame2IplImage(frame);
                        byte[] imageData = new byte[iplImage.imageSize()];
                        iplImage.imageData().get(imageData);
                        FrameResut frameResut = new FrameResut(frame, imageData, iplImage);
                        //画人脸位置
                        for (FaceInfo faceInfo : currentShowFace) {
                            int left = faceInfo.getRect().getLeft();
                            int top = faceInfo.getRect().getTop();
                            int right = faceInfo.getRect().getRight();
                            int bottom = faceInfo.getRect().getBottom();
                            //绘制颜色
                            CvScalar cvScalar = opencv_core.cvScalar(100, 254, 255, 0);
                            CvPoint cvPoint = opencv_core.cvPoint(left, top);
                            CvPoint cvPoint1 = opencv_core.cvPoint(right, bottom);
                            int index = currentShowFace.lastIndexOf(faceInfo);
                            int sex = -1;
                            int age = -1;
                            if (frameResut.genderInfoList.size() > index) {
                                sex = frameResut.genderInfoList.get(index).getGender();
                            }
                            if (frameResut.ageInfoList.size() > index) {
                                age = frameResut.ageInfoList.get(index).getAge();
                            }
                            CvPoint textXY = opencv_core.cvPoint(left + 15, top + 15);
                            opencv_imgproc.cvPutText(iplImage, "sex" + sex
                                            + "age" + age
                                    , textXY, opencv_imgproc.cvFont(1), cvScalar);
                            opencv_imgproc.cvRectangle(iplImage, cvPoint, cvPoint1, cvScalar, 1, 4, 0);
                            BufferedImage bufferedImage = faceCore.frame2Image(frameOrg);
                            BufferedImage subimage = bufferedImage.getSubimage(left, top, right - left, bottom - top);
                            faceCore.saveFrame2JustFaceImage(subimage);
                        }
                        rootFrame.showImage(frame);
                    }
                });

                try {
                    rtmp.startPlay();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (FrameGrabber.Exception e) {
                    e.printStackTrace();
                }

    }


}