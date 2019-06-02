package com.gem.auth;

import com.gem.auth.entities.User;
import com.gem.auth.services.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.time.Instant;
import java.util.List;

@SpringBootApplication
@ComponentScan("com.gem")
public class Starter {

    public static void main(String[] args) throws Exception {

        ConfigurableApplicationContext ctx = SpringApplication.run(Starter.class, args);


        UserService srv = ctx.getBean(UserService.class);
        List<User> list = srv.list("wot");

        System.out.println(list);

        User u = new User();
        u.setName("test2_0");
        u.setPass(DigestUtils.sha256Hex("test"));
        u.setCreatedBy("Alcantara, Yaison");
        u.setCreatedDate(Instant.now());

        srv.post(1, u);
    }


}
