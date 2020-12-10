package com.javeriana.twitter.communitydetection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication
@org.springframework.context.annotation.ComponentScan("com.javeriana.twitter.communitydetection")
@EnableAutoConfiguration(
    exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class})
public class Startup {

  public static final Logger LOG = LoggerFactory.getLogger(Startup.class);

  public static void main(String[] args) throws Exception {

    System.setProperty("hadoop.home.dir", "C:\\apps\\apache\\spark-2.2.0-bin-hadoop2.7");
    SpringApplication app = new SpringApplication(Startup.class);
    app.run(args);

  }

  public void run(String... args) throws Exception {}
}
