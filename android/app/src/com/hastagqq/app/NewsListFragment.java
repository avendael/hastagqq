package com.hastagqq.app;

import com.hastagqq.app.util.DBAdapter;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class NewsListFragment extends Fragment{
    public static final String TAG_FRAGMENT = "tag_news_list_fragment";
    
    private SimpleCursorAdapter mDataAdapter;    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_list_fragment, container, false);

        DBAdapter db = new DBAdapter(getActivity());
        db.open();
        Cursor cursor = db.getAllNews();
        
        String[] columns = new String[] {
                DBAdapter.KEY_CONTENT,
        };
        
        int[] to = new int[] {
                R.id.id_content
        };
        
        mDataAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.news_item,
                cursor,
                columns,
                to,
                0);
        
        ListView listView = (ListView) view.findViewById(R.id.list_news);
        listView.setAdapter(mDataAdapter);
        
        
        return view;        
    }
}
