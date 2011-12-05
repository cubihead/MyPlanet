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

public class PlanetActivity extends TabActivity {
    
    public static final String PREFS_NAME = "PlanetPrefs";
    public static final String LOG_TAG = "beecub";
    
    public static String mName = "Corn";
    
    public static int mLevel = 1;
    public static int mMood;
    public static int mEnvironment;
    public static int mFaith;
    public static int mTimeMultiplier = 0;
    public static int mResourcePerHour = 1;
    
    public static long mLastLogin; 
    public static long mPopulation = 0;
    public static long mMana = 10;
    
    public static int mPower;
    public static long mPowerStartTime;
    
    public static Typeface mTypeface;    
    static TabHost mTabHost;
    
    private static NotificationManager mNotificationManager;
    private static int mNotifications = 1;
    
    public static Context mContext;
    
    public static int mCurrentPosition;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        Log.v(LOG_TAG, "0");
        
        initData();
        
        mContext = getApplicationContext();
        
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
        setupTab(new TextView(this), getString(R.string.powers), intent, R.layout.tab_bg_powers);
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
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        //finish();
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
    
    @Override
    protected void onStop(){
       super.onStop();
       saveData();
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
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        
        mName = settings.getString("name", "Corn");
        
        mLevel = settings.getInt("level", 1);
        mMood = settings.getInt("mood", 50);
        mEnvironment = settings.getInt("environment", 50);
        mFaith = settings.getInt("faith", 50);
        mPower = settings.getInt("power", 0);
        
        mPopulation = settings.getLong("population", 0);
        mLastLogin = settings.getLong("lastlogin", 0);
        mMana = settings.getLong("resources", 10);
        mPowerStartTime = settings.getLong("powertime", 0);
        
//        mPower = 1;
//        mPowerStartTime = new Date().getTime();                
    }
    
    private void saveData() {
        Date currentDate = new Date();
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
                
        editor.putString("name", mName);
        
        editor.putInt("level", mLevel);
        editor.putInt("mood", mMood);
        editor.putInt("environment", mEnvironment);
        editor.putInt("faith", mFaith);
        editor.putInt("power", mPower);
        
        editor.putLong("population", mPopulation);
        editor.putLong("lastlogin", currentDate.getTime());
        editor.putLong("resources", mMana);
        editor.putLong("powertime", mPowerStartTime);
    }
    
    public void saveSingleData(String name, String data) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
                
        editor.putString(name, data);
    }
    
    public void saveSingleData(String name, float data) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
                
        editor.putFloat(name, data);   
    }
    
    public void saveSingleData(String name, long data) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
                
        editor.putLong(name, data);
    }
    
    public void saveSingleData(String name, int data) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
                
        editor.putInt(name, data);
    }
    
    public void saveSingleData(String name, Boolean data) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
                
        editor.putBoolean(name, data);
    }
    
    
    
}