package test;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Rstart {
    public final static Object obj = new Object();
    public final static LinkedList<Object> list = new LinkedList<Object>();


    @SuppressWarnings("resource")
    public static void main(String[] args) {

        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String nextLine = scanner.nextLine();
                synchronized (obj) {
                    list.add(nextLine);
                    obj.notify();
                }
            }
        }).start();

        new Thread(new CustomerTask()).start();

    }
}

class CustomerTask implements Runnable {


    @Override
    public void run() {
        while (true) {
            try {
                Object removeFirst = Rstart.list.removeFirst();
                System.err.println("客户端收到消息:   "+removeFirst);
            }catch (NoSuchElementException e){
                synchronized (Rstart.obj){
                    try {
                        Rstart.obj.wait();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}