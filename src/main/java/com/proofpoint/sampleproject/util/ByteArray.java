package com.proofpoint.sampleproject.util;

import org.apache.commons.codec.digest.DigestUtils;

public class ByteArray {
  private byte[] bytes;

  public ByteArray(String string) {
    this.bytes = string.getBytes();
  }

  public ByteArray(byte[] bytes) {
    this.bytes = bytes;
  }

  public ByteArray(int length) {
    bytes = Random.getRandomByteArray(length);
  }

  public String getSHA256Hex() {
    return DigestUtils.sha256Hex(bytes);
  }

  public byte[] getByteArray() {
    return bytes;
  }

  public String toString() {
    return bytes.toString();
  }
}
