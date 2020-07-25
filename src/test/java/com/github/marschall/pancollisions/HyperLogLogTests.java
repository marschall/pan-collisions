package com.github.marschall.pancollisions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

class HyperLogLogTests {

  @Test
  void m() {
    BigInteger m = BigInteger.TWO.pow(32); // we have 2^32 registers
    BigInteger mSquare = BigInteger.TWO.pow(64); // we have 2^32 registers
    assertEquals(mSquare, m.pow(2));
  }

  @Test
  void bits() {
    System.out.println(Integer.toHexString(Integer.MAX_VALUE));
    System.out.println(Integer.toHexString(Integer.MAX_VALUE / 2));
    System.out.println(Integer.toHexString(0b0011_1111_1111_1111_1111_1111_1111_1111));
  }

  @Test
  void fractions() {
    int leadingZeroes = 3;
    // 4 - 3 = 001
    // 001
    // 001 // 3
    // 010 // 2
    // 100 // 1
  }

}
