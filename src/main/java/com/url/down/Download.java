package com.url.down;


import java.util.*;

import static com.url.down.ReadFile.queue;

public class Download {


    public static void main(String[] args) {
        List<Entity> result = ReadFile.analysisUrl();
        result.forEach(System.out::println);
        ReadFile.putTask(result);
        DownFile.downFileTask();
    }
}