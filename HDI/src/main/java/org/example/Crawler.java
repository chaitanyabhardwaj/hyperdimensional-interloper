package org.example;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class Crawler {
    public Set<String> linkSet;
    public Queue<String> linkQueue;
    public int BREADTH;
    public int DEPTH;
    public int LINK_COUNT;

    public static Set<String> Restricted = new HashSet();

    public Crawler() {
        Restricted.add("png");
        Restricted.add("jpeg");
        Restricted.add("jpg");
        Restricted.add("gif");
        Restricted.add("js");
        Restricted.add("json");
        Restricted.add("webm");
        Restricted.add("mp3");
        Restricted.add("mp4");
        Restricted.add("wav");
        Restricted.add("aud");
        linkSet = new HashSet<String>();
        linkQueue = new ArrayDeque<String>();
    }

    public void setSeed(String seed){
        linkQueue.add(seed);
    }

    public void setLimit(int b,int d,int count){
        BREADTH = b;
        DEPTH = d;
        LINK_COUNT = count;
    }
    public void crawl(){
      dfs(BREADTH,0);
    }

    public void dfs(int breadth, int depth){
        if(depth<DEPTH) {
            while (!linkQueue.isEmpty()) {
                process(breadth,depth + 1, linkQueue.poll());
                dfs(breadth, depth + 1);
            }
        }
    }
    public void process(int breadth,int depth,String inputUrl) {
        if(!linkSet.contains(inputUrl) && linkSet.size()<LINK_COUNT) {
            System.out.println("at [" + depth + "]" + inputUrl);
            linkSet.add(inputUrl);
            try {
                URL url = new URL(inputUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) response.append(line);

                    line = response.toString();
                    int startIndex = 0;
                    while (true) {
                        int http = line.indexOf("http://", startIndex);
                        int https = line.indexOf("https://", startIndex);
                        if(http == -1){
                            if(https == -1) return;
                            else startIndex = https;
                        } else if(https == -1) {
                            startIndex = http;
                        } else startIndex = Math.min(http,https);
                        if (startIndex < 0) break;
                        int endIndex = line.indexOf("\"",startIndex);
                        if (endIndex < 0) break;
                        String link = line.substring(startIndex, endIndex);//.split(" ")[0];
                        startIndex = endIndex + 1;
                        if(!Restricted.contains(line.substring(line.lastIndexOf("[.]"))+1)) {
                            if (--breadth < 0) return;
                            linkQueue.add(link);
                        }
                    }
                    reader.close();
                }
                connection.disconnect();
            } catch (IOException e) {
                System.out.println("error");
            }
        }
    }
}



