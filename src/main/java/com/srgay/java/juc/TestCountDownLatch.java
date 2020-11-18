package com.srgay.java.juc;

import java.util.concurrent.CountDownLatch;

public class TestCountDownLatch {

    public static void main(String[] agrs) {
        CountDownLatch latch = new CountDownLatch(5); // 5表示有5个线程
        LatchDemo ld = new LatchDemo(latch);

        long start = System.currentTimeMillis();

        for (int i = 0; i < 5; i++) {
            new Thread(ld).start();
        }

        try {
            latch.await(); // 等待
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        System.out.println("耗费时间为：" + (end - start) + "ms");
    }

}

class LatchDemo implements Runnable {
    private CountDownLatch latch;

    public LatchDemo(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 50000; i++) {
                if (i % 2 == 0) {
                    System.out.println(i);
                }
            }
        } finally { // 必须执行的操作
            latch.countDown();
        }
    }
}