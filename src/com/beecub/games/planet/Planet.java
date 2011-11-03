package com.beecub.games.planet;


import android.app.TabActivity;
import android.beecub.games.planet.R;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TabHost;

public class Planet extends TabActivity {
    
    public static final String LOG_TAG = "beecub";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        Log.v(LOG_TAG, "0"); 
        
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;
        
        intent = new Intent().setClass(this, Overview.class);
        spec = tabHost.newTabSpec("artists").setIndicator(getString(R.string.overview),
                          res.getDrawable(R.drawable.ic_tab_overview))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, Factory.class);
        spec = tabHost.newTabSpec("albums").setIndicator(getString(R.string.factory),
                          res.getDrawable(R.drawable.ic_tab_factory))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, Research.class);
        spec = tabHost.newTabSpec("songs").setIndicator(getString(R.string.research),
                          res.getDrawable(R.drawable.ic_tab_research))
                      .setContent(intent);
        tabHost.addTab(spec);
        
        tabHost.setCurrentTab(0);
    }
}