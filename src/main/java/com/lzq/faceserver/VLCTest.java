package com.lzq.faceserver;

import javax.swing.*;
import java.awt.*;

class Fram extends JFrame{
    public Fram(){
        setTitle("Test");
        setSize(800, 600);

        setResizable(true);
        setVisible(true);
        setBackground(Color.WHITE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        vgaPanel vgaPanel = new vgaPanel();
        add(vgaPanel, BorderLayout.WEST);

        regPanel regPanel = new regPanel();
        add(regPanel, BorderLayout.EAST);

        JPanel northJPanel = new JPanel();
        northJPanel.setPreferredSize(new Dimension(800, 0));
        add(northJPanel, BorderLayout.NORTH);

        JPanel buttonJPanel = new JPanel();
        buttonJPanel.setPreferredSize(new Dimension(800, 40));
        buttonJPanel.add(new JButton("Run"));
        buttonJPanel.add(new JButton("Step"));
        buttonJPanel.add(new JButton("Restart"));
        add(buttonJPanel, BorderLayout.SOUTH);
    }
}

class vgaPanel extends JPanel{
    public vgaPanel() {
        setLayout(null);
        //setSize(400, 600);
        setPreferredSize(new Dimension(640, 300));
        setBackground(Color.RED);
    }
}

class regPanel extends JPanel{
    public regPanel() {
        setLayout(null);
        //setSize(400, 600);
        setPreferredSize(new Dimension(160, 300));
        setBackground(Color.GREEN);
    }
}

public class VLCTest {

    public static void main(String[] args) {
        Frame frame=new  JFrame();
        JPanel p=new JPanel();
        frame.setSize(300,200);
        frame.add(p);
        frame.setVisible(true);
        p.setBackground(Color.CYAN);
    }

}
