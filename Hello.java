package com.test.test;


import cn.hutool.core.collection.CollUtil;
import org.apache.commons.collections.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Hello {
    public static void main(String[] args) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            list.add("Thread--" + i);
        }
        List<List<String>> dataList = new ArrayList<>();
        dataList = CollUtil.split(list, 100);
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 20, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        List<CompletableFuture<Integer>> futures = dataList.stream()
                .map(item -> CompletableFuture.supplyAsync(() -> settleCountByThread(item), threadPool))
                .collect(Collectors.toList());
        Integer result = futures.stream().map(CompletableFuture::join).mapToInt(Integer::valueOf).sum();
        // 关闭线程池
        threadPool.shutdown();
        // 清理内存
        futures.clear();
    }

    private static int settleCountByThread(List<String> dataList) {
        for (String s : dataList) {
            System.out.println(s);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }
}
