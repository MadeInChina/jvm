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
public class Singleton {
  private Singleton() {}

  private volatile static Singleton myinstance;

  public static Singleton getInstance() {
    if (myinstance == null) {//为了提高效率,如果实例已经存在了,就没有必要再走同步块了
      synchronized (Singleton.class) {
        if (myinstance == null) {
          //对象创建过程，本质可以分文三步
          //1.申请内存空间address = allocate
          //2.实例化对象
          //3.将实例化对象分配到内存
          //如果不加volatile的话，2,3步有可能发生指令重排,也就是myinstance已经被分配了地址,但是对象实际为空的情况
          //所以单例需要加上volatile关键字
          myinstance = new Singleton();
        }
      }
    }
    return myinstance;
  }

  public static void main(String[] args) {
    Singleton.getInstance();
  }
}
