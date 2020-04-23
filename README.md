查看Volatile汇编后的指令

1、下载反汇编插件 

下载地址https://pan.baidu.com/s/1i3HxFDF，原文见：https://www.xuebuyuan.com/3192700.html

2、指定插件的位置
hsdis-amd64.dylib mac
hsdis-amd64.so  linux

hsdis-amd64.dylib放在$JAVA_PATH/jre/lib/server/中，与libjvm.dylib同目录

3、查看hsdis是否工作
java -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -version

4、执行反汇编命令

volatile关键字底层实际加lock指令

-server -Xcomp -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -XX:CompileCommand=compileonly,*VolatileTest.start 

-XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -Xcomp

汇编直接加lock前缀
0x000000010fb21eeb: lock addl $0x0,(%rsp)     ;*putfield sum
                                            ; - org.hanrw.jvm.jmm.VolatileTest::start@2 (line 17)
                                            ; - org.hanrw.jvm.jmm.VolatileTest::main@9 (line 13)
然后找到hotspot源码
orderAccess_linux_x86.inline.hpp
inline void OrderAccess::fence() {
  if (os::is_MP()) {
    // always use locked addl since mfence is sometimes expensive
#ifdef AMD64
    __asm__ volatile ("lock; addl $0,0(%%rsp)" : : : "cc", "memory");
#else
    __asm__ volatile ("lock; addl $0,0(%%esp)" : : : "cc", "memory");
#endif
  }
}

===========jitwatch
https://github.com/AdoptOpenJDK/jitwatch

===========Cas底层实现
https://www.xilidou.com/2018/02/01/java-cas/
unsafe.cpp
cas
UNSAFE_ENTRY(jboolean, Unsafe_CompareAndSwapInt(JNIEnv *env, jobject unsafe, jobject obj, jlong offset, jint e, jint x))
  UnsafeWrapper("Unsafe_CompareAndSwapInt");
  oop p = JNIHandles::resolve(obj);
  jint* addr = (jint *) index_oop_from_field_offset_long(p, offset);
  return (jint)(Atomic::cmpxchg(x, addr, e)) == e;
UNSAFE_END

找到
atomic_linux_x86.inline.hpp

cmpxchg比较交换有三个步骤:读取->比较->写回所以需要保证原子性加LOCK保证原子性

inline jint     Atomic::cmpxchg    (jint     exchange_value, volatile jint*     dest, jint     compare_value) {
  // alternative for InterlockedCompareExchange
  int mp = os::is_MP();
  __asm {
    mov edx, dest
    mov ecx, exchange_value
    mov eax, compare_value
    LOCK_IF_MP(mp)
    cmpxchg dword ptr [edx], ecx
  }

// Adding a lock prefix to an instruction on MP machine
#define LOCK_IF_MP(mp) "cmp $0, " #mp "; je 1f; lock; 1: "
                       
                       
                       
