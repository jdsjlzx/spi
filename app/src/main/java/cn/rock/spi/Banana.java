package cn.rock.spi;

import android.util.Log;

@ServiceProviderInterface(Banana.class)
public class Banana implements Foo {

    @Override
    public void eat() {
        Log.i("lzx", "eat Banana");
    }
}
