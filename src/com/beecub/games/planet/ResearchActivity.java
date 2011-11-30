package com.beecub.games.planet;

import java.util.ArrayList;

import android.app.Activity;
import android.app.TabActivity;
import android.beecub.games.planet.R;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class ResearchActivity extends TabActivity {
    
    public static Context mContext;
    private static TabHost mTabHost;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.task_main);
        Log.v(PlanetActivity.LOG_TAG, "0"); 
        
        mContext = getApplicationContext();
        
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
        
        Intent intent;
        intent = new Intent().setClass(this, SubTasksActivity.class);
        setupTab(new TextView(this), getString(R.string.overview), intent, R.layout.tab_bg_tasks, mContext.getResources().getDrawable(R.drawable.icon_face_grin));
        intent = new Intent().setClass(this, SubTasksActivity.class);
        setupTab(new TextView(this), getString(R.string.overview), intent, R.layout.tab_bg_tasks, mContext.getResources().getDrawable(R.drawable.icon_energy));
        intent = new Intent().setClass(this, SubTasksActivity.class);
        setupTab(new TextView(this), getString(R.string.overview), intent, R.layout.tab_bg_tasks, mContext.getResources().getDrawable(R.drawable.icon_environment));
        intent = new Intent().setClass(this, SubTasksActivity.class);
        setupTab(new TextView(this), getString(R.string.overview), intent, R.layout.tab_bg_tasks, mContext.getResources().getDrawable(R.drawable.icon_energy));
//        intent = new Intent().setClass(this, FactoryActivity.class);
//        setupTab(new TextView(this), getString(R.string.factory), intent, R.layout.tab_bg_factory);
//        intent = new Intent().setClass(this, ResearchActivity.class);
//        setupTab(new TextView(this), getString(R.string.research), intent, R.layout.tab_bg_research);
        
        mTabHost.setCurrentTab(0);
        //toast("test");
    }
    
    @Override
    public void onPause() {
        super.onPause();
        //finish();
    }
    
    private void setupTab(final View view, final String tag, Intent intent, int tab_bg, Drawable drawable) {
        View tabview = createTabView(mTabHost.getContext(), tag, tab_bg, drawable);
            TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
        mTabHost.addTab(setContent);
    }

    private static View createTabView(final Context context, final String text, int tab_bg, Drawable drawable) {
        View view = LayoutInflater.from(context).inflate(tab_bg, null);
        ImageView iv = (ImageView) view.findViewById(R.id.imageTasks);
        iv.setImageDrawable(drawable);
        return view;
        
    }
}
