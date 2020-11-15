package com.srgay.leetcode;

import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

public class leetcode3 {
    public static void main(String[] args) {
        ZeroEvenOdd zeroEvenOdd = new ZeroEvenOdd(5);
        new Thread(() -> {
            try {
                zeroEvenOdd.zero(System.out::println);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                zeroEvenOdd.even(System.out::println);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                zeroEvenOdd.odd(System.out::println);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

class ZeroEvenOdd1 {
    private int n;

    private volatile int state = 11;
    CountDownLatch countDownLatch = new CountDownLatch(2);
    private volatile boolean control = true;
    ReentrantLock lock = new ReentrantLock();
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();
    Condition condition3 = lock.newCondition();

    public ZeroEvenOdd1(int n) {
        this.n = n;
    }

    public void zero(IntConsumer printNumber) throws InterruptedException {
        while (true) {
            lock.lock();
            try {
                System.out.println("z lock");
                condition1.await();
                printNumber.accept(0);
                condition2.signal();
                condition1.await();
                printNumber.accept(0);
                condition3.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        while (true) {
            lock.lock();
            try {
                if (state == 11) {
                    state = 2;
                    condition1.signal();
                }
                condition2.await();
                printNumber.accept(1);
                condition1.signal();
            } finally {
                lock.unlock();
            }

        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        while (true) {
            lock.lock();
            try {
                if (state == 11) {
                    state = 2;
                    condition1.signal();
                }
                condition3.await();
                printNumber.accept(3);
                condition1.signal();
            } finally {
                lock.unlock();
            }

        }
    }
}

class ZeroEvenOdd {
    private int n;

    private volatile int state = 11;
    CountDownLatch countDownLatch = new CountDownLatch(2);
    private volatile boolean control = true;
    ReentrantLock lock = new ReentrantLock();
    Condition condition1 = lock.newCondition();
    Condition condition2 = lock.newCondition();
    Condition condition3 = lock.newCondition();

    public ZeroEvenOdd(int n) {
        this.n = n;
    }

    public void zero(IntConsumer printNumber) throws InterruptedException {
        countDownLatch.await();
        lock.lock();
        try {
            for (int i = 0; i < n; i++) {
                if (i % 2 == 0) {
                    if (state != 11) {
                        condition1.await();
                    }
                    state = 2;
                    printNumber.accept(0);
                    condition3.signal();
                }else {
                    condition1.await();
                    printNumber.accept(0);
                    condition2.signal();
                }
            }
        } finally {
            lock.unlock();
        }

    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        countDownLatch.countDown();
        for (int i = 2; i <= n; i += 2) {
            lock.lock();
            try {
                condition2.await();
                printNumber.accept(i);
                condition1.signal();
            } finally {
                lock.unlock();
            }
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        countDownLatch.countDown();
        for (int i = 1; i <= n; i += 2) {
            lock.lock();
            try {
                condition3.await();
                printNumber.accept(i);
                condition1.signal();
            } finally {
                lock.unlock();
            }
        }
    }
}