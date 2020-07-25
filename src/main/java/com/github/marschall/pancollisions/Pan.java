package com.github.marschall.pancollisions;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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
    for (int i = 14; i >= 0; i--) {
      if (this.numbers[i] < 9) {
        this.numbers[i] += 1;
        Arrays.fill(this.numbers, i + 1, this.numbers.length, (byte) 0);
        this.computeLuhn();
        return;
      }
    }
    throw new IllegalStateException("overflow");
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



  @Override
  public int hashCode() {
    return Arrays.hashCode(this.numbers);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Pan)) {
      return false;
    }
    Pan other = (Pan) obj;
    return Arrays.equals(this.numbers, other.numbers);
  }

  Hasher createHasher() {
    return new Sha1Hasher();
//    return new CustomHahser();
  }

  MutableHasher createMutableHasher() {
    return new MutableHasher();
  }

  static final class CustomHahser implements Hasher {

    private static final int[] X = new int[] {83952648, 84082688, 0, 1, -2147483648, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    CustomHahser() {
      super();
    }

    @Override
    public I160 hash(Pan pan) {
      int[] w = new int[80];
      w[0] = (Byte.toUnsignedInt(pan.numbers[0]) << 24)
              | (Byte.toUnsignedInt(pan.numbers[1]) << 16)
              | (Byte.toUnsignedInt(pan.numbers[2]) << 8)
              | Byte.toUnsignedInt(pan.numbers[3]);
      w[1] = (Byte.toUnsignedInt(pan.numbers[4]) << 24)
              | (Byte.toUnsignedInt(pan.numbers[5]) << 16)
              | (Byte.toUnsignedInt(pan.numbers[6]) << 8)
              | Byte.toUnsignedInt(pan.numbers[7]);
      w[2] = (Byte.toUnsignedInt(pan.numbers[8]) << 24)
              | (Byte.toUnsignedInt(pan.numbers[9]) << 16)
              | (Byte.toUnsignedInt(pan.numbers[10]) << 8)
              | Byte.toUnsignedInt(pan.numbers[11]);
      w[3] = (Byte.toUnsignedInt(pan.numbers[12]) << 24)
              | (Byte.toUnsignedInt(pan.numbers[13]) << 16)
              | (Byte.toUnsignedInt(pan.numbers[14]) << 8)
              | Byte.toUnsignedInt(pan.numbers[15]);
      w[4] = 0x80_00_00_00;
      w[15] = 16 * 8;

      int mismatch = Arrays.mismatch(w, X);

      for (int i = 16; i < 80; i++) {
        w[i] = Integer.rotateLeft((w[i-3] ^ w[i-8] ^ w[i-14] ^ w[i-16]), 1);
      }


      return null;
    }

  }

  static final class Sha1Hasher implements Hasher {
    // https://stackoverflow.com/questions/21107350/how-can-i-access-sha-intrinsic

    private static final int BUFFER_SIZE = 160 / 8;
    private final MessageDigest messageDigest;
    private final byte[] outputBuffer;


    Sha1Hasher() {
      try {
        this.messageDigest = MessageDigest.getInstance("SHA-1");
//        this.messageDigest = MessageDigest.getInstance("SHA-1", "BC");
      } catch (NoSuchAlgorithmException e) {
        throw new IllegalStateException("SHA-1 not availalbe", e);
      }
      this.outputBuffer = new byte[BUFFER_SIZE];
    }

    @Override
    public I160 hash(Pan pan) {
      this.messageDigest.update(pan.numbers);
      try {
        this.messageDigest.digest(this.outputBuffer, 0, BUFFER_SIZE);
      } catch (DigestException e) {
        throw new RuntimeException("could not digest message", e);
      }
      return I160.valueOf(this.outputBuffer);
    }

  }

  static class MutableHasher {

    private static final int BUFFER_SIZE = 160 / 8;
    private final MessageDigest messageDigest;
    private final byte[] outputBuffer;
    private final MutableI160 hash;


    MutableHasher() {
      try {
        this.messageDigest = MessageDigest.getInstance("SHA-1");
      } catch (NoSuchAlgorithmException e) {
        throw new IllegalStateException("SHA-1 not availalbe", e);
      }
      this.outputBuffer = new byte[BUFFER_SIZE];
      this.hash = new MutableI160();
    }

    MutableI160 hash(Pan pan) {
      this.messageDigest.update(pan.numbers);
      try {
        this.messageDigest.digest(this.outputBuffer, 0, BUFFER_SIZE);
      } catch (DigestException e) {
        throw new RuntimeException("could not digest message", e);
      }
      this.hash.setValue(this.outputBuffer);
      return this.hash;
    }

  }

}
