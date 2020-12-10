package com.javeriana.vlcmulticast.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.javeriana.vlcmulticast.dto.MulticastMessage;

public class ObjectConverter {

  public static byte[] fromMessageToByteData(MulticastMessage message) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ObjectOutputStream os = new ObjectOutputStream(outputStream);
    os.writeObject(message);
    return outputStream.toByteArray();
  }

  public static MulticastMessage fromByteDataToMessage(byte[] data)
      throws IOException, ClassNotFoundException {
    ByteArrayInputStream in = new ByteArrayInputStream(data);
    ObjectInputStream is = new ObjectInputStream(in);
    return (MulticastMessage) is.readObject();
  }

  private ObjectConverter() {}

}
