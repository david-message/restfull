package com.xxx.rest.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {
    public static final Map<String, Pattern> PATTERN_MAP = new HashMap();

    private StringUtils() {
    }

    public static String[] split(String s, String regex) {
        Pattern p = (Pattern)PATTERN_MAP.get(regex);
        return p != null ? p.split(s) : s.split(regex);
    }

    public static String[] split(String s, String regex, int limit) {
        Pattern p = (Pattern)PATTERN_MAP.get(regex);
        return p != null ? p.split(s, limit) : s.split(regex, limit);
    }

    public static boolean isFileExist(String file) {
        return (new File(file)).exists() && (new File(file)).isFile();
    }

    public static boolean isEmpty(String str) {
        if (str != null) {
            int len = str.length();

            for(int x = 0; x < len; ++x) {
                if (str.charAt(x) > ' ') {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isEmpty(List<String> list) {
        if (list != null && list.size() != 0) {
            return list.size() == 1 && isEmpty((String)list.get(0));
        } else {
            return true;
        }
    }

    public static String diff(String str1, String str2) {
        int index = str1.lastIndexOf(str2);
        return index > -1 ? str1.substring(str2.length()) : str1;
    }

    public static List<String> getParts(String str, String separator) {
        List<String> ret = new ArrayList();
        List<String> parts = Arrays.asList(split(str, separator));
        Iterator i$ = parts.iterator();

        while(i$.hasNext()) {
            String part = (String)i$.next();
            if (!isEmpty(part)) {
                ret.add(part);
            }
        }

        return ret;
    }

    public static String getFirstNotEmpty(String str, String separator) {
        List<String> parts = Arrays.asList(split(str, separator));
        Iterator i$ = parts.iterator();

        String part;
        do {
            if (!i$.hasNext()) {
                return str;
            }

            part = (String)i$.next();
        } while(isEmpty(part));

        return part;
    }

    public static String getFirstNotEmpty(List<String> list) {
        if (isEmpty(list)) {
            return null;
        } else {
            Iterator i$ = list.iterator();

            String item;
            do {
                if (!i$.hasNext()) {
                    return null;
                }

                item = (String)i$.next();
            } while(isEmpty(item));

            return item;
        }
    }

    public static List<String> getFound(String contents, String regex) {
        if (!isEmpty(regex) && !isEmpty(contents)) {
            List<String> results = new ArrayList();
            Pattern pattern = Pattern.compile(regex, 64);
            Matcher matcher = pattern.matcher(contents);

            while(matcher.find()) {
                if (matcher.groupCount() > 0) {
                    results.add(matcher.group(1));
                } else {
                    results.add(matcher.group());
                }
            }

            return results;
        } else {
            return null;
        }
    }

    public static String getFirstFound(String contents, String regex) {
        List<String> founds = getFound(contents, regex);
        return isEmpty(founds) ? null : (String)founds.get(0);
    }

    public static String addDefaultPortIfMissing(String urlString) {
        return addDefaultPortIfMissing(urlString, "80");
    }

    public static String addDefaultPortIfMissing(String urlString, String defaultPort) {
        URL url = null;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException var6) {
            return urlString;
        }

        if (url.getPort() != -1) {
            return urlString;
        } else {
            String regex = "http://([^/]+)";
            String found = getFirstFound(urlString, regex);
            String replacer = "http://" + found + ":" + defaultPort;
            if (!isEmpty(found)) {
                urlString = urlString.replaceFirst(regex, replacer);
            }

            return urlString;
        }
    }

    public static String capitalize(String name) {
        if (name != null && name.length() != 0) {
            char[] chars = name.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);
        } else {
            return name;
        }
    }

    public static String uncapitalize(String str) {
        return str != null && str.length() != 0 ? (new StringBuilder(str.length())).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1)).toString() : str;
    }

    public static byte[] toBytesUTF8(String str) {
        try {
            return toBytes(str, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static byte[] toBytesASCII(String str) {
        try {
            return toBytes(str, "US-ASCII");
        } catch (UnsupportedEncodingException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static byte[] toBytes(String str, String enc) throws UnsupportedEncodingException {
        return str.getBytes(enc);
    }

    static {
        String[] patterns = new String[]{"/", " ", ":", ",", ";", "="};
        String[] arr$ = patterns;
        int len$ = patterns.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String p = arr$[i$];
            PATTERN_MAP.put(p, Pattern.compile(p));
        }

    }
}