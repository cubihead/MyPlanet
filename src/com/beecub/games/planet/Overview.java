package com.beecub.games.planet;

import android.app.Activity;
import android.beecub.games.planet.R;
import android.os.Bundle;
import android.util.Log;

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
    
    /** A handle to the thread that's actually running the animation. */
    private PlanetThread mPlanetThread;

    /** A handle to the View in which the game is running. */
    private PlanetView mPlanetView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // tell system to use the layout defined in our XML file
        setContentView(R.layout.overview);

        // get handles to the PlanetView from XML, and its PlanetThread
        mPlanetView = (PlanetView) findViewById(R.id.planetview);
        mPlanetThread = mPlanetView.getThread();
        
        // give the PlanetView a handle to the TextView used for messages
        //mPlanetView.setTextView((TextView) findViewById(R.id.text));

        if (savedInstanceState == null) {
            // we were just launched: set up a new game
            mPlanetThread.setState(PlanetThread.STATE_RUNNING);
            Log.w(this.getClass().getName(), "SIS is null");
        } else {
            // we are being restored: resume a previous game
            mPlanetThread.restoreState(savedInstanceState);
            Log.w(this.getClass().getName(), "SIS is nonnull");
        }
        
        mPlanetThread.doStart();
    }

    /**
     * Invoked when the Activity loses user focus.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPlanetView.getThread().pause(); // pause game when Activity pauses
    }

    /**
     * Notification that something is about to happen, to give the Activity a
     * chance to save state.
     * 
     * @param outState a Bundle into which this Activity should save its state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // just have the View's thread save its state into our Bundle
        super.onSaveInstanceState(outState);
        mPlanetThread.saveState(outState);
        Log.w(this.getClass().getName(), "SIS called");
    }
}
