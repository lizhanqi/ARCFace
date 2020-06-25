package com.lzq.samplecode.awt;

import com.lzq.samplecode.demo.Window;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.component.overlay.AbstractJWindowOverlayComponent;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class AWTApp {

    private  static   AWTApp thisApp = null;


    public static void main(String[] args) {
        thisApp = new AWTApp();
    }

    public AWTApp() {
            Frame f = new Frame("测试窗口");//标题
            //设置窗口的大小和位置
            f.setLocation(30, 30);
            f.setSize(250, 200);
            //将窗口显示出来（默认隐藏）
            f.setVisible(true);
//        AbstractJWindowOverlayComponent     ab =new AbstractJWindowOverlayComponent(f){
//            @Override
//            protected void onPaintOverlay(Graphics2D graphics) {
//                super.onPaintOverlay(graphics);
//                //指定字体
//                final Font logoFont = new Font("PingFang SC", Font.BOLD, 50);
//                FontMetrics metrics = graphics.getFontMetrics(logoFont);
//                graphics.drawString("text",0,0);
//            }
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files\\VideoLAN\\VLC");
        //打印版本，用来检验是否获得文件
        System.out.println(LibVlc.INSTANCE.libvlc_get_version());
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        Panel videoPane=  new Panel();
          final   EmbeddedMediaPlayerComponent playerComponent = new EmbeddedMediaPlayerComponent();
        videoPane.add(playerComponent);
        videoPane.setVisible(true);

        f.add(videoPane);
        Canvas canvas = new Canvas();
        playerComponent.add(canvas);
        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
        CanvasVideoSurface videoSurface = mediaPlayerFactory.newVideoSurface(canvas);
        playerComponent.getMediaPlayer().setVideoSurface(videoSurface);
        playerComponent.getMediaPlayer().playMedia("http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8"); // 直接播放视屏，参数是视屏文件的绝对路径

    }

}
