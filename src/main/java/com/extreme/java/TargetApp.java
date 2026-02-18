package com.extreme.java;

public class TargetApp {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("[TargetApp] 메인 메서드 실행 시작!");

        TargetApp app = new TargetApp();
        app.work();

        app.processData("User123", 99);

        System.out.println("[TargetApp] 메인 메서드 종료!");
    }

    @Timer
    public void work() throws InterruptedException {
        Thread.sleep(500); // 측정을 위해 0.5초 대기
    }

    @LogData
    public String processData(String name, int score) {
        return "Processed: " + name + " (Score: " + score + ")";
    }
}