package com.nvbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.nvbank.dao.DefaultBankUserDao;
import com.nvbank.dao.BankUserDao;

import com.nvbank.dao.DefaultActiveSessionDao;
import com.nvbank.dao.ActiveSessionDao;

import com.nvbank.dao.DefaultAccountDao;
import com.nvbank.dao.AccountDao;

@SpringBootApplication
public class SpringBootWebApplication extends SpringBootServletInitializer {
	
	@Bean
    protected BankUserDao bankUserDao() {
        return new DefaultBankUserDao();
    }
	
	@Bean
	protected ActiveSessionDao activeSessionDao() {
		return new DefaultActiveSessionDao();
	}
	
	@Bean
	protected AccountDao accountDao() {
		return new DefaultAccountDao();
	}
    
    @Bean
    WebMvcConfigurer configurer () {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addResourceHandlers (ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/static/**").
                          addResourceLocations("classpath:/");
            }
        };
    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringBootWebApplication.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }

}
