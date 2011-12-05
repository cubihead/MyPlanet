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
    
    public ArrayList<Task> mTasks = new ArrayList<Task>();
    public ListViewAdapter mAdapter;
    private int currentPosition;
    private float oldTouchValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.powers);
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
    
    public void onResume() {
        super.onResume();
        
        mTasks = new ArrayList<Task>();
        this.mAdapter = new ListViewAdapter(this, R.layout.row, mTasks);
        //mAdapter.notifyDataSetChanged();
        
        ListView lv = (ListView) findViewById(R.id.list_task);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                
                currentPosition = position;
                //finish();
                
            }
        });
        lv.setSelection(currentPosition);
        
        Resources res = getResources();
        String[] tasks = res.getStringArray(R.array.powers);
        mAdapter.clear();
        
        int i = 0;
        while(i < tasks.length) {
            int resID = getResources().getIdentifier("task_resource_" + tasks[i], "drawable", getPackageName());
            Drawable icon;
            if(resID == 0) 
                icon = res.getDrawable(R.drawable.icon);
            else
                icon = res.getDrawable(resID);
            
            mAdapter.add(new Task(Integer.valueOf(tasks[i]), tasks[i+1], tasks[i+2], Long.valueOf(tasks[i+3]), icon));
            i+=4;
        }
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
                TextView tv3 = (TextView) v.findViewById(R.id.description);
                ImageView iv1 = (ImageView) v.findViewById(R.id.rowicon);
                
                if(tv1 != null) {
                    tv1.setText(o.getName());
                    tv1.setTypeface(PlanetActivity.mTypeface);
                }
                if(tv2 != null) {
                    tv2.setText(String.valueOf(o.getCost()));
                    tv2.setTypeface(PlanetActivity.mTypeface);
                }
                if(tv3 != null) {
                    tv3.setText(o.getDescription());
                    tv3.setTypeface(PlanetActivity.mTypeface);
                }
                if(iv1 != null) {
                    iv1.setBackgroundDrawable(o.getIcon());
                    iv1.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.research_border));
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
        private Drawable mIcon;
        
        public Task(int number, String name, String description, long cost, Drawable image) {
            this.mNumber = number;
            this.mName = name;
            this.mDescription = description;
            this.mCost = cost;
            this.mIcon = image;
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
    }
}