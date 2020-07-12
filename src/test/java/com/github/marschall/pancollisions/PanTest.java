package com.github.marschall.pancollisions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PanTest {

  @Test
  void valueOf() {
    Pan pan = Pan.valueOf("5148530000");
    assertEquals("5148530000000001", pan.toString());
  }

  @Test
  void computeLuhn() {
    Pan pan = Pan.valueOf("7992739871");
    assertEquals("7992739871000003", pan.toString());
  }

  @Test
  void difference() {
    assertEquals(1L, Pan.valueOf("514853000000000").difference(Pan.valueOf("514853000000001")));
    assertEquals(11L, Pan.valueOf("514853000000000").difference(Pan.valueOf("514853000000011")));
    assertEquals(100000000000000L, Pan.valueOf("500000000000000").difference(Pan.valueOf("600000000000000")));
  }

}
