package myrpc.demo;

public class HelloServiceImpl implements IHelloService {

    @Override
    public String sayHello(String name) {
        return "你好，" + name;
    }
}