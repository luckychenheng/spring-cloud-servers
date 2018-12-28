package com.spring.cloud.moudle.study.demo.design.singleton;

/**
 * 单例模式
 *
 * @author wangmj
 * @since 2018/11/25
 */
public class SingletonDemo {
    private SingletonDemo() {

    }

    private volatile static SingletonDemo instance;

    public static SingletonDemo getInstance() {
        if (instance == null) {
            synchronized (SingletonDemo.class) {
                if (instance == null) {
                    instance = new SingletonDemo();
                }
            }
        }
        return instance;
    }
}
