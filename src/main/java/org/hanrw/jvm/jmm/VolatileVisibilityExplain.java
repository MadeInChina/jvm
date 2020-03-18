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
  原因是因为A线程和B线程分别使用工作内存
  两个线程之前操作动作不可见
  */
  //  public Boolean currentState = false;
  /*
  加volatile关键字的线程共享变量
  会打印->"监控到共享变量的值已经被修改了"
  原因是因为volatile关键字语义上使得两个线程之前的操作可见
  会强制把工作内存刷入到主内存
  汇编代码查看
  -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -Xcomp

  */
  public volatile Boolean currentState = false;

  public static void main(String[] args) throws InterruptedException {

    VolatileVisibilityExplain explain = new VolatileVisibilityExplain();
    /*
    开始监控共享变量
    */
    explain.threadB1.start();
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
            /*
            空跑一直占用cpu使用权,不会进行上下文切换
             */
            while (!currentState) {
            }
            System.out.println("监控到共享变量的值已经被修改了");
          });

  Object object = new Object();
  /** 线程B监视共享变量的值 */
  Thread threadB1 =
      new Thread(
          () -> {
            /*
            由于加了同步块,那么线程会出现上下文切换
            所以不使用volatile关键字也会重新从内存里面读取新的值
            所以会打印"监控到共享变量的值已经被修改了"
             */
            while (!currentState) {
              synchronized (object){

              }
            }
            System.out.println("监控到共享变量的值已经被修改了");
          });
}
