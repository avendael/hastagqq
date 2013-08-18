package com.hastagqq.app.util;

import com.hastagqq.app.api.BasicApiResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * @author avendael
 */
public class HttpUtil {
    public static String readContent(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, Constants.CHARSET));
        StringBuffer content = new StringBuffer();

        try {
            for (String line; (line = bufferedReader.readLine()) != null; ) {
                content.append(line);
            }
        } finally {
            bufferedReader.close();
        }

        return content.toString();
    }

    public static BasicApiResponse parseBasicApiResponse(HttpResponse response) {
        BasicApiResponse basicApiResponse = null;

        if (response != null) {
            try {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    basicApiResponse = GsonUtil.getDefaultGsonParser().fromJson(
                            HttpUtil.readContent(entity.getContent()), BasicApiResponse.class);

                    entity.consumeContent();
                }
            } catch (UnsupportedEncodingException e) {
                // TODO fail response
            } catch (Exception e) {
                // TODO fail response
            }
        }

        return basicApiResponse;
    }
}
