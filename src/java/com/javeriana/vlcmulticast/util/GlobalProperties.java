package com.javeriana.vlcmulticast.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
@ConfigurationProperties("multicast")
public class GlobalProperties {
  private String inetAddress;
  private String inetPort;

  public String getInetAddress() {
    return inetAddress;
  }

  public void setInetAddress(String inetAddress) {
    this.inetAddress = inetAddress;
  }

  public String getInetPort() {
    return inetPort;
  }

  public void setInetPort(String inetPort) {
    this.inetPort = inetPort;
  }

}
