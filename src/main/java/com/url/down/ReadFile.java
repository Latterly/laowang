package com.url.down;
import com.alibaba.fastjson.JSONObject;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName readFile
 * @Description TODO
 * @Author Le
 * @Date 2021/5/23 16:40
 * @Version 1.0
 */
public class ReadFile {

    public static BlockingQueue<Object> queue = new ArrayBlockingQueue<>(10);

    public static List<Entity> analysisUrl() {
        File file = new File("C:\\Users\\Le\\Desktop\\樊登讲论语287集mp3.text");
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempSpace;
//            ArrayList<String> list = new ArrayList<>();
            while ( (tempSpace = reader.readLine()) != null ){
               buffer.append(tempSpace);
            }
            List<Entity> parse = JSONObject.parseArray(buffer.toString(),Entity.class);
            reader.close();
            return parse;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void putTask(List<Entity> list){
        Runnable p1 = ()->{
            List<Entity> taskFirst = list.subList(0, list.size() / 2);
            taskAnalyzer(taskFirst);
        };

        Runnable p2 = ()->{
            List<Entity> taskSecond = list.subList(list.size() / 2, list.size());
            taskAnalyzer(taskSecond);
        };

        new Thread(p1,"p1").start();
        new Thread(p2,"p2").start();
    }

    private static void taskAnalyzer(List<Entity> taskFirst) {
        taskFirst.forEach(entity->{
            if (!entity.getAudioUrl().isEmpty()){
                Map<String, Entity> map = new ConcurrentHashMap<>();
                entity.setTitle(entity.getTitle() +".mp3" );

                map.put("",entity);
                try {
                    queue.put(map);
                } catch (InterruptedException e) {
                    System.err.println("队列添加失败！！文件名：" + entity.getTitle());
                    e.printStackTrace();
                }
            }
        });
    }


//    public static void download(List<Entity> list) {
////        list.parallelStream().forEach(entity -> {
//        for (Entity entity : list) {
//            String fileName = entity.getTitle() +".mp3" ;
//            String audioUrl = entity.getAudioUrl();
//            BufferedOutputStream bos = null;
//            InputStream is = null;
//            BufferedInputStream bis = null;
//            try {
//                URL url = new URL(audioUrl);
//                URLConnection con = url.openConnection();
//                con.setConnectTimeout(3000);
//                con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
//                is = con.getInputStream();
//                bis = new BufferedInputStream(is);
////                byte[] bytes = new byte[1024 << 2];
//                byte[] bytes = new byte[61858764];
//                int len;
//
//                bos = new BufferedOutputStream(new FileOutputStream(outFile.getPath() + "/" + fileName));
//                File file = new File(outFile.getPath() + "/" + fileName);
//
////                if (file.exists()){
////                    System.out.println("文件存在停止线程");
////                    Thread.interrupted();
////                }
//                // start resouces reading
////                System.out.printf("文件 %s 正在写入... \n",fileName);
//                while ( (len = bis.read(bytes)) != -1){
//
//                    bos.write(bytes,0,len);
//                }
////                while ( (len = is.read(bytes)) != -1){
////                    bos.write(bytes,0,len);
////                }
//                System.out.printf("文件 %s 写入完成... \n",fileName);
//            } catch (MalformedURLException e) {
//                System.err.println("MalformedURLException,文件名 ：" + fileName );
//                e.printStackTrace();
//            } catch (IOException e) {
//                System.err.println("IOException,文件名 ：" + fileName );
//                e.printStackTrace();
//            }finally {
//                try {
//                    if (bos != null)
//                        bos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    if (is != null)
//                        is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
////        );
//    }

//    @Override
//    public MyRunable setParam(List<Entity> items) {
//        totalList = items;
//        return this;
//    }
//
//    @Override
//    public void run() {
//        download(totalList);
//    }
}

