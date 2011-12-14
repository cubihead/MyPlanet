package com.beecub.games.planet;

import android.app.Activity;
import android.beecub.games.planet.R;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PowersActivity extends Activity {
    
    private float oldTouchValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.powers);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        OverviewActivity.mPlanetView.getThread().pause();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        Log.v("beecub", "onTouchEvent");
        Log.v("beecub", String.valueOf(touchevent.getAction()));
        switch (touchevent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                Log.v("beecub", "down");
                oldTouchValue = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                Log.v("beecub", "up");
                float currentX = touchevent.getX();
                if (oldTouchValue < currentX)
                {
                }
                if (oldTouchValue > currentX)
                {
                }
                break;
            case MotionEvent.ACTION_MOVE : // Contact has moved across screen
                Log.v("beecub", "move");
            case MotionEvent.ACTION_CANCEL : // Touch event cancelled
                 Log.v("beecub", "cancel");
        }
        return false;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        ListView lv = (ListView) findViewById(R.id.list_task);
        lv.setAdapter(PlanetActivity.mAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                
                PlanetActivity.mCurrentPosition = position;
                
                PlanetActivity.setPower(position);
            }
        });
        lv.setSelection(PlanetActivity.mCurrentPosition);
    }
}