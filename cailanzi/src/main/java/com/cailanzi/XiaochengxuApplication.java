package com.cailanzi;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * rabbitMQ自动配置
 * 1、RabbitAutoConfiguration
 * 2、有自动配置了连接工厂ConnectionFactory
 * 3、RabbitProperTies 封装了RabbitMQ的配置
 * 4、RabbitTemplate: 给RabbitMQ发送和接收消息
 * 5、AmqpAdmin: RabbitMQ系统管理功能组件
 */
@SpringBootApplication
@EnableAsync
@EnableRabbit//开启基于注解的监听
public class XiaochengxuApplication  extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(XiaochengxuApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(XiaochengxuApplication.class);
	}

	/**
	 * rabbitMQ的消息转换器
	 * @return
	 */
	@Bean
	public MessageConverter messageConverter(){
		return new Jackson2JsonMessageConverter();
	}

}
