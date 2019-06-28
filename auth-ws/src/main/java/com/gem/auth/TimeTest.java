package com.gem.auth;

import java.time.Instant;
import java.util.Date;

public class TimeTest {

    public static void main(String[] args) {
        long now = Instant.now().toEpochMilli();
        long d = new Date().getTime();

        System.out.println(now);
        System.out.println(d);
        System.out.println(new Date());
    }
}
