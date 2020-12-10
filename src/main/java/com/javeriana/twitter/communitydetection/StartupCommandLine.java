package com.javeriana.twitter.communitydetection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication
@org.springframework.context.annotation.ComponentScan("com.javeriana.twitter.communitydetection")
@EnableAutoConfiguration(
    exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class StartupCommandLine implements CommandLineRunner {

  public static final Logger LOG = LoggerFactory.getLogger(StartupCommandLine.class);

  public static void main(String[] args) throws Exception {
    SpringApplication app = new SpringApplication(StartupCommandLine.class);
    app.run(args);

  }

  @Override
  public void run(String... args) throws Exception {


  }
}
