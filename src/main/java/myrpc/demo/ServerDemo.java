package myrpc.demo;

import myrpc.RpcServer;

public class ServerDemo {
    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer();
        rpcServer.publisher(new HelloServiceImpl(),12345);
    }
}
