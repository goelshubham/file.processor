package com.file.processor;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws IOException {
		ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
		List<Data> data = applicationContext.getBean(DataReader.class).loadFiles();
		System.out.println(data);
	}

}
