package com.hastagqq.app.api;

import android.os.AsyncTask;
import android.util.Log;

import com.hastagqq.app.model.News;
import com.hastagqq.app.util.Constants;
import com.hastagqq.app.util.GsonUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * @author avendael
 */
public class CreateNewsAsyncTask extends AsyncTask<News, Void, BasicApiResponse>{
    private static final String TAG = CreateNewsAsyncTask.class.getSimpleName();

    private NewsApiClient.CreateCallback mCreateCallback;

    public CreateNewsAsyncTask() {}

    public CreateNewsAsyncTask(NewsApiClient.CreateCallback createCallback) {
        mCreateCallback = createCallback;
    }

    @Override
    protected BasicApiResponse doInBackground(News... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Constants.HOST + Constants.NEWS);

        Log.d(TAG, "::postData() -- payload " + GsonUtil.getDefaultGsonParser().toJson(params[0]));
        try {
            httpPost.setEntity(new StringEntity(GsonUtil.getDefaultGsonParser().toJson(params[0])));
            HttpResponse response = httpClient.execute(httpPost);
            Log.d(TAG, "::postData() -- response = " + response.toString());
        } catch (IOException e) {
            Log.e(TAG, "::postData() -- ERROR: " + e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(BasicApiResponse apiResponse) {
        Log.d(TAG, "::onPostExecute() -- result = " + apiResponse);
        if (mCreateCallback != null) mCreateCallback.onCreateNewsComplete(apiResponse);
    }
}
