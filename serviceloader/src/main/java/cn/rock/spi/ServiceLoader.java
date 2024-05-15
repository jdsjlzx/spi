package cn.rock.spi;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;

/**
 * @param <S> 服务接口类
 */
public final class ServiceLoader<S> {

    public static <S> ServiceLoader<S> load(final Class<S> serviceClass) {
        return new ServiceLoader<S>(serviceClass);
    }

    /**
     * 服务接口类
     */
    private final Class<S> mService;
    /**
     * 缓存已经创建过的服务实现类实例
     */
    private final Map<Class<S>, WeakReference<S>> mProviderCache = new LinkedHashMap<>();

    private ServiceLoader(final Class<S> service) {
        this.mService = service;
        this.load();
    }

    /**
     * 获取服务接口实现类对象
     */
    public S get() {
        WeakReference<S> reference = mProviderCache.get(mService);
        if (reference != null && reference.get() != null) {
            return reference.get();
        }
        return load();
    }

    /**
     * ServiceLoader构造函数执行时，从ServiceProvider注册表中创建服务对象实例
     */
    private S load() {
        try {
            WeakReference<S> reference = mProviderCache.get(mService);
            if (reference != null && reference.get() != null) {
                return reference.get();
            }
            final S provider = ServiceProvider.newProvider(mService);
            mProviderCache.put(mService, new WeakReference<>(provider));
            return provider;
        } catch (Exception e) {
            throw new ServiceConfigurationError(mService.getName() + "创建服务实现类失败", e);
        }
    }
}