package com.xxx.nlp.com.dsl;


import java.util.Collection;

/**
 * 实例提供者接口，其实现类以适配器的方式将Bean查找的任务委托给真正的IoC容器，如SpringIoC或Google Guice。
 */
public interface InstanceProvider {

    /**
     * 根据类型获取对象实例。返回的对象实例所属的类是T或它的实现类或子类。如果找不到该类型的实例则抛出异常。
     *
     * @param <T>      类型参数
     * @param beanType 实例的类型
     * @return 指定类型的实例。
     */
    <T> T getInstance(Class<T> beanType);

    /**
     * 根据类型, 获取 provider 所有的相关 bean
     *
     * @param beanType
     * @param <T>
     * @return
     */
    <T> Collection<T> getInstances(Class<T> beanType);

    /**
     * 根据类型, 获取 provider 所有的相关 bean
     *
     * @param beanType
     * @param <T>
     * @return
     */
    <T> T getInstance(Class<T> beanType, String beanName);


}
