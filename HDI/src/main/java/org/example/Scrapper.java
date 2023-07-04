package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scrapper implements CrawlerResultListener{
    public List<String> keyList;
    @Override
    public void onResultUpdate(String htmlResult){
        System.out.println(htmlResult);
    }

    public void setKeyword(List<String> keyList) {
        this.keyList = keyList;
    }
}
