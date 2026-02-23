package com.extreme.java;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class AgentLauncher {

    public static void premain(String agentArgs, Instrumentation inst){
        init(inst, "premain");
    }

    public static void agentmain(String agentArgs, Instrumentation inst){
        init(inst, "agentmain");
    }

    public static void init(Instrumentation inst, String mode) {
        System.setProperty("net.bytebuddy.experimental", "true");

        System.out.println(" [" + mode + "] 에이전트가 JVM에 침투했습니다.");

        new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)   // 이미 로드된 클래스도 재변환 허용
                .disableClassFormatChanges()                    // 뼈대 변경 금지
                .with(new AgentBuilder.Listener.Adapter(){
                    @Override
                    public void onTransformation(net.bytebuddy.description.type.TypeDescription typeDescription, ClassLoader classLoader, net.bytebuddy.utility.JavaModule module, boolean loaded, net.bytebuddy.dynamic.DynamicType dynamicType){
                        System.out.println("[성공] 바이트코드 변환 완료: " + typeDescription.getSimpleName());
                    }
                    @Override
                    public void onError(String typeName, ClassLoader classLoader, net.bytebuddy.utility.JavaModule module, boolean loaded, Throwable throwable){
                        System.err.println("[에러] 변환 실패 (" + typeName + "): " + throwable.getMessage());
                    }
                })
                .type(ElementMatchers.nameStartsWith("com.extreme.java.TargetApp"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder
                                .visit(Advice.to(TimerAdvice.class).on(isAnnotatedWith(Timer.class)))
                                .visit(Advice.to(LogDataAdvice.class).on(isAnnotatedWith(LogData.class)))
                                .visit(Advice.to(CatchErrorAdvice.class).on(isAnnotatedWith(CatchError.class)))
                )
                .installOn(inst);
    }

    public static class TimerAdvice{
        @Advice.OnMethodEnter
        public static long enter() {
            return System.nanoTime();
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        public static void exit(@Advice.Origin("#m") String methodName, @Advice.Enter long start){
            System.out.printf("[%s] 실행 시간: %.4fms%n", methodName, (System.nanoTime() - start) / 1_000_000.0);
        }
    }

    public static class LogDataAdvice {
        @Advice.OnMethodEnter
        public static void enter(
                @Advice.Origin("#m") String methodName,
                @Advice.AllArguments(readOnly = true, typing = net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC) Object[] args) {
            System.out.printf("[%s] 입력값: %s%n", methodName, java.util.Arrays.toString(args));
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        public static void exit(
                @Advice.Origin("#m") String methodName,
                @Advice.Return(typing = net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC) Object result) {
            if (result != null) {
                System.out.printf("[%s] 반환값: %s%n", methodName, result);
            } else {
                System.out.printf("[%s] 반환값: null%n", methodName);
            }
        }
    }

    public static class CatchErrorAdvice{
        @Advice.OnMethodExit(onThrowable = Throwable.class)
        public static void exit(@Advice.Origin("#m") String methodName, @Advice.Thrown Throwable thrown){
            if (thrown != null){
                System.err.printf("[%s] 실행 중 예외 감지! 타입: %s, 메시지: %s%n", methodName, thrown.getClass().getSimpleName(),thrown.getMessage());
            }
        }
    }
}