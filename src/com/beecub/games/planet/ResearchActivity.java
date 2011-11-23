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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ResearchActivity extends Activity {
    
    public static ArrayList<Technology> mTechnologies = new ArrayList<Technology>();
    public static FontAdapter madapter;
    private int currentPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.research);
    }
    
    @SuppressWarnings("static-access")
    public void onResume() {
        super.onResume();
        
        mTechnologies = new ArrayList<Technology>();
        this.madapter = new FontAdapter(this, R.layout.row, mTechnologies);
        //madapter.notifyDataSetChanged();
        
        ListView lv = (ListView) findViewById(R.id.researchlist);
        lv.setAdapter(madapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                
//                Intent intent = new Intent(GOLauncherFontsActivity.this, DetailedPreviewActivity.class);
//                Bundle b = new Bundle();
//                b.putString("name", madapter.getItem(position).getName());
//                b.putString("author", madapter.getItem(position).getAuthor());
//                b.putString("license", madapter.getItem(position).getLicense());
//                b.putString("link", madapter.getItem(position).getLink());
//                b.putString("typeface", madapter.getItem(position).getTypeface());
//                intent.putExtras(b);
//                startActivity(intent);
                
                currentPosition = position;
                finish();
                
            }
        });
        lv.setSelection(currentPosition);
        
        Resources res = getResources();
        String[] technologies = res.getStringArray(R.array.technologies);
        
        int i = 6;
        while(i < technologies.length) {
            int resID = getResources().getIdentifier("research_" + technologies[i], "drawable", getPackageName());
            Drawable icon;
            if(resID == 0) 
                icon = res.getDrawable(R.drawable.icon);
            else
                icon = res.getDrawable(resID);
            Log.v("beecub", i + " : " + technologies.length);
            madapter.add(new Technology(Integer.valueOf(technologies[i]), technologies[i+1], technologies[i+2], Integer.valueOf(technologies[i+3]), Long.valueOf(technologies[i+4]), Long.valueOf(technologies[i+5]), icon));
            i = i + 6;
        } 
    }
    
    
    public class FontAdapter extends ArrayAdapter<Technology> {
        
        private ArrayList<Technology> items;
        
        public FontAdapter(Context context, int textViewResourceId, ArrayList<Technology> items) {
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
            Technology o = items.get(position);
            if (o != null) {
                TextView tv1 = (TextView) v.findViewById(R.id.name);
                TextView tv2 = (TextView) v.findViewById(R.id.duration);
                TextView tv3 = (TextView) v.findViewById(R.id.cost);
                TextView tv4 = (TextView) v.findViewById(R.id.description);
                ImageView iv1 = (ImageView) v.findViewById(R.id.rowicon);
                
                if(tv1 != null) {
                    tv1.setText(o.getName());
                }
                if(tv2 != null) {
                    tv2.setText(String.valueOf(o.getDuration()));
                }
                if(tv3 != null) {
                    tv3.setText(String.valueOf(o.getCost()));
                }
                if(tv4 != null) {
                    tv4.setText(o.getDescription());
                }
                if(iv1 != null) {
                    iv1.setBackgroundDrawable(o.getIcon());
                    iv1.setImageDrawable(this.getContext().getResources().getDrawable(R.drawable.research_border));
                }
            }
            return v;
        }
    }
    
    public class Technology {
        public int mNumber;
        public String mName;
        public String mDescription;
        public int mLevel;
        public long mDuration;
        public long mCost;
        Drawable mIcon;
        
        public Technology(int number, String name, String description, int level, long duration, long cost, Drawable image) {
            this.mNumber = number;
            this.mName = name;
            this.mDescription = description;
            this.mLevel = level;
            this.mDuration = duration;
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
        public int getLevel() {
            return this.mLevel;
        }
        public long getDuration() {
            return this.mDuration;
        }
        public long getCost() {
            return this.mCost;
        }
        public Drawable getIcon() {
            return this.mIcon;
        }
    }
}
