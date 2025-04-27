package ru.shintar;

import ru.shintar.aop.proxy.Ioc;
import ru.shintar.aop.api.TestLogging;
import ru.shintar.aop.api.TestLoggingInterface;

public class Main {
    public static void main(String[] args) {
        TestLoggingInterface target = new TestLogging();
        TestLoggingInterface proxy = Ioc.createProxy(TestLoggingInterface.class, target);
        proxy.calculation(6);
        proxy.calculation(10, 20);
        proxy.calculation(5, 7, "test");
    }
}
