package com.github.marschall.pancollisions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PanTest {

  @Test
  void valueOf() {
    Pan pan = Pan.valueOf("5148530000");
    assertEquals("5148530000000000", pan.toString());
  }

}
