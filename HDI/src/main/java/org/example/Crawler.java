package org.example;

import java.io.*;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.net.HttpURLConnection;
import java.net.URL;
public class Crawler {
    public static Set<String> ExcludedPage = new HashSet();
    public static Set<String> ExcludedURL = new HashSet();
    public static String header = "_________________________________________________________________________";
    public Set<String> linkSet;
    public Queue<String> linkQueue;
    public Queue<String> breadthQueue;
    public int BREADTH;
    public int DEPTH;
    public int LINK_COUNT;
    public Crawler() {
        loadResource("excluded-pages.txt",ExcludedPage);
        loadResource("excluded-urls.txt",ExcludedURL);
        linkSet = new HashSet<String>();
        linkQueue = new ArrayDeque<String>();
        breadthQueue = null;
    }

    public void loadResource(String fileName,Set<String> resource){
        InputStream ioStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(fileName);
        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " is not found");
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ioStream));
            String line;
            while ((line = reader.readLine()) != null)
                resource.add(line);
            reader.close();
        } catch(Exception e){}
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
                    connection.disconnect();
                    line = response.toString();
                    int startIndex = 0;
                    while (breadth>0) {
                        /*startIndex = beginIndex(line,startIndex);
                        if (startIndex < 0) break;
                        int endIndex = lastIndex(line,startIndex);
                        if (endIndex < 0) break;
                        String link = line.substring(startIndex, endIndex);*/
                        String URL = extractURL(line,startIndex);
                        if(URL==null){
                            --breadth;
                            continue;
                        }
                        String index [] = URL.split(",");
                        String link = line.substring(Integer.valueOf(index[0]), Integer.valueOf(index[1]));
                        System.out.println(link);
                        if(!link.equals("") && (link.charAt(0)!='/' && link.charAt(0)!='#') && link.charAt(0)=='h'){
                            String domain;
                            int s = link.indexOf("//"),e = link.indexOf("/",s+2);
                            if(e==-1) domain = link.substring(s+2);
                            else domain = link.substring(s+2,link.indexOf("/",s+2));
                            if(!linkSet.contains(link) && !ExcludedURL.contains(domain)){
                                ExcludedURL.add(domain);
                                int li = link.lastIndexOf(".");
                                if(li==-1){
                                    breadthQueue.add(link);
                                    ++linkCount;
                                } else {
                                    if(!ExcludedPage.contains(link.substring(li+1))) {
                                        breadthQueue.add(link);
                                        ++linkCount;
                                    }
                                }
                            }
                        }
                        startIndex = Integer.valueOf(index[1]) + 1;
                    }
                }
                System.out.println("Group["+depth+groupName+"][" + linkCount + "]["+inputUrl+"]");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Group["+depth+groupName+"][error]["+inputUrl+"]");
            }
        }
    }

    public static String extractURL(String line,int startIndex){
        int start = line.indexOf("<a", startIndex);
        if(start==-1) return null;
        int end = line.indexOf(">", start);
        if(end==-1) return null;
        int http = line.indexOf("href=",start);
        if(http==-1) return null;
        int q1 = line.indexOf("\"",http);
        int q2 = line.indexOf("\"",q1+1);
        return (q1+1)+","+q2;
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



