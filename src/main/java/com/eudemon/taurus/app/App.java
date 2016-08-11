package com.eudemon.taurus.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.eudemon.taurus.app.common.Config;

@SpringBootApplication
public class App {
	private static Logger logger =  LoggerFactory.getLogger(App.class);
	
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
		try {
			Config.initFromFile();
			Config.initFromDatabase();
		} catch (Exception e) {
			logger.error("App start exception", e);
		}
	}
}
