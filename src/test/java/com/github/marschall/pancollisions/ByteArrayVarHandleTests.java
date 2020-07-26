package com.github.marschall.pancollisions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import org.junit.jupiter.api.Test;

class ByteArrayVarHandleTests {

  private static final VarHandle ARRAY_HANDLE = MethodHandles.arrayElementVarHandle(byte[].class);

  @Test
  void compareAndExchange() {
    byte[] a = new byte[16];
    byte expectedValue = 0;
    byte newValue = 1;
    byte witness = (byte) ARRAY_HANDLE.compareAndExchangeAcquire(a, 0, expectedValue, newValue);
    assertEquals(witness, 0);
  }

}
