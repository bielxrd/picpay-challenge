package br.com.picpay.shared.utils;

public class CacheUtils {
    public static String buildKey(String key, String... values) {
        if (values.length == 1) {
            return key + "::" + values[0];
        }

        return key + "::" + String.join("-", values);
    }

    public static String buildKeyFilter(String cacheName, String key) {
        return cacheName + "::" + key + "*";
    }
}
