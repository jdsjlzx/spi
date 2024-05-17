package cn.rock.spi;

import android.util.Log;

@ServiceProviderInterface(Apple.class)
public class Apple implements Foo {

    @Override
    public void eat() {
        Log.i("lzx", "eat Apple");
    }
}
