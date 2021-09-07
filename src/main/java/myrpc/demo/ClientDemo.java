package myrpc.demo;

import myrpc.RpcClientProxy;

public class ClientDemo {


    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy();
        IHelloService helloService = proxy.clientProxy(IHelloService.class, "127.0.0.1", 12345);
        String name = helloService.sayHello("张三");
        System.out.println(name);
    }
}
