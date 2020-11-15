package com.srgay.juc;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class ProducerAndConsumer {
    public static void main(String[] args) {
        PC pc = new PCBlockingQueue();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 20,
                0, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(5),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        for (int i = 0; i < 4; i++) {
            executor.execute(()-> {
                try {
                    pc.producer();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            executor.execute(()-> {
                try {
                    pc.consumer();
                } catch (InterruptedException e) {


                }
            });
        }

    }

}
interface PC{
    public void producer() throws InterruptedException;
    public void consumer() throws InterruptedException;
}
class PCSynchronized implements PC{
    private final int MAX_LEN = 10;
    private Queue<Integer> queue = new LinkedList<Integer>();
    public void producer() {
        while(true) {
            synchronized (queue) {
                while (queue.size() == MAX_LEN) {
                    System.out.println("当前队列满");
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                queue.add(1);
                System.out.println("生产者生产一条任务，当前队列长度为" + queue.size());
                queue.notify();
            }
        }
    }
    public void consumer() {
        while (true) {
            synchronized (queue) {
                while (queue.size() == 0) {
                    System.out.println("当前队列为空");
                    try {
                        queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                queue.poll();
                System.out.println("消费者消费一条任务，当前队列长度为" + queue.size());
                queue.notify();
            }
        }
    }
}
class PCLock implements PC{
    private final int MAX_LEN = 10;
    private Queue<Integer> queue = new LinkedList<Integer>();
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    public void producer() {
        while(true) {
            lock.lock();
            try {
                while (queue.size() == MAX_LEN) {
                    System.out.println("当前队列满");
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                queue.add(1);
                System.out.println("生产者生产一条任务，当前队列长度为" + queue.size());
                condition.signalAll();
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
    public void consumer() {
        while (true) {
            lock.lock();
            try {
                while (queue.size() == 0) {
                    System.out.println("当前队列为空");
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                queue.poll();
                System.out.println("消费者消费一条任务，当前队列长度为" + queue.size());
                condition.signalAll();
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }
}
class PCBlockingQueue implements PC {
    private final int MAX_LEN = 10;
    private BlockingQueue<Integer> blockingQueue = new LinkedBlockingDeque<>(10);
    private AtomicInteger atomicInteger = new AtomicInteger();
    public void producer() throws InterruptedException {
        while (true) {
            boolean retvalue = blockingQueue.offer(atomicInteger.incrementAndGet(), 2, TimeUnit.SECONDS);
            if (retvalue==true){
                System.out.println(Thread.currentThread().getName()+"\t 插入队列"+ atomicInteger.get()+"成功"+"资源队列大小= " + blockingQueue.size());
            }else {
                System.out.println(Thread.currentThread().getName()+"\t 插入队列"+ atomicInteger.get()+"失败"+"资源队列大小= " + blockingQueue.size());
            }
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public void consumer() throws InterruptedException {
        Integer result = null;
        while (true){
            result = blockingQueue.poll(2, TimeUnit.SECONDS);
            if (null==result){
                System.out.println("超过两秒没有取道数据，消费者即将退出");
                return;
            }
            System.out.println(Thread.currentThread().getName()+"\t 消费"+ result+"成功"+"\t\t"+"资源队列大小= " + blockingQueue.size());
            Thread.sleep(1500);
        }
    }
}