package com.extreme.java;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.LinkedBlockingQueue;

public class DataSender {
    private static final LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    // 1. HTTP 통신을 담당할 클라이언트 생성
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    static {
        Thread senderThread = new Thread(() -> {
            while (true) {
                try {
                    String message = messageQueue.take();

                    // 2. 수집 서버가 읽기 편하도록 JSON 형식으로 변환
                    String jsonPayload = String.format("{\"log\": \"%s\"}", message.replace("\"", "\\\""));

                    // 3. 노드 서버(3000번 포트)로 POST 요청 생성
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:3000/collect"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                            .build();

                    // 4. 비동기 전송 (결과를 기다리지 않고 바로 다음 메시지 처리)
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    // 서버가 꺼져있어도 앱이 멈추지 않게 예외 처리
                }
            }
        });

        senderThread.setDaemon(true);
        senderThread.setName("APM-HttpSender-Thread");
        senderThread.start();
    }

    public static void send(String message) {
        messageQueue.offer(message);
    }
}