package com.hastagqq.app.api;

import android.util.Log;

import com.hastagqq.app.model.News;
import com.hastagqq.app.util.Constants;
import com.hastagqq.app.util.GsonUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.commons.lang3.StringUtils;

/**
 * @author avendael
 */
public class NewsApiClient {
    private static final String TAG = NewsApiClient.class.getSimpleName();

    public interface CreateCallback {
        public void onCreateNewsComplete(BasicApiResponse apiResponse);
    }

    public interface GetCallback {
        public void onGetNewsComplete(GetNewsApiResponse apiResponse);
    }

    public static void createNews(News news, CreateCallback callback) {
        new CreateNewsAsyncTask(callback).execute(news);
    }

    public static void getNews(String location, final GetCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        Log.d(TAG, "::getNews() -- START");

        if (StringUtils.isEmpty(location)) location = "The Dark Void";
//        location = "ortigas"; // TODO test. delete.

        client.addHeader("device_location", location);
        client.get(Constants.HOST + Constants.NEWS, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "::getNews() -- response = " + response);
                callback.onGetNewsComplete(GsonUtil.getDefaultGsonParser()
                        .fromJson(response, GetNewsApiResponse.class));
            }
        });
    }
}
