package com.t4m.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class T4mWebApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(T4mWebApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(T4mWebApplication.class, args);
	}

	@Value("${server.port}")
	private String port;

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		LOGGER.info("T4M has started, please open http://localhost:"+port+"/ in your browser.");
	}

}