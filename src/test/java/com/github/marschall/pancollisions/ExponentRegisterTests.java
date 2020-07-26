package com.github.marschall.pancollisions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.marschall.pancollisions.HyperLogLog.ExponentRegister;

class ExponentRegisterTests {

  private ExponentRegister register;

  @BeforeEach
  void setUp() {
    this.register = new ExponentRegister();
  }

  @Test
  void addSimpleCarray() {
    this.register.add(0);
    this.register.add(31);
    this.register.add(31);
    assertEquals(BigInteger.valueOf(0b1_0000_0000_0000_0000_0000_0000_0000_0001L), this.register.toBigInteger());
  }

  @Test
  void addUnsignedInt() {
    for (int i = 0; i < 64; i++) {
      this.register.add(i);
    }
    assertEquals(BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE), this.register.toBigInteger());
  }

  @Test
  void addDoubleOverflowInt() {
    for (int i = 0; i < 64; i++) {
      this.register.add(i);
    }

    this.register.add(0);
    assertEquals(BigInteger.ONE.shiftLeft(64), this.register.toBigInteger());
  }

}
