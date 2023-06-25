package org.example;

public class Main {
    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        crawler.setSeed("https://en.wikipedia.org/wiki/Main_Page");
        crawler.setLimit(20,2, 200);//Breadth and Depth
        crawler.crawl();
    }
}