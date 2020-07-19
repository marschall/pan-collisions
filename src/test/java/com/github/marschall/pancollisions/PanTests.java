package com.github.marschall.pancollisions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

class PanTests {

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

  @Test
  void increment() {
    Pan pan = Pan.valueOf("514853000000000");
    pan.increment();
    assertEquals(Pan.valueOf("514853000000001"), pan);

    pan = Pan.valueOf("514853000000009");
    pan.increment();
    assertEquals(Pan.valueOf("514853000000010"), pan);

    pan = Pan.valueOf("514853999999999");
    pan.increment();
    assertEquals(Pan.valueOf("5148540000000000"), pan);
  }

  @Test
  void hash() {
    Security.addProvider(new BouncyCastleProvider());
    Pan pan = Pan.valueOf("5148530000");
    Hasher hasher = pan.createHasher();
    I160 hash = hasher.hash(pan);
    assertNotNull(hash);
    assertEquals(I160.valueOf(new byte[] {107, -86, -27, 56, 17, 45, -37, 45, 118, -61, 48, 98, 90, -16, 53, 9, 106, -71, -7, -65}), hash);
  }

}
