package cn.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class ThreadExceptionTest {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        IntStream.rangeClosed(1, 10).forEach(i -> executorService.submit(()-> {
                    if (i == 5) {
                        System.out.println("发生异常啦");
                        throw new RuntimeException("error");
                    }
                    System.out.println("当前执行第几:" + Thread.currentThread().getName() );
                }
        ));
        executorService.shutdown();
    }
}