package com.beecub.games.planet;


import java.util.ArrayList;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.beecub.games.planet.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.beecub.games.planet.dialogs.CreditDialog;

public class PlanetActivity extends TabActivity {
    
    public static final String PREFS_NAME = "PlanetPrefs";
    public static final String LOG_TAG = "beecub";
    
    public static String mName = "Corn";
    public static String mPackageName;
    
    public static float mLevel = 1;
    public static float mMood;
    public static float mEnvironment;
    public static float mTemperature;
    public static float mTimeMultiplier = 0;
    public static float mResourcePerHour = 1;
    
    public static long mLastLogin; 
    public static float mPopulation = 0;
    public static float mElements = 10;
    
    public static float mElementsPH = 0;
    public static float mPopulationPH = 0;
    
    public static Task mPower;
    public static int mPowerInt = 0;
    public static long mPowerStartTime;
    public static long mPowerCalculationTime;
    public ArrayList<Task> mPowers = new ArrayList<Task>();
    public static ListViewAdapter mAdapter;
    
    public static Typeface mTypeface;    
    static TabHost mTabHost;
    
    private static NotificationManager mNotificationManager;
    private static int mNotifications = 1;
    
    public static Context mContext;
    
    public static int mCurrentPosition;
    
    public static SharedPreferences mSettings;
    public static Editor mEditor;
    
    public static View toastView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        Log.v(LOG_TAG, "0");
        
        mPackageName = getPackageName();
        mContext = getApplicationContext();
        
        LayoutInflater inflater = getLayoutInflater();
        toastView = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout_root));
        
        initData();        
        
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) getSystemService(ns);
        
        mTypeface = Typeface.createFromAsset(getAssets(), "fonts/Geo-Regular.ttf");
        //mTypeface = Typeface.createFromAsset(getAssets(), "fonts/WalterTurncoat.ttf");
        
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
        
        Intent intent;        
        intent = new Intent().setClass(this, OverviewActivity.class);
        setupTab(new TextView(this), getString(R.string.overview), intent, R.layout.tab_bg_overview);
//        intent = new Intent().setClass(this, FactoryActivity.class);
//        setupTab(new TextView(this), getString(R.string.factory), intent, R.layout.tab_bg_factory);
        intent = new Intent().setClass(this, PowersActivity.class);
        setupTab(new TextView(this), getString(R.string.weather), intent, R.layout.tab_bg_powers);
        Animation a = AnimationUtils.loadAnimation(mContext, R.anim.in_animation1);
        mTabHost.setAnimation(a);
        mTabHost.setCurrentTab(0);

        //doToast("test");
        
