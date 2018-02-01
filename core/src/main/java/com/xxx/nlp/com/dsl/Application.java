package com.xxx.nlp.com.dsl;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Application {
    //app/Domain/path
    //package/class/method
    private ConcurrentMap<String, DataDomainSet> appMap = new ConcurrentHashMap<>();
    private ConcurrentMap<String, String> matchRuleMap = new ConcurrentHashMap<>();//rule:package -> app

    public static void main(String[] args) {
//        System.out.println(Application.class.getSimpleName());
        new Application().exec("/abc/def/Hij");///klm/nop.qrs
    }

    protected Resources putDataDomain(Class clz) {
        String appKey = matchRuleMap.get(clz.getPackage().getName());
        if (appKey != null) {
            DataDomainSet dds = appMap.get(appKey);
            if (dds == null) {
                dds = new DataDomainSet();
                appMap.put(appKey, dds);
            }
            return dds.putResource(clz);
        }
        return null;
    }

    public Object exec(String url) {
        //app:(/\w+)+
        String app = null;
        int p = url.indexOf('/', 1);
        while (p > 0) {
            char c = url.charAt(p + 1);
            if (c >= 'A' && c <= 'Z') {
                app = url.substring(0, p);
                break;
            }

            //next
            p = url.indexOf('/', p + 1);
        }

        if (p < 0) {
            //format error
            throw new IllegalStateException("url error,unfound domain.");
        }

        p = url.indexOf('/', p + 1);
        //domain:/[A-Z][^/]*
        String domain = null;
        if (p < 0) {
            domain = url.substring(app.length() + 1);
        } else {
            domain = url.substring(app.length() + 1, p);
        }

        //path:/.*
        String path = (p >= 0 && p <= url.length()) ? url.substring(p) : "";

        return exec(app, domain, path);
    }

    /**
     * app/domain/path
     *
     * @param app
     * @param domain
     * @param path
     * @return
     */
    public Object exec(String app, String domain, String path) {
        DataDomainSet dds = appMap.get(app);
        if (dds == null) {
            return null;//unfound
        }
        Resources res = dds.get(domain);
        if (res == null) {
            return null;//unfound
        }
        int i = res.match(path);
        if (i < 0) {
            return null;//unfound
        }

        Invoker invoker = res.getInvoker(i);
        return invoker.invoke(invoker.getArguments());
    }

    private static final class DataDomainSet {
        private ConcurrentMap<String, Resources> domainMap = new ConcurrentHashMap<>();

        protected Resources putResource(Class clz) {
            return domainMap.put(clz.getSimpleName(), new Resources(clz));
        }

        protected Resources get(String name) {
            return domainMap.get(name);
        }
    }

    private static final class Resources {
        private final Class clz;
        private final Invoker[] invokers;
        private Object instance;

        private Resources(Class c) {
            clz = c;
            //TODO lazy init
//            c.getAnnotation("Lazy")
            try {
                instance = clz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            invokers = new Invoker[0];
//            clz.getDeclaredMethods();
        }

        protected int match(String url) {
            //match url
            return -1;
        }

        protected Invoker getInvoker(int index) {
            return invokers[index];
        }
    }

    private static final class Invoker {
        private final Method method;
        private final Resources resources;

        Invoker(Method m, Resources res) {
            method = m;
            resources = res;
        }

        public Method getMethod() {
            return method;
        }

        public Resources getResources() {
            return resources;
        }

        public Object[] getArguments() {
            //TODO get arg from threadLoacal
            return null;
        }

        public Object invoke(Object... args) {
            Object result = null;
            try {
                result = method.invoke(resources.instance, args);
            } catch (Throwable t) {
                // 500
            }
            //TODO response with result data
            return result;
        }
    }

}
