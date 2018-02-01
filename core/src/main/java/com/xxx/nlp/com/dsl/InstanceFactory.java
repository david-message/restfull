package com.xxx.nlp.com.dsl;


import java.util.*;
import java.util.function.Supplier;

/**
 * <p/>
 * 实例工厂类，充当IoC容器的门面，通过它可以获得部署在IoC容器中的Bean的实例。 InstanceFactory向客户代码隐藏了IoC
 * 工厂的具体实现。在后台，它通过InstanceProvider策略接口，允许选择不同的IoC工厂，例如Spring， Google Guice和
 * TapestryIoC等等。
 * <p/>
 * IoC工厂应该在应用程序启动时装配好，也就是把初始化好的InstanceProvider实现类提供给InstanceFactory。对于web应用
 * 来说，最佳的初始化方式是创建一个Servlet过滤器或监听器，并部署到web.xml里面；对普通java应用程序来说，最佳的初始化
 * 位置是在main()函数里面；对于单元测试，最佳的初始化位置是@BeforeClass或@Before标注的方法内部。<br>
 * <p/>
 * InstanceFactor顺序通过三种途径获取Bean实例。（1）如果已经给InstanceFactory设置了InstanceProvider，那么就通过后者
 * 查找Bean；（2）如果没有设置InstanceProvider，或者通过InstanceProvider无法找到Bean，就通过JDK6的ServiceLoader查找（通
 * 过在类路径或jar中的/META-INF/services/a.b.c.Abc文件中设定内容为x.y.z.Xyz，就表明类型a.b.c.Abc将通过类x.y.z.Xyz
 * 的实例提供）；（3）如果仍然没找到Bean实例，那么将返回那些通过bind()方法设置的Bean实例。（4）如果最终仍然找不到，就抛出
 * IocInstanceNotFoundException异常。
 */
public class InstanceFactory {


    private static Set<InstanceProvider> providers = new LinkedHashSet<>();

    static {
        providers.add(ServiceLoaderInstanceProvider.SERVICELOADER);
        providers.add(BindingInstanceProvider.BINDING);
    }

    /**
     * 设置实例提供者。
     *
     * @param provider 一个实例提供者的实例。
     */
    public static void registerProvider(InstanceProvider provider) {
        if (!providers.contains(provider)) {
            LinkedHashSet<InstanceProvider> newProviders = new LinkedHashSet<>();
            newProviders.add(provider);
            newProviders.addAll(providers);
            providers = newProviders;
        }
    }


    /**
     * 根据类型获取对象实例。返回的对象实例所属的类是T或它的实现类或子类。如果找不到该类型的实例则抛出异常。
     *
     * @param <T>      对象的类型
     * @param beanType 对象所属的类型
     * @return 类型为T的对象实例
     */
    public static <T> T getInstance(Class<T> beanType) {
        T result = getInstanceOrNull(beanType);
        if (result != null) {
            return result;
        }
        throw new IllegalStateException("There's not bean of type '" + beanType + "' exists in IoC container!");
    }

    public static <T> T getInstanceOrNull(Class<T> beanType) {
        T result = null;
        for (InstanceProvider provider : providers) {
            result = provider.getInstance(beanType);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    public static <T> T getInstanceOr(Class<T> beanType, Supplier<T> supplier) {
        T result = null;
        for (InstanceProvider provider : providers) {
            result = provider.getInstance(beanType);
            if (result != null) {
                break;
            }
        }
        return result == null ? supplier.get() : result;
    }

    public static <T> Collection<? extends T> getInstances(Class<T> beanType) {
        List<T> result = new ArrayList<>();
        for (InstanceProvider provider : providers) {
            result.addAll(provider.getInstances(beanType));
        }
        return result;
    }

    /**
     * 根据类型获取对象实例。返回的对象实例所属的类是T或它的实现类或子类。如果找不到该类型的实例则抛出异常。
     *
     * @param <T>      对象的类型
     * @param beanType 对象所属的类型
     * @return 类型为T的对象实例
     */
    public static <T> T getInstance(Class<T> beanType, String beanName) {
        T result = null;
        for (InstanceProvider provider : providers) {
            result = provider.getInstance(beanType, beanName);
            if (result != null) {
                return result;
            }
        }
        throw new IllegalStateException("There's not bean of type '" + beanType + "' exists in IoC container!");
    }

}
