package com.beecub.games.planet;


import java.util.Date;

import android.app.TabActivity;
import android.beecub.games.planet.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class PlanetActivity extends TabActivity {
    
    public static final String PREFS_NAME = "PlanetPrefs";
    public static final String LOG_TAG = "beecub";
    
    public static String mName = "Corn";
    
    public static int mLevel = 1;
    public static int mMood = 50;
    public static int mEnvironment = 100;
    public static int mTimeMultiplier = 0;
    public static int mResourcePerHour = 1;
    
    public static long mLastLogin; 
    public static long mPopulation = 0;
    public static long mResources = 10;
    public static long mResourcesMax = 10;
    
    public static Typeface mTypeface;
    
    private static TabHost mTabHost;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        Log.v(LOG_TAG, "0"); 
        
        initData();
        
        mTypeface = Typeface.createFromAsset(getAssets(), "fonts/WalterTurncoat.ttf");
        
        Resources res = getResources();
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        
        Intent intent;        
        intent = new Intent().setClass(this, OverviewActivity.class);
        setupTab(new TextView(this), getString(R.string.overview), intent);
        intent = new Intent().setClass(this, FactoryActivity.class);
        setupTab(new TextView(this), getString(R.string.factory), intent);
        intent = new Intent().setClass(this, ResearchActivity.class);
        setupTab(new TextView(this), getString(R.string.research), intent);
        
        
        //TabHost tabHost = getTabHost();
//        TabHost.TabSpec spec;
//        Intent intent;
//        
//        intent = new Intent().setClass(this, OverviewActivity.class);
////        View view = new View(this);
////        view.findViewById(R.layout.test);
////        //view.setBackgroundResource(R.drawable.bar_green);
////        TextView tv1 = new TextView(this);
////        tv1.findViewById(R.id.textView1);
////        tv1.setText(getString(R.string.overview));
////        tv1.setTypeface(mTypeface, Typeface.BOLD);
////        //tv1.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
////        //tv1.setBackgroundResource(R.drawable.bar_green);
//        spec = tabHost.newTabSpec("overview").setIndicator(getString(R.string.overview),
//                          res.getDrawable(R.drawable.ic_tab_overview))
//                      .setContent(intent);
//        //spec = tabHost.newTabSpec("overview").setIndicator(view).setContent(intent);
//        tabHost.addTab(spec);
//        
//        intent = new Intent().setClass(this, FactoryActivity.class);
//        spec = tabHost.newTabSpec("factory").setIndicator(getString(R.string.factory),
//                          res.getDrawable(R.drawable.ic_tab_factory))
//                      .setContent(intent);
//        tabHost.addTab(spec);
//        
//        intent = new Intent().setClass(this, ResearchActivity.class);
//        spec = tabHost.newTabSpec("research").setIndicator(getString(R.string.research),
//                          res.getDrawable(R.drawable.ic_tab_research))
//                      .setContent(intent);
//        tabHost.addTab(spec);
        
        mTabHost.setCurrentTab(0);
    }
    
    private void setupTab(final View view, final String tag, Intent intent) {
        View tabview = createTabView(mTabHost.getContext(), tag);
            TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
        mTabHost.addTab(setContent);
    }

    private static View createTabView(final Context context, final String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        tv.setTypeface(mTypeface);
        return view;
    }
    
    @Override
    protected void onStop(){
       super.onStop();
       saveData();
    }
    
    private void initData() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        
        mName = settings.getString("name", "Corn");
        
        mLevel = settings.getInt("level", 1);
        mMood = settings.getInt("mood", 50);
        mEnvironment = settings.getInt("environment", 100);
        
        mPopulation = settings.getLong("population", 0);
        mLastLogin = settings.getLong("lastlogin", 0);
        mResources = settings.getLong("resources", 10);
        mResourcesMax = settings.getLong("resourcesmax", 10);
                
    }
    
    private void saveData() {
        Date currentDate = new Date();
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
                
        editor.putString("name", mName);
        
        editor.putInt("level", mLevel);
        editor.putInt("mood", mMood);
        editor.putInt("environment", mEnvironment);
        
        editor.putLong("population", mPopulation);
        editor.putLong("lastlogin", currentDate.getTime());
        editor.putLong("resources", mResources);
        editor.putLong("resourcesmax", mResourcesMax);
        
    }
    
    public void saveSingleData(String name, String data) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
                
        editor.putString(name, data);
    }
    
    public void saveSingleData(String name, float data) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
                
        editor.putFloat(name, data);   
    }
    
    public void saveSingleData(String name, long data) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
                
        editor.putLong(name, data);
    }
    
    public void saveSingleData(String name, int data) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
                
        editor.putInt(name, data);
    }
    
    public void saveSingleData(String name, Boolean data) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
                
        editor.putBoolean(name, data);
    }
    
}