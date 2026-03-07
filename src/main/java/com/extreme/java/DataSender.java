package com.extreme.java;

import java.util.concurrent.LinkedBlockingQueue;

public class DataSender {
    // 데이터를 안전하게 담아둘 무제한 우체통 (스레드 세이프 큐)
    private static final LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    static {
        // 백그라운드에서 우체통을 확인하고 데이터를 전송할 우체부 스레드 생성
        Thread senderThread = new Thread(() -> {
            while(true) {
                try {
                    // 큐에 데이터가 들어올 때 까지 대기하다가 빼옴 (메인 스레드 부하 제로)
                    String message = messageQueue.take();

                    System.out.println("[비동기 전송 스레드 - " + Thread.currentThread().getName() + "] 데이터 수집 서버로 전송 -> " + message);
                } catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        // 스프링 부트가 꺼질 때 이 스레드도 같이 꺼지도록 데몬 스레드로 설정
        senderThread.setDaemon(true);
        senderThread.setName("APM-DataSender-Thread");
        senderThread.start();
    }

    public static void send(String message) {
        messageQueue.offer(message);
    }
}
