package myrpc;

import myrpc.demo.RemoteInvocationHandler;

import java.lang.reflect.Proxy;

public class RpcClientProxy {
    public <T> T clientProxy(final Class<T> interfaceClass,final String host,final int port){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class[]{interfaceClass},new RemoteInvocationHandler(host, port));
    }
}
