package com.lzq.samplecode.demo;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
/**
 * 所属包:test<br>
 * 类名:SnapshotTest<br>
 * -------------------<br>
 * 描述:预设帧数，抽取截图<br>
 * -------------------<br>
 * 日期:2018年11月22日<br>
 * 作者:cuixin
 */
public class SnapshotTest  {

    private static final String NATIVE_LIBRARY_SEARCH_PATH = "C:\\Program Files\\VideoLAN\\VLC";
    public static void main(String[] args) throws Exception {

        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), NATIVE_LIBRARY_SEARCH_PATH);
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        @SuppressWarnings("unused")
        boolean discover = new NativeDiscovery().discover();
        //预设进度值
        List<Float>listBar=new ArrayList<Float>();
        listBar.add(0.20f);
        listBar.add(0.50f);
        listBar.add(0.70f);
        listBar.add(0.90f);
        MediaPlayerFactory factory = new MediaPlayerFactory();
        MediaPlayer mediaPlayer = factory.newEmbeddedMediaPlayer();
        mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
                System.out.println("截取的快照：+(filename=" + filename + ")");
            }
        });

        mediaPlayer.startMedia("E:\\face\\recoder\\testOne.flv");
        for (Float float1 : listBar) {
            mediaPlayer.setPosition(float1);
            Thread.sleep(1000);

            File file3 = new File((int)(float1*100)+".png");

            System.out.println("截图地址："+file3.getAbsolutePath());
            file3.deleteOnExit();
            if(!file3.exists()){
                file3.createNewFile();
            }
            mediaPlayer.saveSnapshot(file3);
            BufferedImage image3 = ImageIO.read(file3);
            show("截屏", image3, 3);
        }
        mediaPlayer.stop();
    }

    @SuppressWarnings("serial")
    private static void show(String title, final BufferedImage img, int i) {
        JFrame f = new JFrame(title);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane(new JPanel() {
            @Override
            protected void paintChildren(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.drawImage(img, null, 0, 0);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(img.getWidth(), img.getHeight());
            }
        });
        f.pack();
        f.setLocation(50 + (i * 50), 50 + (i * 50));
        //是否开启弹窗
        f.setVisible(false);
    }}
