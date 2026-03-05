package com.extreme.java.springtargetapp;

import com.extreme.java.Timer;
import com.extreme.java.LogData;
import com.extreme.java.CatchError;
import com.extreme.java.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/test")
    @Timer
    @LogData
    @Trace
    public String testApi(@RequestParam String name, @RequestParam int age) throws InterruptedException {
        Thread.sleep(300);
        return "Hello, " + name + "! You are " + age + " years old.";
    }

    @Trace
    @GetMapping("/api/error")
    @CatchError
    public String errorApi(){
        throw new RuntimeException("스프링 부트 컨트롤러에서 의도적인 에러 발생!");
    }

    @Trace
    @GetMapping("/api/db")
    public String dbApi(@RequestParam String keyword){
        // H2 DB에 간단한 덧셈과 문자열 조합을 요청하는 진짜 SQL 쿼리임
        // 이 쿼리가 DB로 날아갈 때 우리 에이전트가 낚아챌 예정
        String sql = "SELECT CONCAT('DB 검색 결과: ', ?) FROM DUAL";

        return jdbcTemplate.queryForObject(sql, String.class, keyword);
    }
}
