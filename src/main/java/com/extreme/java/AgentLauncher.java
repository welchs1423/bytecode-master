package com.extreme.java;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

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
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .disableClassFormatChanges()
                .with(new AgentBuilder.Listener.Adapter(){
                    @Override
                    public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType){
                        System.out.println("[성공] 바이트코드 변환 완료: " + typeDescription.getSimpleName());
                    }
                    @Override
                    public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable){
                        System.err.println("[에러] 변환 실패 (" + typeName + "): " + throwable.getMessage());
                    }
                })
                .type(nameStartsWith("com.extreme.java").or(nameStartsWith("org.h2.jdbc.JdbcPreparedStatement")))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder
                                .visit(Advice.to(TraceAdvice.class).on(isAnnotatedWith(Trace.class)))
                                .visit(Advice.to(TimerAdvice.class).on(isAnnotatedWith(Timer.class)))
                                .visit(Advice.to(LogDataAdvice.class).on(isAnnotatedWith(LogData.class)))
                                .visit(Advice.to(CatchErrorAdvice.class).on(isAnnotatedWith(CatchError.class)))
                                .visit(Advice.to(JdbcAdvice.class).on(named("executeQuery").or(named("executeUpdate"))))
                )
                .installOn(inst);
    }

// ==============================================================================
    // 💡 아래부터는 각 Advice들이 TraceContext에서 ID를 꺼내와서 로그에 같이 찍도록 수정되었습니다.
    // ==============================================================================

    public static class TraceAdvice {
        @Advice.OnMethodEnter
        public static void enter(){
            TraceContext.startTrace();
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        public static void exit(){
            TraceContext.clear();
        }
    }

    public static class TimerAdvice{
        @Advice.OnMethodEnter
        public static long enter() {
            return System.nanoTime();
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        public static void exit(@Advice.Origin("#m") String methodName, @Advice.Enter long start){
            String log = String.format("[TraceID: %s] [%s] 실행 시간: %.4fms", TraceContext.getTraceId(),methodName, (System.nanoTime() - start) / 1_000_000.0);
            DataSender.send(log);
        }
    }

    public static class LogDataAdvice {
        @Advice.OnMethodEnter
        public static void enter(
                @Advice.Origin("#m") String methodName,
                @Advice.AllArguments(readOnly = true, typing = net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC) Object[] args) {
            String log = String.format("[TraceID: %s] [%s] 입력값: %s", TraceContext.getTraceId(), methodName, java.util.Arrays.toString(args));
            DataSender.send(log);

        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        public static void exit(
                @Advice.Origin("#m") String methodName,
                @Advice.Return(typing = net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC) Object result) {
            String log = String.format("[TraceID: %s] [%s] 반환값: %s", TraceContext.getTraceId(), methodName, result != null ? result : "null");
            DataSender.send(log);

        }
    }

    public static class CatchErrorAdvice{
        @Advice.OnMethodExit(onThrowable = Throwable.class)
        public static void exit(@Advice.Origin("#m") String methodName, @Advice.Thrown Throwable thrown){
            if (thrown != null){
                String log = String.format("[TraceID: %s] [%s] 실행 중 예외 감지! 타입: %s, 메시지: %s", TraceContext.getTraceId(), methodName, thrown.getClass().getSimpleName(), thrown.getMessage());
                DataSender.send(log);
            }
        }
    }

    public static class JdbcAdvice {
        @Advice.OnMethodEnter
        public static long enter(){
            return System.nanoTime();
        }

        @Advice.OnMethodExit(onThrowable = Throwable.class)
        public static void exit(@Advice.This Object preparedStatement, @Advice.Enter long start){
            long duration = System.nanoTime() - start;
            String logTime = String.format("[TraceID: %s] [JDBC] 쿼리 실행 시간: %.4fms", TraceContext.getTraceId(), duration / 1_000_000.0);
            String logSql = String.format("[TraceID: %s] [JDBC] 실행된 SQL: %s", TraceContext.getTraceId(), preparedStatement.toString());

            DataSender.send(logTime);
            DataSender.send(logSql);
        }
    }
}