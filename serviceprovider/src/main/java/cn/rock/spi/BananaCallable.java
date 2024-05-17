package cn.rock.spi;

import java.util.concurrent.Callable;

public class BananaCallable implements Callable<Banana> {
    public BananaCallable() {
    }

    public Banana call() throws Exception {
        return new Banana();
    }
}