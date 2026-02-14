package com.extreme.java;

public class TargetApp {
    public static void main(String[] args) throws InterruptedException{
        System.out.println("[TargetApp] 메인 메서드 실행 시작!");
        TargetApp app = new TargetApp();
        app.work();
        System.out.println("[TargetApp] 메인 메서드 종료!");
    }

    public void work() throws InterruptedException{
        Thread.sleep(500);
    }
}
