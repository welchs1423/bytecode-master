package com.extreme.java;

import net.bytebuddy.agent.ByteBuddyAgent;
import java.io.File;

public class DynamicAttacher {
    public static void main(String[] args){
        if (args.length < 1){
            System.out.println("사용법: 타겟PID를 입력해주세요.");
            return;
        }

        String targetPid = args[0];
        File agentJar = new File("build/libs/bytecode-master-1.0-SNAPSHOT.jar");

        System.out.println("대상 PID [" + targetPid + "] 에 에이전트 침투를 시도합니다...");

        try {
            ByteBuddyAgent.attach(agentJar, targetPid);
            System.out.println("침투 완료! 타겟 앱의 콘솔 로그를 확인하세요.");
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
