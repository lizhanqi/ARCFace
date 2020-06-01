package com.lzq.faceserver.bean;

import com.arcsoft.face.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.IplImage;

import java.util.ArrayList;
import java.util.List;

public class FrameResut {

    public  FrameResut(Frame frame, byte[] imageData, IplImage iplImage) {
        this.frame = frame;
        this.imageData = imageData;
        this.iplImage = iplImage;
        Thread th = new Thread() {
            @Override
            public void run() {
                super.run();
            }
        };
    }


    /**
     * 图片信息
     */
    private Frame frame;
    private byte[] imageData;
    private IplImage iplImage;

    public byte[] getImageData() {
        return imageData;
    }

    public Frame getFrame() {
        return frame;
    }

    public IplImage getIplImage() {
        return iplImage;
    }

    /**
     * 识别到的人脸
     */
    public List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
    /**
     * 识别的性别
     */
    public   List<GenderInfo> genderInfoList = new ArrayList<GenderInfo>();
    //年龄检测
    public   List<AgeInfo> ageInfoList = new ArrayList<AgeInfo>();
    //3D信息检测
    public List<Face3DAngle> face3DAngleList = new ArrayList<Face3DAngle>();
    //活体检测
    public  List<LivenessInfo> livenessInfoList = new ArrayList<LivenessInfo>();
    //IR活体检测
    public List<IrLivenessInfo> irLivenessInfo = new ArrayList<IrLivenessInfo>();
}
