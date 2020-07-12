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
      char c = s.charAt(i);
      if ((c < '0') || (c > '9')) {
        throw new IllegalArgumentException("invalid character");
      }
      numbers[i] = (byte) (c - '0');
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

  long difference(Pan other) {
    long difference = 0L;
    // ignore Luhn
    for (int i = 0; i < (this.numbers.length - 1); i++) {
      difference *= 10L;
      difference += other.numbers[i] - this.numbers[i];
    }
    return difference;
  }

  void increment() {

  }

  private void computeLuhn() {
    int sum = 0;
    // we know length is 16 so start at the beginning
    for (int i = 0; i < 16 ; ++i) {
      int digit = this.numbers[i];
      // we know length is 16
      if ((i % 2) != 0) {
        digit = digit * 2;
      }
      if (digit > 9) {
        digit = digit - 9;
      }
      sum = sum + digit;
    }
    this.numbers[15] = (byte) ((sum * 9) % 10);
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
