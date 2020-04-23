package org.hanrw.jvm.jmm;

/**
 * @author hanrw
 * @date 2020/4/23 2:04 PM
 */
public class VolatileTest {

  private volatile int sum;

  public static void main(String[] args) {
    VolatileTest volatileTest = new VolatileTest();
    volatileTest.start();
  }

  void start() {
    sum = 1;
  }
}
