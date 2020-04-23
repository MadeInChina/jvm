package org.hanrw.jvm.jmm;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hanrw
 * @date 2020/4/23 2:04 PM
 */
public class AtomicIntegerTest {
  AtomicInteger atomicInteger = new AtomicInteger();
  static CountDownLatch latch = new CountDownLatch(3);
  public static void main(String[] args) throws InterruptedException {
    AtomicIntegerTest atomicIntegerTest = new AtomicIntegerTest();

    new Thread(
            () -> {
              for (int i = 0; i < 10000; i++) {
                atomicIntegerTest.atomicInteger.incrementAndGet();
              }
              latch.countDown();
            })
        .start();

    new Thread(
            () -> {
              for (int i = 0; i < 10000; i++) {
                atomicIntegerTest.atomicInteger.incrementAndGet();
              }
              latch.countDown();
            })
        .start();

    new Thread(
        () -> {
          for (int i = 0; i < 10000; i++) {
            atomicIntegerTest.atomicInteger.incrementAndGet();
          }
          latch.countDown();
        })
        .start();

    latch.await();
    System.out.println(atomicIntegerTest.atomicInteger.get());
  }
}
