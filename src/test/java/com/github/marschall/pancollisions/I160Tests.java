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

    assertEquals(0b1010101011111111000000001111111, i160.getFirstBits(31));
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
    assertEquals(0, i160.getNumberOfLeadingZeroes(31));

    i160 = I160.valueOf(new byte[] {
        (byte) 0b10101010,
        (byte) 0b11111111,
        (byte) 0b00000000,
        (byte) 0b11111110,

        (byte) 0b01101010,
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
    assertEquals(2, i160.getNumberOfLeadingZeroes(31));

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
        (byte) 0b00000010,

        (byte) 0b00000000,
        (byte) 0b00000000,
        (byte) 0b00000000,
        (byte) 0b00000000,

        (byte) 0b00000000,
        (byte) 0b00000000,
        (byte) 0b00000000,
        (byte) 0b00000000,
    });
    assertEquals(2, i160.getNumberOfLeadingZeroes(31));

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

        (byte) 0b00000000,
        (byte) 0b00000000,
        (byte) 0b00000000,
        (byte) 0b00000000,

        (byte) 0b00000000,
        (byte) 0b00000000,
        (byte) 0b00000000,
        (byte) 0b00000010,
    });
    assertEquals(2, i160.getNumberOfLeadingZeroes(63));
  }

}
