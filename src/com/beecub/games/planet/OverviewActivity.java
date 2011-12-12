package com.beecub.games.planet;

import android.app.Activity;
import android.os.Bundle;


public class OverviewActivity extends Activity {
    
    private static PlanetView mPlanetView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new PlanetView(this));
    }
    
    @Override
    protected void onStop() {
        super.onStop();

    }
    
    @Override
    protected void onResume() {
        super.onResume();
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
