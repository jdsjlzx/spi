# Spi
首次运行，请修改[buildSrc](buildSrc)工程下本地repo的url

maven {
    url 'D:\\git_project\\spi\\repo'
}

Android中SPI思想应用与改进

https://blog.csdn.net/jdsjlzx/article/details/129474806


# serviceprovider

serviceprovider工程只是为了编译使用，他下面的类全部都会通过asm动态生成


# 基于buildSrc，创建Gradle插件

与 https://github.com/jdsjlzx/NewSpi（基于Gradle 8.+，创建Gradle插件）不同，直接使用buildSrc创建Gradle插件

# 已知问题
1. 第一次运行代码会出现崩溃的问题，勾选下面的选项重新运行即可。
<img width="732" alt="image" src="https://github.com/jdsjlzx/NewSpi/assets/1652076/f66facb3-1a79-47d0-9a2c-47bae8715ea0">

2. 直接安装apk第一次运行打开不会崩溃

# 感谢

如果你能解决第一次运行崩溃的问题，请及时告知我
