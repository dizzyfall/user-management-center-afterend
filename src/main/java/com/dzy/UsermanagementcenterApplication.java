package com.dzy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class UsermanagementcenterApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(UsermanagementcenterApplication.class, args);
/*		String[] beanDefinitionNames = run.getBeanDefinitionNames();
		for (String beanDefinitionName : beanDefinitionNames) {
			System.out.println(beanDefinitionName);
		}*/
    }

}
