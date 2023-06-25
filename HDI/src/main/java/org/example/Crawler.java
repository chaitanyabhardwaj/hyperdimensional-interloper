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
    public static Set<String> Restricted = new HashSet();
    public static String header = "_________________________________________________________________________";
    public Set<String> linkSet;
    public Queue<String> linkQueue;
    public Queue<String> breadthQueue;
    public int BREADTH;
    public int DEPTH;
    public int LINK_COUNT;
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
        Restricted.add("css");
        linkSet = new HashSet<String>();
        linkQueue = new ArrayDeque<String>();
        breadthQueue = null;
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
        dfs("",BREADTH,0);
    }

    public void dfs(String groupName,int breadth, int depth){
        if(depth<DEPTH) {
            Queue<String> temp = null;
            int gid = 0;
            System.out.println(header+"Level["+depth+"]"+header);
            while (!linkQueue.isEmpty()) {
                process(groupName +"-"+Integer.toString(++gid),breadth,depth, linkQueue.poll());
            }
            if(breadthQueue!=null) {
                linkQueue = breadthQueue;
                breadthQueue = null;
                dfs(groupName, breadth, depth + 1);
            }
        }
    }

    public void process(String groupName,int breadth,int depth,String inputUrl) {
        if(!linkSet.contains(inputUrl) && linkSet.size()<LINK_COUNT) {
            linkSet.add(inputUrl);
            if(breadthQueue==null) breadthQueue = new ArrayDeque<String>();
            int linkCount = 1;
            try {
                URL url = new URL(inputUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();
                    line = response.toString();
                    int startIndex = 0;
                    while (true) {
                        startIndex = beginIndex(line,startIndex);
                        if (startIndex < 0) break;
                        int endIndex = lastIndex(line,startIndex);
                        if (endIndex < 0) break;

                        String link = line.substring(startIndex, endIndex);
                        int li = link.lastIndexOf(".");
                        if(li==-1){
                            if (--breadth < 0) break;
                            else {breadthQueue.add(link);++linkCount;}
                        } else {
                            if(!Restricted.contains(link.substring(li+1))) {
                                if (--breadth < 0) break;
                                else {breadthQueue.add(link);++linkCount;}
                            }
                        }
                        startIndex = endIndex + 1;
                    }
                }
                connection.disconnect();
                System.out.println("Group["+depth+groupName+"][" + linkCount + "]["+inputUrl+"]");
            } catch (IOException e) {
                System.out.println("Group["+depth+groupName+"][error]["+inputUrl+"]");
            }
        }
    }

    public static int beginIndex(String line,int startIndex){
        int http = line.indexOf("http://", startIndex);
        int https = line.indexOf("https://", startIndex);
        if(http == -1){
            if(https == -1) return https;
            return https;
        } else if(https == -1) {
            return http;
        } return Math.min(http,https);
    }

    public static int lastIndex(String line,int startIndex){
        int co = Math.min(line.indexOf(" ",startIndex),line.indexOf("<",startIndex));
        co = Math.min(co,line.indexOf("*",startIndex));
        co = Math.min(co,line.indexOf(";",startIndex));
        co = Math.min(co,line.indexOf("'",startIndex));
        co = Math.min(co,line.indexOf("\\",startIndex));
        return Math.min(line.indexOf("\"",startIndex),co);
    }
}



