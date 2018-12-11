package com.example.kesha.blog.utils;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kesha.blog.MainActivity;

public class SearchClickListener implements View.OnClickListener {
    private Activity activity;

    public SearchClickListener(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(activity, "CLICK !",Toast.LENGTH_SHORT).show();
        TextView searchText = (TextView) v;
        MainActivity activity = ((MainActivity) this.activity);
        activity.search(searchText.getText().toString());
    }
}
