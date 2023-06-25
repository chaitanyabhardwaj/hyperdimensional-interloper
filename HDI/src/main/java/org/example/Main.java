package org.example;

public class Main {
    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        crawler.setSeed("Link to be seeded initially");
        //breadth, depth and number of links to be processed
        crawler.setLimit(10,20, 1000);
        crawler.crawl();
    }
}