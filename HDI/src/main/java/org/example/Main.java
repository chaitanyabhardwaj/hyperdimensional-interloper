package org.example;

public class Main {
    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        crawler.setSeed("https://example.com");
        //breadth, depth and number of links to be processed
        crawler.setLimit(200,100, 100000);
        crawler.crawl();
    }
}