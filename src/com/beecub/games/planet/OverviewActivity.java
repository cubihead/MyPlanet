package com.beecub.games.planet;

import android.app.Activity;
import android.beecub.games.planet.R;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.beecub.games.planet.PlanetView.PlanetThread;


public class OverviewActivity extends Activity {
    
    private PlanetThread mPlanetThread;
    private PlanetView mPlanetView;
    
    private TextView mPlanetName;
    private TextView mPlanetMoney;
    private TextView mPlanetPopulation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.overview);

        mPlanetView = (PlanetView) findViewById(R.id.planetview);
        mPlanetThread = mPlanetView.getThread();
        
        mPlanetName = (TextView) findViewById(R.id.planet_name);
        mPlanetMoney = (TextView) findViewById(R.id.planet_resources);
        mPlanetPopulation = (TextView) findViewById(R.id.planet_population);
        
        mPlanetName.setText(PlanetActivity.mName);
        mPlanetName.setTypeface(PlanetActivity.mTypeface);
        mPlanetMoney.setText(this.getString(R.string.resources) + ": " + PlanetActivity.mResources + "/" + PlanetActivity.mResourcesMax);
        mPlanetMoney.setTypeface(PlanetActivity.mTypeface);
        mPlanetPopulation.setText(this.getString(R.string.population) + ": " + PlanetActivity.mPopulation);
        mPlanetPopulation.setTypeface(PlanetActivity.mTypeface);
        

        if (savedInstanceState == null) {
            mPlanetThread.setState(PlanetThread.STATE_RUNNING);
            Log.w(this.getClass().getName(), "SIS is null");
        } else {
            mPlanetThread.restoreState(savedInstanceState);
            Log.w(this.getClass().getName(), "SIS is nonnull");
        }
        
        mPlanetThread.doStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlanetView.getThread().pause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mPlanetView.getThread().resume();
        mPlanetView.getThread().doStart();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPlanetThread.saveState(outState);
        Log.w(this.getClass().getName(), "SIS called");
    }
}
