package com.beecub.games.planet;

import android.app.Activity;
import android.beecub.games.planet.R;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.beecub.games.planet.PlanetView.PlanetThread;


public class Overview extends Activity {
//    @Override
//    public void onCreate(Bundle icicle) {
//        super.onCreate(icicle);
//       
//        Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),
//               R.drawable.icon);
//       
//        Matrix matrix = new Matrix();
//        matrix.postRotate(45);
//        
//        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0,
//                          bitmapOrg.getWidth(), bitmapOrg.getHeight(), matrix, true);
//        
//        BitmapDrawable bmd = new BitmapDrawable(resizedBitmap);            
//        
//        setContentView(R.layout.overview);
//            ImageView iv1 = (ImageView) findViewById(R.id.imageView1);
//            iv1.setImageDrawable(bmd);
//    }
    
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
        
        mPlanetName.setText(Planet.mName);
        mPlanetMoney.setText(this.getString(R.string.resources) + ": " + Planet.mMoney);
        mPlanetPopulation.setText(this.getString(R.string.population) + ": " + Planet.mPopulation);
        

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPlanetThread.saveState(outState);
        Log.w(this.getClass().getName(), "SIS called");
    }
}
