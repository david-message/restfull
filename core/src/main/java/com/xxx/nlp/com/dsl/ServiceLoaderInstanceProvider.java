package com.xxx.nlp.com.dsl;


import javax.inject.Named;
import java.util.*;


public enum ServiceLoaderInstanceProvider implements InstanceProvider {
    SERVICELOADER;
    @Override
    public <T> T getInstance(Class<T> beanType) {
        ServiceLoader<T> services = ServiceLoader.load(beanType);
        T result = null;
        int count = 0;
        for (T service : services) {
            result =service;
            count = count + 1;
        }
        if (count > 1) {
            throw new IllegalStateException(String.format("found %s beans of type %s", count, beanType.getName()));
        }
        return result;
    }

    @Override
    public <T> Collection<T> getInstances(Class<T> beanType) {
        ServiceLoader<T> services = ServiceLoader.load(beanType);
        List<T> result = new ArrayList<>();
        for (T service : services) {
            result.add(service);
        }
        return result;
    }

    @Override
    public <T> T getInstance(Class<T> beanType, String beanName) {
        Set<T> results = new HashSet<T>();
        for (T instance : ServiceLoader.load(beanType)) {
            Named named = instance.getClass().getAnnotation(Named.class);
            if (named != null && beanName.equals(named.value())) {
                results.add(instance);
            }
        }
        if (results.size() > 1) {
            throw new IllegalStateException("There're more than one bean of type '"
                    + beanType + "' and named '" + beanName + "'");
        }
        if (results.size() == 1) {
            return results.iterator().next();
        }
        return null;
    }
}
