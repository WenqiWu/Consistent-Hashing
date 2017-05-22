package com.wwq.cloud;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 不带虚拟节点的一致性hash算法
 */
public class ConsistentHashing {
    private static String[] servers = {"192.168.0.0:111", "192.168.0.1:111", "192.168.0.2:111", "192.168.0.3:111", "192.168.0.4:111"};

    //采用TreeMap来存储整数环
    private static SortedMap<Integer, String> sortedMap = new TreeMap<Integer, String>();

    static {
        for (int i = 0; i < servers.length; i++) {
            int hash = getHash(servers[i]);
            System.out.println("[" + servers[i] + "]加入集合中, 其Hash值为" + hash);
            sortedMap.put(hash, servers[i]);
        }
    }

    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        if (hash < 0) {
            hash = Math.abs(hash);
        }

        return hash;
    }

    private static String getServer(String node) {
        int hash = getHash(node);
        SortedMap<Integer, String> subMap = sortedMap.tailMap(hash);

        //对于不存在比key大的hash值的情况，返回最小的key，用于模拟环状结构
        if (subMap.size() == 0) {
            return sortedMap.get(sortedMap.firstKey());
        }

        Integer key = subMap.firstKey();
        return subMap.get(key);
    }

    public static void main(String[] args) {
        String[] nodes = {"127.0.0.1:1111", "221.226.0.1:2222", "10.211.0.1:3333", "5.18.19.21:5555"};
        for (int i = 0; i < nodes.length; i++) {
            System.out.println("[" + nodes[i] + "]的hash值为" + getHash(nodes[i]) + ", 被路由到结点[" + getServer(nodes[i]) + "]");
        }
    }
}
