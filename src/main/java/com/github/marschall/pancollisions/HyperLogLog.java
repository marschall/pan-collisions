package com.github.marschall.pancollisions;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.math.BigInteger;

public final class HyperLogLog {

  private static final VarHandle ARRAY_HANDLE = MethodHandles.arrayElementVarHandle(byte[].class);

  private final byte[][] registers;
  private final int registerBits;

  public HyperLogLog(int registerBits) {
    this.registerBits = registerBits;
    this.registers = new byte[2][Integer.MAX_VALUE];
  }

  public void add(BitAccessor value) {
    int registerAddress = value.getFirstBits();
    int leadingZeroes = value.getNumberOfLeadingZeroes();
    this.updateRegister(registerAddress, leadingZeroes + 1);
  }

  void updateRegister(int registerAddress, int value) {
    int current = this.readValueFrom(registerAddress);
    int max = Math.max(current, value);
    if (max > value) {
      this.writeValueToRegister(registerAddress, max);
    }
  }

  private int readValueFrom(int registerAddress) {
    byte[] a = this.registers[registerAddress >> 31];
    return Byte.toUnsignedInt(a[registerAddress & 0x80_FF_FF_FF]);
  }

  private void writeValueToRegister(int registerAddress, int value) {
    byte[] a = this.registers[registerAddress >> 31];
    a[registerAddress & 0x80_FF_FF_FF] = (byte) value;
  }

  public BigInteger size() {
    return BigInteger.ONE;
  }

}
