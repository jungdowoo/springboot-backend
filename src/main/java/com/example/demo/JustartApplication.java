package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@ComponentScan(basePackages = {"com.example.demo", "com.author","com.board.demo", "com.fileupload","com.security","com.artwork"})
@EnableJpaRepositories(basePackages = {"com.example.demo", "com.author", "com.board.demo","com.artwork"})
@EntityScan(basePackages = {"com.example.demo", "com.author", "com.board.demo","com.artwork"})
public class JustartApplication {
    public static void main(String[] args) {
        SpringApplication.run(JustartApplication.class, args);
    }
}