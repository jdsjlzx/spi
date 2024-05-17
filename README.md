# Spi
首次运行，请修改[buildSrc](buildSrc)工程下本地repo的url

maven {
    url 'D:\\git_project\\spi\\repo'
}

Android中SPI思想应用与改进

https://blog.csdn.net/jdsjlzx/article/details/129474806


第一次运行可能会崩溃，属于正常现象。第二次运行就好了

Foo foo = ServiceLoader.load(Banana.class).get();
foo.eat();
