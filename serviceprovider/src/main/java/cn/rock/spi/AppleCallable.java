package cn.rock.spi;

import java.util.concurrent.Callable;

public class AppleCallable implements Callable<Apple> {
    public AppleCallable() {
    }

    public Apple call() throws Exception {
        return new Apple();
    }
}
