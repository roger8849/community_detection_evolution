package com.javeriana.vlcmulticast.dto;

import java.io.Serializable;
import java.util.UUID;

import com.javeriana.vlcmulticast.enumeration.MessageType;

public class MulticastMessage implements Serializable {
  /** */
  private static final long serialVersionUID = 2348465134494597549L;

  private UUID messageId;
  private MessageType messageType;
  private String vlcCommand;

  public MulticastMessage(UUID messageId, MessageType messageType, String vlcCommand) {
    super();
    this.messageId = messageId;
    this.messageType = messageType;
    this.vlcCommand = vlcCommand;
  }

  public UUID getMessageId() {
    if (this.messageId == null) {
      this.messageId = UUID.randomUUID();
    }
    return messageId;
  }

  public void setMessageId(UUID messageId) {
    this.messageId = messageId;
  }

  public MessageType getMessageType() {
    return messageType;
  }

  public void setMessageType(MessageType messageType) {
    this.messageType = messageType;
  }

  public String getVlcCommand() {
    return vlcCommand;
  }

  public void setVlcCommand(String vlcCommand) {
    this.vlcCommand = vlcCommand;
  }

}
