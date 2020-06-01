package com.lzq.faceserver;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import com.arcsoft.face.enums.ImageFormat;
import com.arcsoft.face.toolkit.ImageInfo;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.IplImage;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.arcsoft.face.toolkit.ImageFactory.getGrayData;
import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

public class FaceCore {
    FaceEngine faceEngine;
    String appId = "Enm1vzGgM8et2QsVEKjH3sCqKxATSrXqo87tKJXdDjTP";
    String sdkKey = "D7tE4UNb7uto9f11FTWqVYJSXNcVax5U9NpwbeZxFH1A";
    //转换器
    OpenCVFrameConverter.ToIplImage toIplImage = new OpenCVFrameConverter.ToIplImage();

    FaceEngine init() {
        String path = System.getProperty("user.dir");
        faceEngine  = new FaceEngine(path+"\\arcsoft-lib");
        //激活引擎
        int activeCode = faceEngine.activeOnline(appId, sdkKey);
        if (activeCode != ErrorInfo.MOK.getValue() && activeCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("引擎激活失败");
            return null;
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

        if (initCode != ErrorInfo.MOK.getValue()) {
            System.out.println("初始化引擎失败");
            return null;
        }
        return faceEngine;
    }

    void use() {
        //人脸检测
        ImageInfo imageInfo = getRGBData(new File("1.jpg"));
        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
        int detectCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList);
        System.out.println(faceInfoList);

        //特征提取
        FaceFeature faceFeature = new FaceFeature();
        int extractCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(),
                ImageFormat.CP_PAF_BGR24, faceInfoList.get(0), faceFeature);
        System.out.println("特征值大小：" + faceFeature.getFeatureData().length);

        //人脸检测2
        ImageInfo imageInfo2 = getRGBData(new File("2.jpg"));
        List<FaceInfo> faceInfoList2 = new ArrayList<FaceInfo>();
        int detectCode2 = faceEngine.detectFaces(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList2);
        System.out.println(faceInfoList);

        //特征提取2
        FaceFeature faceFeature2 = new FaceFeature();
        int extractCode2 = faceEngine.extractFaceFeature(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList2.get(0), faceFeature2);
        System.out.println("特征值大小：" + faceFeature.getFeatureData().length);

        //特征比对
        FaceFeature targetFaceFeature = new FaceFeature();
        targetFaceFeature.setFeatureData(faceFeature.getFeatureData());
        FaceFeature sourceFaceFeature = new FaceFeature();
        sourceFaceFeature.setFeatureData(faceFeature2.getFeatureData());
        FaceSimilar faceSimilar = new FaceSimilar();
        int compareCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
        System.out.println("相似度：" + faceSimilar.getScore());


        //人脸属性检测
        FunctionConfiguration configuration = new FunctionConfiguration();
        configuration.setSupportAge(true);
        configuration.setSupportFace3dAngle(true);
        configuration.setSupportGender(true);
        configuration.setSupportLiveness(true);
        int processCode = faceEngine.process(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList, configuration);


        //性别检测
        List<GenderInfo> genderInfoList = new ArrayList<GenderInfo>();
        int genderCode = faceEngine.getGender(genderInfoList);
        //assertEquals("性别检测失败", genderCode, ErrorInfo.MOK.getValue());
        System.out.println("性别：" + genderInfoList.get(0).getGender());

        //年龄检测
        List<AgeInfo> ageInfoList = new ArrayList<AgeInfo>();
        int ageCode = faceEngine.getAge(ageInfoList);
        //   assertEquals("年龄检测失败", ageCode, ErrorInfo.MOK.getValue());
        System.out.println("年龄：" + ageInfoList.get(0).getAge());

        //3D信息检测
        List<Face3DAngle> face3DAngleList = new ArrayList<Face3DAngle>();
        int face3dCode = faceEngine.getFace3DAngle(face3DAngleList);
        System.out.println("3D角度：" + face3DAngleList.get(0).getPitch() + "," + face3DAngleList.get(0).getRoll() + "," + face3DAngleList.get(0).getYaw());

        //活体检测
        List<LivenessInfo> livenessInfoList = new ArrayList<LivenessInfo>();
        int livenessCode = faceEngine.getLiveness(livenessInfoList);
        System.out.println("活体：" + livenessInfoList.get(0).getLiveness());


        //IR属性处理
        ImageInfo imageInfoGray = getGrayData(new File("1.jpg"));
        List<FaceInfo> faceInfoListGray = new ArrayList<FaceInfo>();
        int detectCodeGray = faceEngine.detectFaces(imageInfoGray.getImageData(), imageInfoGray.getWidth(), imageInfoGray.getHeight(), ImageFormat.CP_PAF_GRAY, faceInfoListGray);

        FunctionConfiguration configuration2 = new FunctionConfiguration();
        configuration2.setSupportIRLiveness(true);
        int processCode2 = faceEngine.processIr(imageInfoGray.getImageData(), imageInfoGray.getWidth(), imageInfoGray.getHeight(), ImageFormat.CP_PAF_GRAY, faceInfoListGray, configuration2);
        //IR活体检测
        List<IrLivenessInfo> irLivenessInfo = new ArrayList<IrLivenessInfo>();
        int livenessIr = faceEngine.getLivenessIr(irLivenessInfo);
        System.out.println("IR活体：" + irLivenessInfo.get(0).getLiveness());


        //设置活体检测参数
        int paramCode = faceEngine.setLivenessParam(0.8f, 0.8f);


        //获取激活文件信息
        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
        int activeFileCode = faceEngine.getActiveFileInfo(activeFileInfo);

    }

    int unInit() {
        return faceEngine.unInit();
    }

    public IplImage frame2IplImage(Frame frame) {
        return toIplImage.convert(frame);
    }


    Java2DFrameConverter converter = new Java2DFrameConverter();

    /**
     * 帧数据变为图片
     * @param image
     * @return
     */
    BufferedImage frame2Image(Frame image) {
        return converter.getBufferedImage(image, Java2DFrameConverter.getBufferedImageType(image) == 0 ? 1.0D : 1.0, false, (ColorSpace) null);
    }

   void saveFrame2Image(Frame frame){
       File outputfile = new File("E:\\face\\"+System.currentTimeMillis()+"image.jpg");
       BufferedImage bufferedImage = frame2Image(frame);
       try {
           ImageIO.write(bufferedImage, "jpg", outputfile);
       } catch (IOException e) {
           e.printStackTrace();
       }
    }

    /**
     * 保存脸部数据图片
     * @param bufferedImage
     */

    void saveFrame2JustFaceImage(BufferedImage bufferedImage){
        File outputfile = new File("E:\\face\\cFace\\"+System.currentTimeMillis()+"image.jpg");


        try {
            ImageIO.write(bufferedImage, "jpg", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
