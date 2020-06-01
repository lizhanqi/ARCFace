package com.lzq.samplecode.demo;

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

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class CameraVideoDemo {
    public static void main(String[] args) throws FrameGrabber.Exception {

        String appId = "Enm1vzGgM8et2QsVEKjH3sCqKxATSrXqo87tKJXdDjTP";
        String sdkKey = "D7tE4UNb7uto9f11FTWqVYJSXNcVax5U9NpwbeZxFH1A";
        String path = System.getProperty("user.dir");

        FaceEngine faceEngine = new FaceEngine(path+"\\arcsoft-lib");
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

        //视频帧抓取器
        OpenCVFrameGrabber openCVFrameGrabber =new OpenCVFrameGrabber(0);

        openCVFrameGrabber.start();
        //显示
        CanvasFrame canvasFrame=new CanvasFrame("摄像头");
        //转换器
        OpenCVFrameConverter.ToIplImage toIplImage = new OpenCVFrameConverter.ToIplImage();
        //图像转换器 将帧中的image相关信息提取出来
        final Java2DFrameConverter converter = new Java2DFrameConverter();
        for (;;){
            //视频帧
            Frame grab = openCVFrameGrabber.grab();
            IplImage iplImage = toIplImage.convert(grab);
            byte[] imageData =new byte[iplImage.imageSize()];
            iplImage.imageData().get(imageData);
            //识别成功后的人脸信息结果
            List<FaceInfo> faceInfos =new LinkedList<FaceInfo>();
            //人脸识别
        int res=    faceEngine.detectFaces(imageData,iplImage.width(),iplImage.height(), ImageFormat.CP_PAF_BGR24,faceInfos);
        System.out.println(res);
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
//            Frame frame = toIplImage.convert(iplImage);
            canvasFrame.showImage(      grab);
        }

    }

}
