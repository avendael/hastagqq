package com.hastagqq.app.api;

import com.google.gson.annotations.Expose;
import com.hastagqq.app.model.News;

import java.util.List;

/**
 * @author avendael
 */
public class GetNewsApiResponse extends BasicApiResponse {
    @Expose
    private List<News> newsItems;

    public List<News> getNewsItems() {
        return newsItems;
    }

    public void setNewsItems(List<News> newsItems) {
        this.newsItems = newsItems;
    }
}
