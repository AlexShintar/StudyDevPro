package ru.shintar.aop.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shintar.aop.annotation.Log;
import ru.shintar.aop.api.TestLoggingInterface;

public class Ioc {
    private static final Logger logger = LoggerFactory.getLogger(Ioc.class);

    public static TestLoggingInterface createProxy(
            Class<TestLoggingInterface> interfaceClass, TestLoggingInterface target) {
        InvocationHandler handler = new LoggingInvocationHandler<>(interfaceClass, target);
        return (TestLoggingInterface)
                Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] {interfaceClass}, handler);
    }

    static class LoggingInvocationHandler<T> implements InvocationHandler {
        private final T target;
        private final Set<Method> methodsToLog;

        public LoggingInvocationHandler(Class<?> interfaceClass, T target) {
            this.target = target;
            this.methodsToLog = Arrays.stream(interfaceClass.getMethods())
                    .filter(m -> {
                        try {
                            Method impl = target.getClass().getMethod(m.getName(), m.getParameterTypes());
                            return impl.isAnnotationPresent(Log.class);
                        } catch (NoSuchMethodException e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toSet());
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (methodsToLog.contains(method) && logger.isInfoEnabled()) {
                if (args != null && args.length > 0) {
                    logger.info("executed method: {}, params: {}", method.getName(), Arrays.toString(args));
                } else {
                    logger.info("executed method: {}", method.getName());
                }
            }
            return method.invoke(target, args);
        }
    }
}
