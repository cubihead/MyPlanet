package com.beecub.games.planet;

import java.util.ArrayList;

import android.app.Activity;
import android.beecub.games.planet.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PowersActivity extends Activity {
    
    public ArrayList<Task> mpowers = new ArrayList<Task>();
    public static ListViewAdapter mAdapter;
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
        
        mpowers = new ArrayList<Task>();
        mAdapter = new ListViewAdapter(this, R.layout.row, mpowers);
        //mAdapter.notifyDataSetChanged();
        
        ListView lv = (ListView) findViewById(R.id.list_task);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                
                PlanetActivity.mCurrentPosition = position;
                
                PlanetActivity.setPower(position + 1);
            }
        });
        lv.setSelection(PlanetActivity.mCurrentPosition);
        
        Resources res = getResources();
        String[] powers = res.getStringArray(R.array.powers);
        mAdapter.clear();
        
        int i = 0;
        while(i < powers.length) {
            int resID = getResources().getIdentifier("power_" + powers[i+4], "drawable", getPackageName());
            Drawable icon;
            if(resID == 0)
                icon = res.getDrawable(R.drawable.icon);
            else
                icon = res.getDrawable(resID);
            
            mAdapter.insert(new Task(Integer.valueOf(powers[i]), powers[i+1], powers[i+2], Long.valueOf(powers[i+3]), icon, Integer.valueOf(powers[i+5]), Integer.valueOf(powers[i+6]), Integer.valueOf(powers[i+7]), Integer.valueOf(powers[i+8]), Integer.valueOf(powers[i+9])), 0);
            i+=10;
        }
        lv.setSelection(PlanetActivity.mCurrentPosition);
    }
    
    public class ListViewAdapter extends ArrayAdapter<Task> {
        
        private ArrayList<Task> items;
        
        public ListViewAdapter(Context context, int textViewResourceId, ArrayList<Task> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row, null);
            }
            Task o = items.get(position);
            if (o != null) {
                TextView tv1 = (TextView) v.findViewById(R.id.name);
                TextView tv2 = (TextView) v.findViewById(R.id.cost);
                //TextView tv3 = (TextView) v.findViewById(R.id.description);
                TextView tvi1 = (TextView) v.findViewById(R.id.impactPopulationText);
                TextView tvi2 = (TextView) v.findViewById(R.id.impactMoodText);
                TextView tvi3 = (TextView) v.findViewById(R.id.impactFaithText);
                TextView tvi4 = (TextView) v.findViewById(R.id.impactEnvironmentText);
                TextView tvi5 = (TextView) v.findViewById(R.id.impactManaText);
                
                ImageView iv1 = (ImageView) v.findViewById(R.id.rowicon);                
                ImageView ivi1 = (ImageView) v.findViewById(R.id.impactPopulationImage);
                ImageView ivi2 = (ImageView) v.findViewById(R.id.impactMoodImage);
                ImageView ivi3 = (ImageView) v.findViewById(R.id.impactFaithImage);
                ImageView ivi4 = (ImageView) v.findViewById(R.id.impactEnvironmentImage);
                ImageView ivi5 = (ImageView) v.findViewById(R.id.impactManaImage);
                
                if(tv1 != null) {
                    tv1.setText(o.getName());
                    tv1.setTypeface(PlanetActivity.mTypeface);
                }
                if(tv2 != null) {
                    tv2.setText(String.valueOf(o.getCost()));
                    tv2.setTypeface(PlanetActivity.mTypeface);
                }
//                if(tv3 != null) {
//                    tv3.setText(o.getDescription());
//                    tv3.setTypeface(PlanetActivity.mTypeface);
//                }
                if(tvi1 != null) {
                    tvi1.setTypeface(PlanetActivity.mTypeface);
                }
                if(tvi2 != null) {
                    tvi2.setTypeface(PlanetActivity.mTypeface);
                }
                if(tvi3 != null) {
                    tvi3.setTypeface(PlanetActivity.mTypeface);
                }
                if(tvi4 != null) {
                    tvi4.setTypeface(PlanetActivity.mTypeface);
                }
                if(tvi5 != null) {
                    tvi5.setTypeface(PlanetActivity.mTypeface);
                }
                
                
                if(iv1 != null) {
                    iv1.setBackgroundDrawable(o.getIcon());
                    iv1.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.powers_border));
                }
                
                if(ivi1 != null) {
                    int resID = getResources().getIdentifier("impact_" + o.getImpactPopulation(), "drawable", getPackageName());
                    Drawable icon = getResources().getDrawable(resID);
                    ivi1.setImageDrawable(icon);
                }
                if(ivi2 != null) {
                    int resID = getResources().getIdentifier("impact_" + o.getImpactMood(), "drawable", getPackageName());
                    Drawable icon = getResources().getDrawable(resID);
                    ivi2.setImageDrawable(icon);
                }
                if(ivi3 != null) {
                    int resID = getResources().getIdentifier("impact_" + o.getImpactTemperature(), "drawable", getPackageName());
                    Drawable icon = getResources().getDrawable(resID);
                    ivi3.setImageDrawable(icon);
                }
                if(ivi4 != null) {
                    int resID = getResources().getIdentifier("impact_" + o.getImpactEnvironment(), "drawable", getPackageName());
                    Drawable icon = getResources().getDrawable(resID);
                    ivi4.setImageDrawable(icon);
                }
                if(ivi5 != null) {
                    int resID = getResources().getIdentifier("impact_" + o.getImpactMana(), "drawable", getPackageName());
                    Drawable icon = getResources().getDrawable(resID);
                    ivi5.setImageDrawable(icon);
                }
            }
            return v;
        }
    }
    
    public class Task {
        private int mNumber;
        private String mName;
        private String mDescription;
        private long mCost;
        private int mImpactPopulation;
        private int mImpactMood;
        private int mImpactTemperature;
        private int mImpactEnvironment;
        private int mImpactMana;
        private Drawable mIcon;
        
        public Task(int number, String name, String description, long cost, Drawable image, int impactPopulation, int impactMood, int impactFaith, int impactEnvironment, int impactMana) {
            this.mNumber = number;
            this.mName = name;
            this.mDescription = description;
            this.mCost = cost;
            this.mIcon = image;
            this.mImpactPopulation = impactPopulation;
            this.mImpactMood = impactMood;
            this.mImpactTemperature = impactFaith;
            this.mImpactEnvironment = impactEnvironment;
            this.mImpactMana = impactMana;
        }
        
        public int getNumber() {
            return this.mNumber;
        }
        public String getName() {
            return this.mName;
        }
        public String getDescription() {
            return this.mDescription;
        }
        public long getCost() {
            return this.mCost;
        }
        public Drawable getIcon() {
            return this.mIcon;
        }
        public int getImpactPopulation() {
            return this.mImpactPopulation;
        }
        public int getImpactMood() {
            return this.mImpactMood;
        }
        public int getImpactTemperature() {
            return this.mImpactTemperature;
        }
        public int getImpactEnvironment() {
            return this.mImpactEnvironment;
        }
        public int getImpactMana() {
            return this.mImpactMana;
        }
    }
}