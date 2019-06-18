package com.gem.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.gem")
public class Starter {

    public static void main(String[] args) throws Exception {


        ConfigurableApplicationContext ctx = SpringApplication.run(Starter.class, args);

    }


}
