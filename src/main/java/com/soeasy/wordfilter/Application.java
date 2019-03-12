package com.soeasy.wordfilter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan
//@MapperScan("cn.com.cootoo.mapper")//将项目中对应的mapper类的路径加进来就可以了
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
