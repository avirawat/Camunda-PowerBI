package com.camunda.powerbi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.camunda.zeebe.spring.client.EnableZeebeClient;

@SpringBootApplication
@EnableZeebeClient
public class PowerBIMainApplication {

  public static void main(String[] args) {
    SpringApplication.run(PowerBIMainApplication.class, args);
    System.out.println("Inside Main class");
  }

}
