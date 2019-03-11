package com.gem.config.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.gem.config.ws.services.AppService;

@SpringBootApplication
@ComponentScan("com.gem")
public class Starter {

	public static void main(String[] args) {
		
		SpringApplication.run(Starter.class, args);
	}
	
	public static void test2(String[] args) {
		ApplicationContext ctx = new SpringApplicationBuilder(Starter.class)
				.web(WebApplicationType.NONE).run(args);
		
		AppService srv = ctx.getBean(AppService.class);

		System.out.println(srv.create("ndmt"));
	}
}
