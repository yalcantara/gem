package com.gem.config.ws;

import javax.inject.Inject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import com.gem.config.ws.services.AppService;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class Starter {

	@Inject
	private AppService srv;
	
	public static void main(String[] args) {

		SpringApplication.run(Starter.class, args);
	}

	public static void test2(String[] args) {
		ApplicationContext ctx = new SpringApplicationBuilder(Starter.class)
				.web(WebApplicationType.NONE).run(args);

		Starter srv = ctx.getBean(Starter.class);
		
		System.out.println(srv.srv.create("ndmt"));
	}
}
