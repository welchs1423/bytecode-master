package com.extreme.java;

public class TargetApp {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("[TargetApp] 메인 메서드 실행 시작!");

        TargetApp app = new TargetApp();
        app.work();

        app.processData("User123", 99);

        try {
            app.riskyTask();
        } catch (RuntimeException e){
            System.out.println("[TargetApp] 메인에서 예외를 안전하게 처리했습니다.");
        }

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

    @CatchError
    public void riskyTask(){
        System.out.println("위험한 작업을 시작합니다...");
        throw new RuntimeException("DB 연결 시간 초과!");
    }
}