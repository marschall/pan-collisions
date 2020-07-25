package com.github.marschall.pancollisions;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.math.BigInteger;
import java.util.concurrent.atomic.LongAdder;

public final class HyperLogLog {

  // we have 2^32 registers
  // use the top two bits to identify the first array
  private static final int REGISTER_SHIFT = 30;

  // we have 2^32 registers
  // use the bottom 30 bits to identify the second array
  private static final int REGISTER_MASK = 0x3F_FF_FF_FF;

  private static final VarHandle ARRAY_HANDLE = MethodHandles.arrayElementVarHandle(byte[].class);

  private final byte[][] registers;

  private final LongAdder conflicts;

  public HyperLogLog() {
    this.registers = new byte[4][Integer.MAX_VALUE / 2];
    this.conflicts = new LongAdder();
  }

  public void add(BitAccessor value) {
    int registerAddress = value.getFirstBits();
    int leadingZeroes = value.getNumberOfLeadingZeroes();
    this.updateRegister(registerAddress, leadingZeroes + 1);
  }

  private void updateRegister(int registerAddress, int value) {
    int current = this.readValueFrom(registerAddress);
    while (current < value) {
      // if a concurrent update set the register to the same or a greater value
      // abort the update
      current = this.writeValueToRegister(registerAddress, current, value);
    }
  }

  private int readValueFrom(int registerAddress) {
    byte[] a = this.registers[registerAddress >>> REGISTER_SHIFT];
    // without varhandles
//    return Byte.toUnsignedInt(a[registerAddress & REGISTER_MASK]);
    // Byte.toUnsignedInt showed up during profiling
//    return Byte.toUnsignedInt((byte) ARRAY_HANDLE.getAcquire(a, registerAddress & REGISTER_MASK));
    byte b = (byte) ARRAY_HANDLE.getAcquire(a, registerAddress & REGISTER_MASK);
    return (b) & 0xFF;
  }

  private int writeValueToRegister(int registerAddress, int expected, int newValue) {
    byte[] a = this.registers[registerAddress >>> REGISTER_SHIFT];
    // a[registerAddress & 0x80_FF_FF_FF] = (byte) value;
    byte previous = (byte) ARRAY_HANDLE.compareAndExchangeRelease(a, registerAddress & REGISTER_MASK, (byte) expected, (byte) newValue);
    if (Byte.toUnsignedInt(previous) != expected) {
      // concurrent update
      this.conflicts.add(1L);
    }
    return Byte.toUnsignedInt(previous);
  }

  public BigInteger size() {
    ExponentRegister exponents = new ExponentRegister();
    for (byte[] a : this.registers) {
      for (byte register : a) {
        exponents.add(Byte.toUnsignedInt(register));
      }
    }
//    BigInteger m = BigInteger.TWO.pow(32); // we have 2^32 registers
    BigInteger mSquare = BigInteger.TWO.pow(64);
    // we don't multiply by am because we don't hash the input so we don't have to deal with hash collisions
    return BigInteger.ONE;
  }

  public long getAndResetConflicts() {
    return this.conflicts.sumThenReset();
  }

  static final class ExponentRegister {

    private final int[] values;

    ExponentRegister() {
      this.values = new int[4];
    }

    void add(int index) {
      int toAdd = 0;
      for (int i = 0; i < this.values.length; i++) {
        if (index < (32 * i)) {
          toAdd = 1 << (index % 32);
        }
        if (toAdd != 0) {
          long sum = Integer.toUnsignedLong(this.values[i]) + Integer.toUnsignedLong(toAdd);
          toAdd = (int) (sum >>> 32);
          this.values[i] = (int) (sum & 0xFF_FF_FF_FF);
        }
      }
    }

    BigInteger toBigInteger() {
      BigInteger value = BigInteger.ZERO;
      for (int i : this.values) {
        value = value.shiftLeft(32).add(BigInteger.valueOf(i));
      }
      return value;
    }

  }

}
