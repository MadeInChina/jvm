package org.hanrw.jvm.jmm;

/**
 * @author hanrw
 * @date 2020/3/18 3:12 PM
 * 对象初始化分成三部分
 * 1.申请内存
 * 2.实例化
 * 3.将实例化对象分配到内存
 * 这个步骤不能保证原子性
 * 可能产生指令重排
 * 所以可能造成引用对象已经分配地址,但是实际的对象还没有实例化
 */
public class SingletonV2 {
  private static SingletonV2 singletonV2;

  /**
   * 单例禁止使用构造函数初始化
   */
  private SingletonV2(){
  }

  /**
   *
   * 锁的范围更小，可以参考JDK8 ConcurrentHashMap node锁的机制
   * 在node锁之前的操作都是基于CAS多线程安全
   * 构造实例方法
   *
   * @return
   */
  public static SingletonV2 getInstance() {
    // 业务方法
    // dosomething
    if (singletonV2 == null) { // 这条判断是优化代码,避免实例已经生成后再进入同步块判断
      synchronized (SingletonV2.class) {
        if (singletonV2 == null) {
          singletonV2 = new SingletonV2();
        }
      }
    }
    return singletonV2;
  };

  public static void main(String[] args) {
    SingletonV2.getInstance();
  }
}
