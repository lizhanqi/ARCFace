package com.lzq.faceserver;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.ImageFormat;
import com.lzq.faceserver.bean.FrameResut;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
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

    //todo 播放卡顿 ， 人脸检索
public class TestA {

    /**
     * 根据坐标获取对应的脸部信息
     *
     * @param x
     * @param y
     * @return
     */
    public static FaceInfo getUser(int x, int y) {

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

    public static void main(final String[] args) throws FrameGrabber.Exception, InterruptedException {
        //流直播
        String url = "http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8";//cctv6
        url = "rtmp://58.200.131.2:1935/livetv/hunantv";
//        url = "rtmp://rtmp01open.ys7.com/openlive/5f49b6c11b7b4b6eafa1bda90337d05d.hd";
        final String finalUrl = url;
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();


                RTMP rtmp = RTMP.create(finalUrl);
                rtmp.isPlaySound = true;
                rtmp.read();
                //初始化视图
                double v = CanvasFrame.getDefaultGamma() / rtmp.getGrabber().getGamma();
                System.out.println(rtmp.getGrabber().getGamma());
                System.out.println(v);
                final CanvasFrame rootFrame = new CanvasFrame("云端视频流", 1);//新建一个窗口
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


                rtmp.setCallBack(new RTMP.CallBack() {
                    public void onSound() {
                    }

                    public void onImage(Frame frame) {
                        Frame frameOrg = frame.clone();

                        //
                        IplImage iplImage = faceCore.frame2IplImage(frame);
                        byte[] imageData = new byte[iplImage.imageSize()];
                        iplImage.imageData().get(imageData);
                        FrameResut frameResut = new FrameResut(frame, imageData, iplImage);
                        //识别成功后的人脸信息结果
//                        List<FaceInfo> faceInfos = new LinkedList<FaceInfo>();
                        //人脸识别
//                            int res = faceEngine.detectFaces(imageData, iplImage.width(), iplImage.height(), ImageFormat.CP_PAF_BGR24, frameResut.faceInfoList);
//                        currentShowFace = frameResut.faceInfoList;
//                        getFaces(frameResut);
                        //画人脸位置
                        for (FaceInfo faceInfo : currentShowFace) {
                            //特征提取2
                    FaceFeature faceFeature2 = new FaceFeature();
//                            int extractCode2 = faceEngine.extractFaceFeature(frameResut.getImageData(),
//                                    frameResut.getIplImage().width(), frameResut.getIplImage().height(), ImageFormat.CP_PAF_BGR24, faceInfo
//                                    , faceFeature2);
//                            byte[] bytes = faceFeature2.getFeatureData();
//                            String s = new String(bytes);
//                            System.out.println("特征：" + s);
//                            FaceFeature faceFeature = new FaceFeature(s.getBytes());
//                            FaceSimilar faceSimilar = new FaceSimilar();
//                            int compareCode = faceEngine.compareFaceFeature(faceFeature, faceFeature2, faceSimilar);
//                            System.out.println("相似度：" + faceSimilar.getScore());
//
//


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
                            BufferedImage subimage = bufferedImage.getSubimage(left, top, right-left, bottom-top);
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
        };

        thread.start();

    }
String s =" ��D  �A\u007F��<\u0007\n" +
        "\u0003�g\u0007n��\u0019=�o��\u001A�]<\u0011b�<�C\u0006=4[�\u0013��<!��11\u0003=�1�<��\t�|�ܻ���<M�\u0018> \"9=4��=*R�=6��=C\u0016��ċ�=\u0019�B��� =�\u007F�8�\u0011���3�=\u0019X\u0001=���\n" +
        "=�Vq�����T��=d'�z��=��.�\u0006C��\u0007!�=������\u001F�\u000FO�<(P��7��=\n" +
        "=���=�%\u0011��\u0003v�|,|=�,���蟽P#\u0003����<䩰<��\u0005�x�5=7 �\u0018Ѹ=��y=tc �K�3��ϻ;�\u0001ѽ�#�:N�'��\u001C���u\u0005=�e\u0017=�_9=���=����G�h��\u001A[�v�b<��o=���!�3=�Q�=��!��=\u0001�/F�=_�=�\t\u05FC\n" +
        "v�<�>\u0016=���<�\\\u0012=\u0016�G=W���0Kb�N\\\u0001�O\u0003�<��\u001F���=�$���������=�u\u001F=Z�f�";
    private static void getFaces(final FrameResut frameResut) {
        Thread th = new Thread() {
            @Override
            public void run() {
                super.run();
                long timeMillis = System.currentTimeMillis();


                //人脸属性检测
                FunctionConfiguration configuration = new FunctionConfiguration();
                configuration.setSupportAge(true);
                configuration.setSupportFace3dAngle(true);
                configuration.setSupportGender(true);
                configuration.setSupportLiveness(true);
                int processCode = faceEngine.process(frameResut.getImageData(), frameResut.getIplImage().width(), frameResut.getIplImage().height(),
                        ImageFormat.CP_PAF_BGR24, currentShowFace, configuration);
                //性别检测
                List<GenderInfo> genderInfoList = new ArrayList<GenderInfo>();
                int genderCode = faceEngine.getGender(genderInfoList);
                //assertEquals("性别检测失败", genderCode, ErrorInfo.MOK.getValue());
                //年龄检测
                List<AgeInfo> ageInfoList = new ArrayList<AgeInfo>();
                int ageCode = faceEngine.getAge(ageInfoList);
                //   assertEquals("年龄检测失败", ageCode, ErrorInfo.MOK.getValue());
                //3D信息检测
                List<Face3DAngle> face3DAngleList = new ArrayList<Face3DAngle>();
                int face3dCode = faceEngine.getFace3DAngle(face3DAngleList);
                //活体检测
                List<LivenessInfo> livenessInfoList = new ArrayList<LivenessInfo>();
                int livenessCode = faceEngine.getLiveness(livenessInfoList);

            }
        };
        th.start();

    }

}