package com.github.tsonglew.etcdhelper.common;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author tsonglew
 */
public class EtcdClientManager {

    private static final ConcurrentHashMap<String, EtcdClient> CONN_MAP = new ConcurrentHashMap<>();

    public static EtcdClient addConn(String urls) {
        CONN_MAP.computeIfAbsent(urls, k -> {
            var c = new EtcdClient();
            c.init(k.split(","), null, null);
            return c;
        });
        return CONN_MAP.get(urls);
    }

    public static EtcdClient getConn() {
        return CONN_MAP.entrySet().stream().findAny().get().getValue();
    }
}
