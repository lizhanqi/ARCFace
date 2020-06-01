package com.lzq.faceserver;

public class App {
    public static void main(String[] args) {
        String name = Thread.currentThread().getName();
        System.out.println("入口线程"+name);
        UIFace uiFace = new UIFace();
        uiFace.start();
    }
}
