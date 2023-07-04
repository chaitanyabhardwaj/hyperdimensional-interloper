package org.example;

@FunctionalInterface
public interface CrawlerResultListener {
    void onResultUpdate(String htmlResult);
}
