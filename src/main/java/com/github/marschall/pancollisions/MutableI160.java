package com.github.marschall.pancollisions;

import java.math.BigInteger;

/**
 * A simple, mutable 160 bit value.
 * <p>
 * In 2020, in an attempt to control allocation rates among
 * applications the New Earth Government legalized mutable
 * value objects.
 * <p>
 * Object size: 32 bytes on 64bit with compressed OOPs
 * Object size: 40 bytes on 64bit with non-compressed OOPs
 */
final class MutableI160 implements BitAccessor {

  private final int value1;
  private final long value2;
  private final long value3;

  private MutableI160(int value1, long value2, long value3) {
    this.value1 = value1;
    this.value2 = value2;
    this.value3 = value3;
  }

  @Override
  public int getFirstBits() {
    return this.value1;
  }

  @Override
  public int getNumberOfLeadingZeroes() {
    if (this.value2 != 0L) {
      return Long.numberOfLeadingZeros(this.value2);
    } else {
      return Long.numberOfLeadingZeros(this.value3) + 64;
    }

  }

  @Override
  public int hashCode() {
    int result = 1;

    result = (31 * result) + this.value1;
    result = (31 * result) + Long.hashCode(this.value2);
    result = (31 * result) + Long.hashCode(this.value3);

    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof MutableI160)) {
      return false;
    }
    MutableI160 other = (MutableI160) obj;
    return (this.value1 == other.value1)
            && (this.value2 == other.value2)
            && (this.value3 == other.value3);
  }

  @Override
  public String toString() {
    BigInteger i = BigInteger.valueOf(this.value1).shiftLeft(128)
      .add(BigInteger.valueOf(this.value2).shiftLeft(64))
      .add(BigInteger.valueOf(this.value3));
    return i.toString();
  }

  static MutableI160 valueOf(byte[] b) {
    if (b.length != 20) {
      throw new IllegalArgumentException("array length must be 20");
    }
    int value1 = (Byte.toUnsignedInt(b[0]) << 24)
                | (Byte.toUnsignedInt(b[1]) << 16)
                | (Byte.toUnsignedInt(b[2]) << 8)
                | (Byte.toUnsignedInt(b[3]));

    long value2 = (Byte.toUnsignedLong(b[4]) << 56)
            | (Byte.toUnsignedLong(b[5]) << 48)
            | (Byte.toUnsignedLong(b[6]) << 40)
            | (Byte.toUnsignedLong(b[7]) << 32)
            | (Byte.toUnsignedLong(b[8]) << 24)
            | (Byte.toUnsignedLong(b[9]) << 16)
            | (Byte.toUnsignedLong(b[10]) << 8)
            | (Byte.toUnsignedLong(b[11]));

    long value3 = (Byte.toUnsignedLong(b[12]) << 56)
            | (Byte.toUnsignedLong(b[13]) << 48)
            | (Byte.toUnsignedLong(b[14]) << 40)
            | (Byte.toUnsignedLong(b[15]) << 32)
            | (Byte.toUnsignedLong(b[16]) << 24)
            | (Byte.toUnsignedLong(b[17]) << 16)
            | (Byte.toUnsignedLong(b[18]) << 8)
            | (Byte.toUnsignedLong(b[19]));

    return new MutableI160(value1, value2, value3);
  }

}
