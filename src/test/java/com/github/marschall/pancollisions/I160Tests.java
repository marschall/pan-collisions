package com.github.marschall.pancollisions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

class I160Tests {

  @Test
  void objectSize() {
    System.out.println(VM.current().details());
    System.out.println(ClassLayout.parseClass(I160.class).toPrintable());
  }

  @Test
  void getFirstBits() {
    I160 i160 = I160.valueOf(new byte[] {
        (byte) 0b10101010,
        (byte) 0b11111111,
        (byte) 0b00000000,
        (byte) 0b11111110,

        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,

        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,

        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,

        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,
    });

    assertEquals(0b10101010111111110000000011111110, i160.getFirstBits());
  }

  @Test
  void getNumberOfLeadingZeroes() {
    I160 i160 = I160.valueOf(new byte[] {
        (byte) 0b00000000,
        (byte) 0b00000000,
        (byte) 0b00000000,
        (byte) 0b00000001,

        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,

        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,

        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,

        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,
    });
    assertEquals(0, i160.getNumberOfLeadingZeroes());

    i160 = I160.valueOf(new byte[] {
        (byte) 0b10101010,
        (byte) 0b11111111,
        (byte) 0b00000000,
        (byte) 0b11111110,

        (byte) 0b00000000,
        (byte) 0b00000000,
        (byte) 0b00000000,
        (byte) 0b00000000,

        (byte) 0b00000000,
        (byte) 0b00000000,
        (byte) 0b00000000,
        (byte) 0b00000000,

        (byte) 0b00101010,
        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,

        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,
        (byte) 0b10101010,
    });
    assertEquals(66, i160.getNumberOfLeadingZeroes());
  }

}
