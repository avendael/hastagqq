package com.hastagqq.app.api;

import com.hastagqq.app.model.News;

import java.util.List;

/**
 * @author avendael
 */
public class GetNewsApiResponse extends BasicApiResponse {
    private List<News> newsItems;

    public List<News> getNewsItems() {
        return newsItems;
    }

    public void setNewsItems(List<News> newsItems) {
        this.newsItems = newsItems;
    }
}
