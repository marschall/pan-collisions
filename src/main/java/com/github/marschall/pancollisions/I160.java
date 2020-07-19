package com.github.marschall.pancollisions;

/**
 * A simple 160 bit value.
 * <p>
 * Object size: 32 bytes on 64bit with compressed OOPs
 * Object size: 40 bytes on 64bit with non-compressed OOPs
 */
final class I160 {

  private final long value1;
  private final long value2;
  private final int value3;

  private I160(long value1, long value2, int value3) {
    this.value1 = value1;
    this.value2 = value2;
    this.value3 = value3;
  }

  @Override
  public int hashCode() {
    int result = 1;

    result = (31 * result) + Long.hashCode(this.value1);
    result = (31 * result) + Long.hashCode(this.value2);
    result = (31 * result) + this.value3;

    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof I160)) {
      return false;
    }
    I160 other = (I160) obj;
    return (this.value1 == other.value1)
            && (this.value2 == other.value2)
            && (this.value3 == other.value3);
  }

  static I160 valueOf(byte[] b) {
    if (b.length != 20) {
      throw new IllegalArgumentException("array length must be 20");
    }
    long value1 = (Byte.toUnsignedLong(b[0]) << 56)
                | (Byte.toUnsignedLong(b[1]) << 48)
                | (Byte.toUnsignedLong(b[2]) << 40)
                | (Byte.toUnsignedLong(b[3]) << 32)
                | (Byte.toUnsignedLong(b[4]) << 24)
                | (Byte.toUnsignedLong(b[5]) << 16)
                | (Byte.toUnsignedLong(b[6]) << 8)
                | Byte.toUnsignedLong(b[7]);

    long value2 = (Byte.toUnsignedLong(b[8]) << 56)
            | (Byte.toUnsignedLong(b[9]) << 48)
            | (Byte.toUnsignedLong(b[10]) << 40)
            | (Byte.toUnsignedLong(b[11]) << 32)
            | (Byte.toUnsignedLong(b[12]) << 24)
            | (Byte.toUnsignedLong(b[13]) << 16)
            | (Byte.toUnsignedLong(b[14]) << 8)
            | Byte.toUnsignedLong(b[15]);

    int value3 = (Byte.toUnsignedInt(b[16]) << 24)
            | (Byte.toUnsignedInt(b[17]) << 16)
            | (Byte.toUnsignedInt(b[18]) << 8)
            | Byte.toUnsignedInt(b[19]);

    return new I160(value1, value2, value3);
  }

}
