package com.lzq.faceserver;

import javax.swing.*;
import java.awt.*;

public class UITest {
    public static void main(String[] args) {
        int gap = 10;
        final JFrame window = new JFrame("soler");

        window.setSize(410, 400);
        window.setLocation(200, 200);
        window.setLayout(null);
        JPanel pInput = new JPanel();
        pInput.setBackground(Color.CYAN);
        pInput.setBounds(0, 0, 30, 30);
        pInput.setLayout(new GridLayout(4, 3, gap, gap));
        final JLabel status = new JLabel("");
        pInput.add(status);
        JButton btnSave = new JButton("设置");
        btnSave.setBounds(0, 0, 80, 30);
        JButton btnStart = new JButton("启动");
//        btnStart.setBounds(0, 0+30, 80, 30);
        window.add(pInput);
        window.add(btnSave);
        window.add(btnStart);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
}
