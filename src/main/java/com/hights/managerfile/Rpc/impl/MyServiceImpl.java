package com.hights.managerfile.Rpc.impl;


import com.hights.managerfile.Rpc.api.MyService;

public class MyServiceImpl implements MyService {

    @Override
    public String sayHelloWorld(String name) {

        return "Hello world, " + name;
    }
}
