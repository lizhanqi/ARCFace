package com.lzq.samplecode.demo;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sun.jna.Memory;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.direct.*;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

public class Tutorial {

    private static final int width = 1920;

    private static final int height = 1080;

    private final Frame frame;

    private final JPanel videoSurface;

    private final BufferedImage image;

    DirectMediaPlayerComponent mediaPlayerComponent;
    TutorialRenderCallbackAdapter callbackAdapter = new TutorialRenderCallbackAdapter();

    public static void main(final String[] args) {
        new NativeDiscovery().discover();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Tutorial(args);
            }
        });
    }

    public Tutorial(String[] args) {
        frame = new Frame();
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());
        frame.setBounds(0, 0, width, height);
//        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
           }
        });
        videoSurface = new VideoSurfacePanel();
        frame.add(videoSurface);
//       frame.setContentPane(videoSurface);
        image = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .createCompatibleImage(width, height);


        final BufferFormatCallback bufferFormatCallback = new BufferFormatCallback() {
            @Override
            public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
                return new RV32BufferFormat(frame.getWidth(), frame.getHeight());
            }
        };

// mediaPlayerComponent=new EmbeddedMediaPlayerComponent();
//        frame.add(mediaPlayerComponent);
        mediaPlayerComponent = new DirectMediaPlayerComponent(bufferFormatCallback) {
            @Override
            protected RenderCallback onGetRenderCallback() {
                return callbackAdapter;
            }
        };
        videoSurface.setBackground(Color.RED);
        //监听窗口尺寸改变事件
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                videoSurface.setSize(new Dimension(frame.getWidth(),frame.getHeight()));
//                RV32BufferFormat newValue =new RV32BufferFormat(frame.getWidth(), frame.getHeight());
//                DefaultDirectMediaPlayer defaultDirectMediaPlayer=      (DefaultDirectMediaPlayer)  mediaPlayerComponent.getMediaPlayer();
//                Class class1 =   defaultDirectMediaPlayer.getClass();
//                Field field = null; //  getDeclaredField可以获取私有的变量
//                try {
//                    field = class1.getDeclaredField("bufferFormat");
//                    field.setAccessible(true); // 为true时可以访问私有类型变量
//                    field.set(defaultDirectMediaPlayer, newValue); // 将i的值设置为111
//                } catch (NoSuchFieldException ex) {
//                    ex.printStackTrace();
//                } catch (IllegalAccessException ex) {
//                    ex.printStackTrace();
//                }

            }
        });
        String url="rtsp://admin:EKKAIN@192.168.1.100/Streaming/Channels/1";
//        url="http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8";
        mediaPlayerComponent.getMediaPlayer().playMedia("http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8");

    }

    private class VideoSurfacePanel extends JPanel {
        private VideoSurfacePanel() {
            setBackground(Color.white);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(image, null, 0, 0);
        }
    }

    int [] a  =new int[width * height];
    private class TutorialRenderCallbackAdapter extends RenderCallbackAdapter {

        private TutorialRenderCallbackAdapter() {
            super(a);
        }



        @Override
        protected void onDisplay(DirectMediaPlayer mediaPlayer, int[] rgbBuffer) {

            image.setRGB(0, 0, width, height, rgbBuffer, 0, width);
            videoSurface.repaint();

        }
    }
}