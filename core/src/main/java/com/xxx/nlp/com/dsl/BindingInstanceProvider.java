package com.xxx.nlp.com.dsl;

import java.util.*;

/**
 * 基于手工绑定, 测试用
 */
public enum BindingInstanceProvider implements InstanceProvider {
    BINDING;

    /**
     * 以下部分仅用于提供代码测试功能，产品代码不要用
     */
    private final Map<String, Object> instances = new HashMap<>();

    /**
     * 将服务绑定到具体实例
     *
     * @param <T>                   Bean实例的类型
     * @param serviceInterface      注册类型
     * @param serviceImplementation 对象实例
     */
    public <T> void bind(Class<T> serviceInterface, T serviceImplementation) {
        instances.put(serviceInterface.getName(), serviceImplementation);
    }

    /**
     * 将服务绑定到具体实例并指定名字
     *
     * @param <T>                   Bean实例的类型
     * @param serviceInterface      注册类型
     * @param serviceImplementation 对象实例
     * @param beanName              实例名称
     */
    public <T> void bind(Class<T> serviceInterface, T serviceImplementation, String beanName) {
        instances.put(toName(serviceInterface, beanName), serviceImplementation);
    }

    @Override
    public <T> T getInstance(Class<T> beanType) {
        return (T) instances.get(beanType.getName());
    }

    @Override
    public <T> Collection<T> getInstances(Class<T> beanType) {
        List<T> result = new ArrayList<>();
        for (Object o : instances.values()) {
            if (beanType.isInstance(o)) {
                result.add((T) o);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getInstance(Class<T> beanType, String beanName) {
        return (T) instances.get(toName(beanType, beanName));
    }

    private String toName(Class<?> beanType, String beanName) {
        return beanType.getName() + ":" + beanName;
    }

    /**
     * 删除缓存的bean实例
     */
    public void clear() {
        instances.clear();
    }
}
