package com.github.marschall.pancollisions;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A payment card number
 */
final class Pan {

  private final byte[] numbers;

  private Pan() {
    this.numbers = new byte[16];
  }

  private Pan(byte[] numbers) {
    if (numbers.length != 16) {
      throw new IllegalArgumentException("numbers length must be 16");
    }
    this.numbers = numbers;
    this.computeLuhn();
  }

  static Pan valueOf(CharSequence s) {
    if (s.length() > 16) {
      throw new IllegalArgumentException("input length must not be greater than 16");
    }
    // Java initializes array with 0
    byte[] numbers = new byte[16];
    for (int i = 0; i < s.length(); i++) {
      numbers[i] = (byte) (s.charAt(i) - '0');
    }
    return new Pan(numbers);
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder(16);
    for (byte b : this.numbers) {
      buffer.append(b);
    }
    return buffer.toString();
  }

  void increment() {

  }

  private void computeLuhn() {

  }

  Sha1Hasher createHasher() {
    return new Sha1Hasher();
  }

  static class Sha1Hasher {

    private MessageDigest messageDigest;


    Sha1Hasher() {
      try {
        this.messageDigest = MessageDigest.getInstance("SHA-1");
      } catch (NoSuchAlgorithmException e) {
        throw new IllegalStateException("SHA-1 not availalbe", e);
      }
    }

    I160 hash(Pan pan) {
      this.messageDigest.reset();
      byte[] digest = this.messageDigest.digest(pan.numbers);
      return I160.valueOf(digest);
    }

  }

}
