package com.cailanzi;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@SpringBootApplication
@EnableWebSocket
public class CailanziWebsocketApplication  extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CailanziWebsocketApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CailanziWebsocketApplication.class);
	}

	/**
	 * rabbitMQ的消息转换器
	 * @return
	 */
	@Bean
	public MessageConverter messageConverter(){
		return new Jackson2JsonMessageConverter();
	}

	/**
	 * 此处需要注意，仅使用Application文件启动项目才使用此配置。如果使用本地tomcat启动项目，无需进行此步配置，
	 * 否则会产生java.lang.IllegalStateException: Failed to register @ServerEndpoint class的错误
	 */
	@Bean
	public ServerEndpointExporter serverEndpointExporter(){
		return new ServerEndpointExporter();
	}


}
