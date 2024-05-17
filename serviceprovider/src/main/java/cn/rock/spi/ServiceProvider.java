package cn.rock.spi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class ServiceProvider {

    public static final Map<Class<?>, Callable<?>> sProviders = new LinkedHashMap<>();

    private ServiceProvider() {
    }

    static synchronized void register(final Class<?> service, final Callable<?> callable) {
        sProviders.put(service, callable);
    }

    static synchronized <S> S newProvider(final Class<S> service) throws Exception {
        System.out.println("ServiceProvider newProvider service  " + service);
        for (Map.Entry<Class<?>, Callable<?>> entry : sProviders.entrySet()) {
            System.out.println("ServiceProvider newProvider provider entry.getKey()  " + entry.getKey() + "  entry.getValue() " + entry.getValue());
        }
        //调用Callable#call获取实例
        return (S) sProviders.get(service).call();
    }

    static {
        // kv方式添加service接口名以及Callable对象
        register(Apple.class, new AppleCallable());
        register(Banana.class, new BananaCallable());
        System.out.println("ServiceProvider init sProviders size " + sProviders.entrySet().size());
    }
}