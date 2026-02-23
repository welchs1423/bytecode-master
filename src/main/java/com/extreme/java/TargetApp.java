package com.extreme.java;

public class TargetApp {
    public static void main(String[] args) throws InterruptedException {

        long pid = ProcessHandle.current().pid();
        System.out.println("[TargetApp] 서버 가동 완료! 현재 PID: " + pid);
        System.out.println("[TargetApp] 에이전트 주입을 대기하며 루프를 돕니다...\n");

        TargetApp app = new TargetApp();

        // 무한 루프로 3초마다 작업 실행 (에이전트가 중간에 붙으면 이때부터 로그가 출력됨)
        while(true){
            app.work();
            app.processData("User123",99);
            try{ app.riskyTask(); } catch (Exception ignored) {}

            Thread.sleep(3000);
            System.out.println("--- 3초 대기 ---");
        }
    }

    @Timer
    public void work() throws InterruptedException { Thread.sleep(500); }

    @LogData
    public String processData(String name, int score) { return "Processed: " + name; }

    @CatchError
    public void riskyTask() { throw new RuntimeException("DB 연결 시간 초과!"); }
}