package org.hanrw.jvm.jmm;

/**
 * @author hanrw
 * @date 2020/3/17 5:47 PM
 * 主要用来验证volatile关键字语义上的禁止指令重排序
 */
public class VolatileMemoryReorderingExplain {
  private static int x = 0, y = 0;
  private static int a = 0, b =0;

  public static void main(String[] args) throws InterruptedException {
    int i = 0;

    for (;;){
      i++;
      x = 0; y = 0;
      a = 0; b = 0;
      Thread t1 = new Thread(() -> {
        /*
        线程一等一下线程二
         */
        shortWait(100000);
        a = 1;
        x = b;
      });
      Thread t2 = new Thread(() -> {
        b = 1;
        y = a;
      });
      t1.start();
      t2.start();
      t1.join();
      t2.join();

      /**
       * 1,1
       * 0,1
       * 1,0
       * 0,0 这种情况最从逻辑上来讲是不可能发生的
       * 如果发生则代表了指令重排
       */
      String result = "第" + i + "次 (" + x + "," + y + "）";
      if(x == 0 && y == 0) {
        System.err.println(result);
        break;
      } else {
        System.out.println(result);
      }
    }

  }

  public static void shortWait(long interval){
    long start = System.nanoTime();
    long end;
    do{
      end = System.nanoTime();
    }while(start + interval >= end);
  }

}
