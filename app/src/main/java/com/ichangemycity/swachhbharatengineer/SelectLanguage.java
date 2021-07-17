package com.ichangemycity.swachhbharatengineer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ichangemycity.adapter.LanguageAdapter;
import com.ichangemycity.appdata.AppController;
import com.ichangemycity.base.BaseAppCompatActivity;

/**
 * Created by pattabi.raman on 07-12-2017.
 */

public class SelectLanguage extends BaseAppCompatActivity {

    ListView languageListView;
    TextView belowWelcome;
    public static Activity act;


    public Activity getActivity() {
        if (act == null) {
            act = SelectLanguage.this;
        }
        return act;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_language);
        act = SelectLanguage.this;
        languageListView = findViewById(R.id.sp_language);
        belowWelcome = findViewById(R.id.belowWelcome);
        belowWelcome.setOnClickListener(v -> {
            act.finish();
        });

//        findViewById(R.id.done)
//                .setOnClickListener(v -> {
//                    startActivity(new Intent(act,
//                            UserMobileNumber.class).putExtra("setSelection", 0));
//                    act.finish();
//
//                });

    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight()
                    * (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        try {
            mAdapter = new LanguageAdapter(act,
                    AppController.languageArrayList);
            languageListView.setAdapter(mAdapter);
            setListViewHeightBasedOnItems(languageListView);
//            phoneLogin(((RelativeLayout)findViewById(R.id.parent)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LanguageAdapter mAdapter;

}
