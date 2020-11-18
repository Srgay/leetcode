package com.srgay.java.juc;

import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierTest{
    static CyclicBarrier cb = new CyclicBarrier(2,()-> System.out.println("end"));
    public static void main(String[] args){
        Thread thread = new Thread(()->{
                try{
                    cb.await();
                }catch(Exception e){
                }
                System.out.println("111");
        });
        thread.start();
        try{
            cb.await();
        }catch(Exception e){
        }
        System.out.println("222");
    }
}