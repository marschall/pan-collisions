package com.github.marschall.pancollisions;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

class I160Test {

  @Test
  void objectSize() {
    System.out.println(VM.current().details());
    System.out.println(ClassLayout.parseClass(I160.class).toPrintable());
  }

}
