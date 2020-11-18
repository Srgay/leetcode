package com.srgay.java.iostream;

import java.io.*;
import java.sql.Time;

public class FileIo {
    public static void main(String[] args) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        var f1 = new File(FileIo.class.getClassLoader().getResource("2.txt").getPath());
        var f2 = new File(FileIo.class.getClassLoader().getResource("2.txt").getPath());
        in = new FileInputStream(f1);
        out = new FileOutputStream(f2);
        try {
            byte[] b = new byte[1024 * 10];
            // 起始长度为0
            int len = 0;
            // while(){} 循环   一边读取 ，一边写入（复制）文件
            for (int i = 0; i < 1000000; i++) {
                out.write("zsdasda".getBytes());
            }
            long start = System.currentTimeMillis();
            while ((len = in.read(b)) != -1) {
                System.out.println(new String(b));
            }
            long end =System.currentTimeMillis();
            System.out.println(end-start);
            /*byte[] b = new byte[1024 * 10];
            // 起始长度为0
            int len = 0;
            // while(){} 循环   一边读取 ，一边写入（复制）文件
            while ((len = in.read(b)) != -1) {
                out.write(b, 0, len);
                out.flush();  //  文件刷新
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
            out.close();
        }
    }
}
