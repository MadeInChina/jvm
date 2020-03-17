package org.hanrw.jvm.jmm;

/**
 * @author hanrw
 * @date 2020/3/17 5:47 PM
 * 主要用来验证volatile关键字语义上的可见性 一个线程用于修改共享变量 另外一个线程用于监视共享变量是否改变
 */
public class VolatileVisibilityExplain {

  /*
  没有加volatile关键字的线程共享变量
  不会打印->"监控到共享变量的值已经被修改了"
  */
  //  public Boolean currentState = false;
  /*
  加volatile关键字的线程共享变量
  会打印->"监控到共享变量的值已经被修改了"
  */
  public volatile Boolean currentState = false;

  public static void main(String[] args) throws InterruptedException {

    VolatileVisibilityExplain explain = new VolatileVisibilityExplain();
    /*
    开始监控共享变量
    */
    explain.threadB.start();
    Thread.sleep(500);
    /*
    开始修改共享变量
    */
    explain.threadA.start();
  }

  /** 线程A修改共享变量的值 */
  Thread threadA =
      new Thread(
          () -> {
            currentState = true;
          },
          "threadA");

  /** 线程B监视共享变量的值 */
  Thread threadB =
      new Thread(
          () -> {
            while (!currentState) {
            }
            System.out.println("监控到共享变量的值已经被修改了");
          });
}
