package com.lzq.samplecode.demo;
import java.awt.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
public class Test {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
//        Fram fram = new Fram();
        Frame frame=new  JFrame();
        JPanel p=new JPanel();
        frame.setSize(300,200);
        frame.add(p);
        frame.setVisible(true);
        p.setBackground(Color.CYAN);
    }

}
