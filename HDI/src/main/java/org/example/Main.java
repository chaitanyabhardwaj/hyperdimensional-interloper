package org.example;

public class Main {
    public static void main(String[] args) {
        Scrapper scrapper = new Scrapper();
        Crawler crawler = new Crawler();
        crawler.setResultCallBackListener(scrapper);
        crawler.setSeed("https://github.com/reljicd");
        //breadth, depth and number of links to be processed
        crawler.setLimit(200,100, 100000);
        crawler.crawl();
    }
}