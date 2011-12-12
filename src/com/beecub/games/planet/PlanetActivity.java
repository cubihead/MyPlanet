package com.beecub.games.planet;


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
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    
    public static int mPower = 0;
    public static float mPowerStartTime;
    
    public static Typeface mTypeface;    
    static TabHost mTabHost;
    
    private static NotificationManager mNotificationManager;
    private static int mNotifications = 1;
    
    public static Context mContext;
    
    public static int mCurrentPosition;
    
    public static SharedPreferences mSettings;
    public static Editor mEditor;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        Log.v(LOG_TAG, "0");
        
        mPackageName = getPackageName();
        mContext = getApplicationContext();
        
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
        mPower = power;
        mPowerStartTime = new Date().getTime();
        
        saveSingleData("power", mPower);
        saveSingleData("powertime", mPowerStartTime);
    }
    
    public static void progress() {
        
        float difference = new Date().getTime() - mLastLogin;
        difference = difference / (1000 * 60 * 0.5f);
        if(difference >= 1) {
            long impactPopulation = 0;
            long impactMood = 0;
            long impactTemperature = 0;
            long impactEnvironment = 0;
            long impactElements = 0;
            
            String number = String.valueOf(mPower);
            if(number.length() < 3) {
                number = "0" + number;
            }
            if(number.length() < 3) {
                number = "0" + number;
            }
            boolean found = false;
            
            String[] powers = mContext.getResources().getStringArray(R.array.powers);
            int i = 0;
            while(i < powers.length && !found) {
                if(powers[i].equalsIgnoreCase(number)) {
                    impactPopulation = Long.valueOf(powers[i+5]);
                    impactMood = Long.valueOf(powers[i+6]);
                    impactTemperature = Long.valueOf(powers[i+7]);
                    impactEnvironment = Long.valueOf(powers[i+8]);
                    impactElements = Long.valueOf(powers[i+9]);
                    found = true;
                }
                i+=10;
            }
            if(found) {
                if(impactPopulation == 4) {
                    // nothing
                } else if(impactPopulation == 1) {
                    mPopulation -= 3 * difference;
                } else if(impactPopulation == 2) {
                    mPopulation -= 2 * difference;
                } else if(impactPopulation == 3) {
                    mPopulation -= 1 * difference;
                } else if(impactPopulation == 5) {
                    mPopulation += 1 * difference;
                } else if(impactPopulation == 6) {
                    mPopulation += 2 * difference;
                } else if(impactPopulation == 7) {
                    mPopulation += 3 * difference;
                }
            }
            
            mLastLogin = new Date().getTime();
            saveSingleData("lastlogin", mLastLogin);
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
    
    public void doToast(String text) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout_root));

        ImageView image = (ImageView) layout.findViewById(R.id.toast_image);
        image.setImageResource(R.drawable.ic_launcher);
        TextView tv = (TextView) layout.findViewById(R.id.toast_text);
        tv.setText(text);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
    
    private void initData() { 
        mSettings = getSharedPreferences(PREFS_NAME, 0);
        mEditor = mSettings.edit();
        
        
        mName = mSettings.getString("name", "Corn");
        
        mPower = mSettings.getInt("power", 0);
        
        mLastLogin = mSettings.getLong("lastlogin", new Date().getTime());
        
        mLevel = mSettings.getFloat("level", 1);
        mMood = mSettings.getFloat("mood", 50);
        mEnvironment = mSettings.getFloat("environment", 50);
        mTemperature = mSettings.getFloat("faith", 50);    
        mPopulation = mSettings.getFloat("population", 0);
        mElements = mSettings.getFloat("resources", 10);
        mPowerStartTime = mSettings.getFloat("powertime", 0);
        
        progress();
    }
    
    private static void saveData() {                
        mEditor.putString("name", mName);
        
        mEditor.putInt("power", mPower);
        
        mEditor.putFloat("level", mLevel);
        mEditor.putFloat("mood", mMood);
        mEditor.putFloat("environment", mEnvironment);
        mEditor.putFloat("faith", mTemperature);    
        mEditor.putFloat("population", mPopulation);
        mEditor.putFloat("resources", mElements);
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
}