//        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
//            public void onTabChanged(String tabId) {
//                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.in_animation);
//                LinearLayout layout = (LinearLayout) findViewById(R.layout.tab_bg_overview);
//                layout.setAnimation(animation);
//            }
//        });
        
        mPowers = new ArrayList<Task>();
        mAdapter = new ListViewAdapter(this, R.layout.row, mPowers);
        //mAdapter.notifyDataSetChanged();
        
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
            if(Integer.valueOf(powers[i]) == mPowerInt) {
                mPower = mAdapter.getItem(0);
            }
            i+=10;
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.credits:
                CreditDialog creditDialog = new CreditDialog(this);
                creditDialog.show();
                return true;
            case R.id.settings:
                Intent settingsIntent = new Intent();
                settingsIntent.setClass(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.stop:
                android.os.Process.killProcess(android.os.Process.myPid());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
//        Intent settingsIntent = new Intent();
//        settingsIntent.setClass(this, PlanetActivity.class);
//        startActivity(settingsIntent);
//        android.os.Process.killProcess(android.os.Process.myPid());
        OverviewActivity.mPlanetView.getThread().pause();
    }
    
    @Override
    public void onResume() {
        super.onResume();
    }
    
    @Override
    protected void onStop(){
       super.onStop();
       saveData();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private void setupTab(final View view, final String tag, Intent intent, int tab_bg) {
        View tabview = createTabView(mTabHost.getContext(), tag, tab_bg);
            TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
        mTabHost.addTab(setContent);
    }

    private static View createTabView(final Context context, final String text, int tab_bg) {
        View view = LayoutInflater.from(context).inflate(tab_bg, null);
        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        tv.setTypeface(mTypeface);
        return view;
    }
    
    public static void setPower(int power) {
        if(mPower.getNumber() != power) {
            Task tPower = null;
            boolean found = false;
            for(int j = 0; j < mAdapter.getCount(); j++) {
                if(mAdapter.getItem(j).getNumber() == power) {
                    tPower = mAdapter.getItem(j);
                    found = true;
                }
            }
            if(found) {
                if(tPower.getCost() < mElements) {
                    mElements -= tPower.getCost();
                    
                    mPower = tPower;
                    mPowerInt = power;
                    
                    mPowerStartTime = new Date().getTime();
                    mPowerCalculationTime = mPowerStartTime;
                    
                    saveSingleData("power", mPower.getNumber());
                    saveSingleData("powertime", mPowerStartTime);
                    saveSingleData("powercalctime", mPowerCalculationTime);
                    saveSingleData("resources", mElements);
                    
                    mTabHost.setCurrentTab(0);
                } else {
                    doToast(mContext.getResources().getString(R.string.notenough));
                }
            }
        }
    }
    
    public static void progress(boolean login) {
        
        float difference = 0;
        if(login) {
            difference = new Date().getTime() - mLastLogin;
            mPowerCalculationTime = new Date().getTime();
        } else
            difference = new Date().getTime() - mPowerCalculationTime;
        difference = difference / (1000 * 60 * 0.5f);
        
        if(difference >= 1) {            
            float factor = 0.5f;
            
            if(mPower.getImpactPopulation() == 4) {
                // nothing
            } else if(mPower.getImpactPopulation() == 1) {
                mPopulation -= 3 * difference;
            } else if(mPower.getImpactPopulation() == 2) {
                mPopulation -= 2 * difference;
            } else if(mPower.getImpactPopulation() == 3) {
                mPopulation -= 1 * difference;
            } else if(mPower.getImpactPopulation() == 5) {
                mPopulation += 1 * difference;
            } else if(mPower.getImpactPopulation() == 6) {
                mPopulation += 2 * difference;
            } else if(mPower.getImpactPopulation() == 7) {
                mPopulation += 3 * difference;
            }
            if(mPopulation < 0)
                mPopulation = 0;
            
            if(mPower.getImpactMood() == 4) {
                // nothing
            } else if(mPower.getImpactMood() == 1) {
                mMood -= (3 * difference) * factor;
            } else if(mPower.getImpactMood() == 2) {
                mMood -= (2 * difference) * factor;
            } else if(mPower.getImpactMood() == 3) {
                mMood -= (1 * difference) * factor;
            } else if(mPower.getImpactMood() == 5) {
                mMood += (1 * difference) * factor;
            } else if(mPower.getImpactMood() == 6) {
                mMood += (2 * difference) * factor;
            } else if(mPower.getImpactMood() == 7) {
                mMood += (3 * difference) * factor;
            }
            if(mMood < 0)
                mMood = 0;
            if(mMood > 100)
                mMood = 100;
            
            if(mPower.getImpactTemperature() == 4) {
                // nothing
            } else if(mPower.getImpactTemperature() == 1) {
                mTemperature -= (3 * difference) * factor;
            } else if(mPower.getImpactTemperature() == 2) {
                mTemperature -= (2 * difference) * factor;
            } else if(mPower.getImpactTemperature() == 3) {
                mTemperature -= (1 * difference) * factor;
            } else if(mPower.getImpactTemperature() == 5) {
                mTemperature += (1 * difference) * factor;
            } else if(mPower.getImpactTemperature() == 6) {
                mTemperature += (2 * difference) * factor;
            } else if(mPower.getImpactTemperature() == 7) {
                mTemperature += (3 * difference) * factor;
            }
            if(mTemperature < 0)
                mTemperature = 0;
            if(mTemperature > 100)
                mTemperature = 100;
            
            if(mPower.getImpactEnvironment() == 4) {
                // nothing
            } else if(mPower.getImpactEnvironment() == 1) {
                mEnvironment -= (3 * difference) * factor;
            } else if(mPower.getImpactEnvironment() == 2) {
                mEnvironment -= (2 * difference) * factor;
            } else if(mPower.getImpactEnvironment() == 3) {
                mEnvironment -= (1 * difference) * factor;
            } else if(mPower.getImpactEnvironment() == 5) {
                mEnvironment += (1 * difference) * factor;
            } else if(mPower.getImpactEnvironment() == 6) {
                mEnvironment += (2 * difference) * factor;
            } else if(mPower.getImpactEnvironment() == 7) {
                mEnvironment += (3 * difference) * factor;
            }
            if(mEnvironment < 0)
                mEnvironment = 0;
            if(mEnvironment > 100)
                mEnvironment = 100;
            
            factor = 0.1f;
            mElementsPH = 0;
            if(mPower.getImpactElements() == 4) {
                // nothing
            } else if(mPower.getImpactElements() == 1) {
                mElementsPH = (3 * difference) * factor;           
            } else if(mPower.getImpactElements() == 2) {
                mElementsPH = (2 * difference) * factor;
            } else if(mPower.getImpactElements() == 3) {
                mElementsPH = (1 * difference) * factor;
            } else if(mPower.getImpactElements() == 5) {
                mElementsPH = (1 * difference) * factor;
            } else if(mPower.getImpactElements() == 6) {
                mElementsPH = (2 * difference) * factor;
            } else if(mPower.getImpactElements() == 7) {
                mElementsPH = (3 * difference) * factor;
            }
            // regular income
            mElementsPH += difference * 0.333333f;
            
            mElements += mElementsPH;
            mElementsPH *= 30;
            
            if(mElements < 0)
                mElements = 0;
            if(mElements > 100)
                mElements = 100;
            
            
            mPowerCalculationTime = new Date().getTime();
            saveData();
        }
    }
    
    public void notification(String text) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();

        Notification notification = new Notification(icon, text, when);
        
        Context context = getApplicationContext();
        CharSequence contentTitle = "Planet";
        CharSequence contentText = text;
        Intent notificationIntent = new Intent(this, PlanetActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        mNotifications++;
        mNotificationManager.notify(mNotifications, notification);
    }
    
    public static void doToast(String text) {        
        ImageView image = (ImageView) toastView.findViewById(R.id.toast_image);
        image.setImageResource(R.drawable.ic_launcher);
        TextView tv = (TextView) toastView.findViewById(R.id.toast_text);
        tv.setTypeface(mTypeface);
        tv.setText(text);

        Toast toast = new Toast(mContext);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastView);
        toast.show();
    }
    
    private void initData() { 
        mSettings = getSharedPreferences(PREFS_NAME, 0);
        mEditor = mSettings.edit();
        
        
        mName = mSettings.getString("name", "Corn");
        
        mPowerInt = mSettings.getInt("power", 0);
        
        mLastLogin = mSettings.getLong("lastlogin", new Date().getTime());
        
        mLevel = mSettings.getFloat("level", 1);
        mMood = mSettings.getFloat("mood", 50);
        mEnvironment = mSettings.getFloat("environment", 50);
        mTemperature = mSettings.getFloat("faith", 50);    
        mPopulation = mSettings.getFloat("population", 0);
        mElements = mSettings.getFloat("resources", 10);
        mPowerStartTime = mSettings.getLong("powertime", new Date().getTime());
        
        progress(true);
    }
    
    private static void saveData() {                
        mEditor.putString("name", mName);
        
        mEditor.putInt("power", mPowerInt);
        
        mEditor.putLong("lastlogin", new Date().getTime());
        
        mEditor.putFloat("level", mLevel);
        mEditor.putFloat("mood", mMood);
        mEditor.putFloat("environment", mEnvironment);
        mEditor.putFloat("faith", mTemperature);    
        mEditor.putFloat("population", mPopulation);
        mEditor.putFloat("resources", mElements);
        mEditor.putLong("powertime", mPowerStartTime);
        mEditor.commit();
    }
    
    public static void saveSingleData(String name, String data) {     
        mEditor.putString(name, data);
        mEditor.commit();
    }
    
    public static void saveSingleData(String name, float data) {    
        mEditor.putFloat(name, data);
        mEditor.commit();
    }
    
    public static void saveSingleData(String name, long data) {
        mEditor.putLong(name, data);
        mEditor.commit();
    }
    
    public static void saveSingleData(String name, int data) {                
        mEditor.putInt(name, data);
        mEditor.commit();
    }
    
    public static void saveSingleData(String name, Boolean data) {               
        mEditor.putBoolean(name, data);
        mEditor.commit();
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
                    int resID = getResources().getIdentifier("impact_" + o.getImpactElements(), "drawable", getPackageName());
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
        private int mImpactElements;
        private Drawable mIcon;
        
        public Task(int number, String name, String description, long cost, Drawable image, int impactPopulation, int impactMood, int impactTemperature, int impactEnvironment, int impactElements) {
            this.mNumber = number;
            this.mName = name;
            this.mDescription = description;
            this.mCost = cost;
            this.mIcon = image;
            this.mImpactPopulation = impactPopulation;
            this.mImpactMood = impactMood;
            this.mImpactTemperature = impactTemperature;
            this.mImpactEnvironment = impactEnvironment;
            this.mImpactElements = impactElements;
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
        public int getImpactElements() {
            return this.mImpactElements;
        }
        public Drawable getDrawable() {
            return this.mIcon;
        }
    }
}