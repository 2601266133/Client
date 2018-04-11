package com.cisco.client;

import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableTransactionManagement
public class AppClient extends SpringBootServletInitializer {

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		//// 设置文件大小限制 ,超了，页面会抛出异常信息，这时候就需要进行异常信息的处理了;
		factory.setMaxFileSize("1024MB"); // KB,MB
		/// 设置总上传数据总大小
		factory.setMaxRequestSize("1152MB");
		// Sets the directory location wherefiles will be stored.
		// factory.setLocation("C:\\Users\\jacsong2\\Desktop\\PPTconvert");
		return factory.createMultipartConfig();
	}

	public static void main(String[] args) {
		SpringApplication.run(AppClient.class, args);
		System.out.println("Hello World!");
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(AppClient.class);
	}
}
