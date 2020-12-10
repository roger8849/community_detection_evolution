package com.javeriana.twitter.communitydetection.config;

import com.mongodb.MongoClient;
import java.io.IOException;
import java.util.Properties;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

@Configuration
@PropertySource("classpath:application.properties")
public class ApplicationConfig {

  @Value("${app.name:community-detection-evolution}")
  private String appName;

  @Value("${spark.home}")
  private String sparkHome;

  @Value("${master.uri:local}")
  private String masterUri;

  @Value("${spring.social.twitter.app-id}")
  private String consumerkey;

  @Value("${spring.social.twitter.app-secret}")
  private String consumerSecret;

  @Value("${twitter.access.token}")
  private String accessToken;

  @Value("${twitter.access.token.secret}")
  private String accessTokenSecret;

  @Bean
  public SparkConf sparkConf() {
    return new SparkConf().setAppName(this.appName).setSparkHome(this.sparkHome)
        .setMaster(this.masterUri);
  }

  @Bean
  public JavaSparkContext javaSparkContext() {
    return new JavaSparkContext(this.sparkConf());
  }

  @Bean
  public SparkSession sparkSession() {
    return SparkSession.builder().sparkContext(this.javaSparkContext().sc())
        .appName("community-detection-evolution").getOrCreate();
  }

  @Bean
  public Twitter getTwitter() {
    return new TwitterTemplate(consumerkey, consumerSecret, accessToken, accessTokenSecret);
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public static StanfordCoreNLP stanfordCoreNLP() throws IOException {
    Properties props = new Properties();
    props.load(IOUtils.readerFromString("StanfordCoreNLP-spanish.properties"));
    props.get("tokenize.language");
    return new StanfordCoreNLP(props);
  }

  public @Bean
  MongoTemplate mongoTemplate() throws Exception {
    MongoTemplate mongoTemplate = new MongoTemplate(new MongoClient("127.0.0.1"), "dyec");
    return mongoTemplate;

  }
}
