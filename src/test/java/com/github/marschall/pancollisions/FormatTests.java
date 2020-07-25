package com.github.marschall.pancollisions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DecimalFormat;

import org.junit.jupiter.api.Test;

class FormatTests {

  @Test
  void doubles() {
    DecimalFormat formatter = new DecimalFormat("#0.0000");
    assertEquals("0.0001", formatter.format(0.00012d));
  }

}
