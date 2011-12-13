package com.beecub.games.planet;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class OverviewActivity extends Activity {
    
    public static PlanetView mPlanetView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mPlanetView = new PlanetView(this);
        setContentView(mPlanetView);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        doStart();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        mPlanetView.getThread().pause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.v("beecub", "resume");
        mPlanetView = new PlanetView(this);
        setContentView(mPlanetView);
    }
    
    public static void doStart() {
        if(mPlanetView != null)
            mPlanetView.getThread().doStart();
    }
    public static void doPause() {
        if(mPlanetView != null)
            mPlanetView.getThread().pause();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
