package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@SpringBootApplication
public class WebsocketDemoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(WebsocketDemoApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WebsocketDemoApplication.class);
	}

	@Bean
	public ServerEndpointExporter serverEndpointExporter(){
		return new ServerEndpointExporter();
	}
}
