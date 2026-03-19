package com.example.javaPractice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@MapperScan("com/example/javaPractice/mapper")
@ServletComponentScan
public class JavaPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaPracticeApplication.class, args);
	}

}
