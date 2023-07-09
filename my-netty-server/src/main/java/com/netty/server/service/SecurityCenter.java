package com.netty.server.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class SecurityCenter {
    private static Map<String, Boolean> nodeCheck = new ConcurrentHashMap<String, Boolean>();

    private static Set<String> whiteList = new CopyOnWriteArraySet<>();
    static{
        whiteList.add("127.0.0.1");
    }

    public static boolean isWhiteIP(String ip){
        return whiteList.contains(ip);
    }

    public static boolean isDupLog(String usrInfo){
        return nodeCheck.containsKey(usrInfo);
    }

    public static void addLoginUser(String usrInfo){
        nodeCheck.put(usrInfo,true);
    }

    public static void removeLoginUser(String usrInfo){
        nodeCheck.remove(usrInfo,true);
    }
}
