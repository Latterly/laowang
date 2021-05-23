package com.url.down;

import com.sun.org.apache.bcel.internal.generic.NEW;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.url.down.ReadFile.queue;

/**
 * @ClassName DownFile
 * @Description TODO
 * @Author Le
 * @Date 2021/5/23 21:23
 * @Version 1.0
 */
public class DownFile {

    private static File outFile;

    // create output file
    static {
        String savePath = "E:\\audio";
        outFile = new File(savePath);
        if (!outFile.exists()) {
            outFile.mkdirs();
        }
    }

    public static void downFileTask(){
        Runnable c1 = ()->{
            while (queue.size()!=0){
                try {
                    Map<URLConnection,Entity> urlInfo = (Map<URLConnection, Entity>) queue.poll();
                    Set<Map.Entry<URLConnection, Entity>> entries = urlInfo.entrySet();
                    for (Map.Entry<URLConnection, Entity> entry : entries) {
                        doDownload(entry.getValue());
                    }

                    queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(c1).start();
        new Thread(c1).start();
        new Thread(c1).start();
        new Thread(c1).start();
        new Thread(c1).start();
        new Thread(c1).start();
    }



    private static void doDownload(Entity entity) {

        //  直接通过主机认证
        HostnameVerifier hv = new HostnameVerifier() {
            @Override
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        //  配置认证管理器
        try {
            javax.net.ssl.TrustManager[] trustAllCerts = {new TrustAllTrustManager()};
            SSLContext sc = SSLContext.getInstance("SSL");
            SSLSessionContext sslsc = sc.getServerSessionContext();
            sslsc.setSessionTimeout(0);
            sc.init(null, trustAllCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            //  激活主机认证
            HttpsURLConnection.setDefaultHostnameVerifier(hv);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }


        BufferedOutputStream bos = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        HttpURLConnection con = null;
        try {
//            URL url = new URL(url);
//            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            URL url = new URL(entity.getAudioUrl());
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(3000);
            con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
            int responseCode = con.getResponseCode();
            System.out.printf("响应码 %d",responseCode);
            is = con.getInputStream();
            bis = new BufferedInputStream(is);
//                byte[] bytes = new byte[1024 << 2];
            byte[] bytes = new byte[61858764];
            int len;

            bos = new BufferedOutputStream(new FileOutputStream(outFile.getPath() + "/" + entity.getTitle()));
            File file = new File(outFile.getPath() + "/" + entity.getTitle());
            // start resouces reading
//                System.out.printf("文件 %s 正在写入... \n",fileName);
            while ( (len = bis.read(bytes)) != -1){
                bos.write(bytes,0,len);
            }
            System.out.printf("文件 %s 写入完成... \n",entity.getTitle());
        } catch (MalformedURLException e) {
            try {
                queue.put(entity);
            } catch (InterruptedException interruptedException) {
                System.err.println("任务失败，进行添加，添加也失败了~~");
                interruptedException.printStackTrace();
            }
            e.printStackTrace();
        } catch (IOException e) {
            try {
                queue.put(entity);
                System.out.println("IO异常任务失败,重新添加该任务,文件名：" + entity.getTitle());
            } catch (InterruptedException interruptedException) {
                System.err.println("IO异常任务失败,重新添加该任务,添加也失败了~~ \n" + "添加失败文件名："+entity);
                interruptedException.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            try {
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
