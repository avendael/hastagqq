package com.hastagqq.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hastagqq.app.api.NewsApiClient;
import com.hastagqq.app.model.News;

/**
 * @author avendael
 */
public class CreateNewsFragment extends Fragment {
    public static final String EXTRAS_LOCATION = "CreateNewsFragment.extras_location";
    private static final String TAG = CreateNewsFragment.class.getSimpleName();

    private EditText mEtTitle;
    private EditText mEtContent;
    private String mLocation;
    private NewsApiClient.CreateCallback mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof NewsApiClient.CreateCallback)) {
            throw new IllegalArgumentException(
                    "Activity must implement NewsApiClient.CreateCallback");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_item, container, false);
        Bundle args = getArguments();
        mEtTitle = (EditText) view.findViewById(R.id.et_news_title);
        mEtContent = (EditText) view.findViewById(R.id.et_news_content);
        TextView txtLocation = (TextView) view.findViewById(R.id.txt_location);
        String defaultLocation = getString(R.string.default_news_location);
        mLocation = args != null ? args.getString(EXTRAS_LOCATION, defaultLocation) : defaultLocation;
        mCallback = (NewsApiClient.CreateCallback) getActivity();

        Log.d(TAG, "::onCreateView() mLocation " + mLocation);
        txtLocation.setText(mLocation);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.create_news_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_submit_news:
                submitNews();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void submitNews() {
        Log.d(TAG, "::submitNews() -- START");
        String defaultTitle = getString(R.string.default_news_title);
        String title = mEtTitle.getText() != null ? mEtTitle.getText().toString() : defaultTitle;
        String defaultContent = getString(R.string.default_news_content);
        String content = mEtContent.getText() != null ? mEtContent.getText().toString() : defaultContent;
        News news = new News(title, content, mLocation, "");
        NewsApiClient.createNews(news, mCallback);
        Log.d(TAG, "::submitNews() -- END");
    }
}
