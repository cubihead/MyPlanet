package com.beecub.games.planet;

import java.util.Calendar;

import android.beecub.games.planet.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class PlanetView extends SurfaceView implements SurfaceHolder.Callback {    
    class PlanetThread extends Thread {
        
        public static final int STATE_RUNNING = 1;
        public static final int STATE_PAUSE = 2;
        
        private static final String KEY_Planet_HEIGHT = "mPlanetHeight";
        private static final String KEY_Planet_WIDTH = "mPlanetWidth";
        private static final String KEY_Moon_HEIGHT = "mMoonHeight";
        private static final String KEY_Moon_WIDTH = "mMoonWidth";
        private static final String KEY_Sun_HEIGHT = "mSunHeight";
        private static final String KEY_Sun_WIDTH = "mSunWidth";
        
        private int mCanvasHeight = 1;
        private int mCanvasWidth = 1;
        
        private SurfaceHolder mSurfaceHolder;
        
        // background
        private Bitmap mBackgroundImage;
        private float mBackgroundOffset = 0.0F;
        
        // darkness
        private Bitmap mDarknessImage;
        
        // planet
        private Drawable mPlanetImage;
        private Drawable mPlanetSurface;
        private Drawable mPlanetBorder;
        private Drawable mPlanetPopulation;
        private int mPlanetHeight;
        private int mPlanetWidth;
        
        // moon
        private Drawable mMoonImage;
        private int mMoonHeight;
        private int mMoonWidth;
        
        // sun
        private Drawable mSunImage;
        private int mSunHeight;
        private int mSunWidth;
        
        
        private double mDaytime;
        private boolean bDaytime = false;
        
        private float mRotation;
        
        private int mMode;
        
        private boolean mRun = false;
        
        private Resources mResources;
        
        // bars
        private Bar mHappinessBar;
        private Bar mEnvironmentBar;
        private Bar mEnergyBar;
        
        
        public PlanetThread(SurfaceHolder surfaceHolder, Context context,
                Handler handler) {
            
            mSurfaceHolder = surfaceHolder;
            //mContext = context;

            mResources = context.getResources();

            // planet
            mPlanetImage = mResources.getDrawable(R.drawable.planet);
            if(PlanetActivity.mEnvironment < 30) 
                mPlanetSurface = mResources.getDrawable(R.drawable.planet_surface_low);
            else if(PlanetActivity.mEnvironment >= 70)
                mPlanetSurface = mResources.getDrawable(R.drawable.planet_surface_full);
            else
                mPlanetSurface = mResources.getDrawable(R.drawable.planet_surface_half);
            mPlanetBorder = mResources.getDrawable(R.drawable.planet_border);
            
            // moon
            mMoonImage = mResources.getDrawable(R.drawable.moon);
            
            // sun
            mSunImage = mResources.getDrawable(R.drawable.sun);

            
            mBackgroundImage = BitmapFactory.decodeResource(mResources,
                    R.drawable.background_overview); 
            mDarknessImage  = BitmapFactory.decodeResource(mResources,
                    R.drawable.dark);
            
        }
        
        public void doStart() {
            synchronized (mSurfaceHolder) {
                mDaytime = 0;                
                setState(STATE_RUNNING);
            }
        }
        
        public void pause() {
            synchronized (mSurfaceHolder) {
                if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
            }
        }
        
        /**
         * Restores game state from the indicated Bundle. Typically called when
         * the Activity is being restored after having been previously
         * destroyed.
         * 
         * @param savedState Bundle containing the game state
         */
        public synchronized void restoreState(Bundle savedState) {
            synchronized (mSurfaceHolder) {
                setState(STATE_PAUSE);

                mPlanetWidth = savedState.getInt(KEY_Planet_WIDTH);
                mPlanetHeight = savedState.getInt(KEY_Planet_HEIGHT);
                
                mMoonWidth = savedState.getInt(KEY_Moon_WIDTH);
                mMoonHeight = savedState.getInt(KEY_Moon_HEIGHT);
                
                mSunWidth = savedState.getInt(KEY_Sun_WIDTH);
                mSunHeight = savedState.getInt(KEY_Sun_HEIGHT);
            }
        }
        
        /**
         * Dump game state to the provided Bundle. Typically called when the
         * Activity is being suspended.
         * 
         * @return Bundle with this view's state
         */
        public Bundle saveState(Bundle map) {
            synchronized (mSurfaceHolder) {
                if (map != null) {
                    map.putInt(KEY_Planet_WIDTH, Integer.valueOf(mPlanetWidth));
                    map.putInt(KEY_Planet_HEIGHT, Integer.valueOf(mPlanetHeight));
                    map.putInt(KEY_Moon_WIDTH, Integer.valueOf(mMoonWidth));
                    map.putInt(KEY_Moon_HEIGHT, Integer.valueOf(mMoonHeight));
                    map.putInt(KEY_Sun_WIDTH, Integer.valueOf(mSunWidth));
                    map.putInt(KEY_Sun_HEIGHT, Integer.valueOf(mSunHeight));
                }
            }
            return map;
        }
        
        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         * 
         * @see #setState(int, CharSequence)
         * @param mode one of the STATE_* constants
         */
        public void setState(int mode) {
            synchronized (mSurfaceHolder) {
                setState(mode, null);
            }
        }
        
        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         * 
         * @param mode one of the STATE_* constants
         * @param message string to add to screen or null
         */
        public void setState(int mode, CharSequence message) {
            synchronized (mSurfaceHolder) {
                mMode = mode;

                if (mMode == STATE_RUNNING) {
                    Bundle b = new Bundle();
                    b.putString("text", "");
                    b.putInt("viz", View.INVISIBLE);
                } else {
                    Bundle b = new Bundle();
                    b.putString("text", "testtext_setState");
                    b.putInt("viz", View.VISIBLE);
                }
            }
        }
        
        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        doDraw(c);
                    }
                } finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
        
        public void setRunning(boolean b) {
            mRun = b;
        }
        
        /* Callback invoked when the surface dimensions change. */
        @SuppressWarnings("static-access")
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height; 
                
                mPlanetWidth = (int)(width / 1.5);
                mPlanetHeight = mPlanetWidth;
                
                mMoonWidth = mPlanetWidth / 4;
                mMoonHeight = mPlanetHeight / 4;
                
                mSunWidth = mPlanetWidth / 4;
                mSunHeight = mPlanetHeight / 4;               
                
                mBackgroundImage = mBackgroundImage.createScaledBitmap(
                        mBackgroundImage, width, height, true);
                mDarknessImage = mDarknessImage.createScaledBitmap(
                        mDarknessImage, width, height, true);
            }
        }
        
        /**
         * Draws the ship, fuel/speed bars, and background to the provided
         * Canvas.
         */
        private void doDraw(Canvas canvas) {
            
            Log.v("beecub", "doDraw");
            
            drawBackground(canvas);            
            canvas.save();
            
            //Log.v(Planet.LOG_TAG, "Time: " + calendar.get(Calendar.HOUR_OF_DAY));
            
            //Log.v(Planet.LOG_TAG, "Canvas Width: " + mCanvasWidth + "| Planet Width: " + mPlanetWidth);
            //Log.v(Planet.LOG_TAG, "Canvas Height: " + mCanvasHeight + "| Planet Height: " + mPlanetHeight);
            
            mPlanetImage.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, 
                    mCanvasHeight / 2 - mPlanetHeight / 2, 
                    mCanvasWidth / 2 + mPlanetWidth / 2, 
                    mCanvasHeight / 2 + mPlanetHeight / 2);
            mPlanetSurface.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, 
                    mCanvasHeight / 2 - mPlanetHeight / 2, 
                    mCanvasWidth / 2 + mPlanetWidth / 2, 
                    mCanvasHeight / 2 + mPlanetHeight / 2);
            mPlanetBorder.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, 
                    mCanvasHeight / 2 - mPlanetHeight / 2, 
                    mCanvasWidth / 2 + mPlanetWidth / 2, 
                    mCanvasHeight / 2 + mPlanetHeight / 2);
            
            canvas.rotate(mRotation, mCanvasWidth / 2, mCanvasHeight / 2);
            
            mRotation += 0.03;
            if(mRotation >= 360) mRotation = 0;
            
            mPlanetImage.draw(canvas);
            mPlanetSurface.draw(canvas);
            if(!bDaytime)
                drawPopulation(canvas);
            canvas.save();
            mPlanetBorder.draw(canvas);
            
            canvas.restore();
            
            drawMoonSun(canvas);
            if(bDaytime)
                drawPopulation(canvas);
            
            drawBars(canvas);
        }
        
        private void drawPopulation(Canvas canvas) {
            canvas.save();
            if(bDaytime) {
                if(PlanetActivity.mPopulation < 1000)
                    mPlanetPopulation = mResources.getDrawable(R.drawable.planet_popuatlion_1);
                Log.v("beecub", "draw ");
//                else if(PlanetActivity.mPopulation < 10000)
//                    mPlanetPopulation = mResources.getDrawable(R.drawable.planet_popuatlion_2);
                canvas.rotate(mRotation, mCanvasWidth / 2, mCanvasHeight / 2);
            } else {
                if(PlanetActivity.mPopulation < 1000)
                    mPlanetPopulation = mResources.getDrawable(R.drawable.planet_popuatlion_1_night);
//                else if(PlanetActivity.mPopulation < 10000)
//                    mPlanetPopulation = mResources.getDrawable(R.drawable.planet_popuatlion_2);
            }
            mPlanetPopulation.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, 
                    mCanvasHeight / 2 - mPlanetHeight / 2, 
                    mCanvasWidth / 2 + mPlanetWidth / 2, 
                    mCanvasHeight / 2 + mPlanetHeight / 2);
            mPlanetPopulation.draw(canvas);
            canvas.restore();
        }
        
        private void drawBars(Canvas canvas) {
            // happiness bar
            if(PlanetActivity.mMood < 30)
                mHappinessBar = new Bar(false, 66, false, mResources.getDrawable(R.drawable.icon_face_angry));
            else if(PlanetActivity.mMood >= 70)
                mHappinessBar = new Bar(false, 66, false, mResources.getDrawable(R.drawable.icon_face_grin));
            else
                mHappinessBar = new Bar(false, 66, false, mResources.getDrawable(R.drawable.icon_face_plain));
            mHappinessBar.setPercent(PlanetActivity.mMood / 100.0F);
            mHappinessBar.onDraw(canvas);
            
            // energy bar
            mEnergyBar = new Bar(false, 43, false, mResources.getDrawable(R.drawable.icon_energy));
            mEnergyBar.setPercent(PlanetActivity.mEnergy / 100.0F);
            mEnergyBar.onDraw(canvas);
            
            // environment bar
            mEnvironmentBar = new Bar(false, 20, false, mResources.getDrawable(R.drawable.icon_environment));
            mEnvironmentBar.setPercent(PlanetActivity.mEnvironment / 100.0F);
            mEnvironmentBar.onDraw(canvas);
        }
        
        private void drawBackground(Canvas canvas) {
            mBackgroundOffset -= 0.1;
            if (mBackgroundOffset < 0)
              this.mBackgroundOffset += mBackgroundImage.getWidth();
            canvas.save();
            canvas.translate(mBackgroundOffset, 0);
            canvas.drawBitmap(mBackgroundImage, 0, 0, null);
            canvas.restore();
            canvas.save();
            canvas.translate(mBackgroundOffset - mBackgroundImage.getWidth(), 0);
            canvas.drawBitmap(mBackgroundImage, 0, 0, null);
            canvas.restore();
        }
        
        private void drawMoonSun(Canvas canvas) {
            Calendar calendar = Calendar.getInstance();
            double hour = calendar.get(Calendar.HOUR_OF_DAY);
            double minutes = calendar.get(Calendar.MINUTE);
            
            hour = 8;
            hour = mDaytime;            
            minutes = 0;
            
            if(hour <= 6) {
                hour += 6;
                bDaytime = false;
            }
            else if(hour >= 18) {
                hour -= 18;
                bDaytime = false;
            }
            else if(hour > 6 && hour < 18) {
                hour -= 6;
                bDaytime = true;
            }
            minutes += hour * 60;
            
            double minutesP = 2;
            minutesP = minutes / 720 * 100;
            
            double left = 0;
            double top = 0;
            double right = 0;
            double bottom = 0;
            
            if(minutesP <= 100) {
                left = ( mCanvasWidth / 2.0 ) / 100.0 * (minutesP*2);
                //top = ( mCanvasHeight / 2.0 ) / 100.0 * (100.0 - minutesP );
                //top = -1*Math.pow((0.025 * left), 2) + (0) + (mCanvasHeight / 4);
                //top = 0.005 * Math.pow(left, 2) + 2 * left + (mCanvasHeight / 4);
                top = 0.003 * Math.pow(left, 2) + (-1.3481 * left) + (mCanvasHeight / 2.5);
            }
            
            //Log.v(Planet.LOG_TAG, "W: " + mCanvasWidth + " H: " + mCanvasHeight + " D: " + mDaytime + " ..... " + (int)left + " | " + (int)top + " | " + (int)right + " | " + (int)bottom);
            
            
            if(!bDaytime) {
                right = left + mMoonWidth / 2;
                bottom = top + mMoonHeight / 2;
                left = left - mMoonWidth / 2;
                top = top - mMoonHeight / 2;
                mMoonImage.setBounds((int)left, (int)top, (int)right, (int)bottom);
                mMoonImage.draw(canvas);
                canvas.restore();
                
                canvas.save();
                canvas.drawBitmap(mDarknessImage, 0, 0 , null);
                canvas.restore();
            } else {
                right = left + mSunWidth / 2;
                bottom = top + mSunHeight / 2;
                left = left - mSunWidth / 2;
                top = top - mSunHeight / 2;
                mSunImage.setBounds((int)left, (int)top, (int)right, (int)bottom);
                mSunImage.draw(canvas);
                canvas.restore();
            }
            
            mDaytime += 0.01;
            if(mDaytime >= 24) mDaytime = 0;
            
        }
        
        public class Bar
        {
            public static final int MODE_CRITICAL = 2;
            public static final int MODE_NORMAL = 0;
            public static final int MODE_WARNING = 1;
            private Drawable mBackgroundImage;
            private Drawable mDefaultBar;
            private Drawable mCriticalBar;
            private int mFillWidth = 0;
            private Drawable mIconImage;
            private boolean mIsBallance = false;
            private Drawable mMidPoint;
            private int mMode;
            private int mOffsetBottom;
            private int mOffsetLeft;
            private Drawable mWarningBar;
            private int mWidth = 0;

            public Bar(boolean paramInt, int paramDrawable, boolean left, Drawable paramIcon)
            {
                this.mIsBallance = paramInt;
                this.mIconImage = paramIcon;
                this.mBackgroundImage = mResources.getDrawable(R.drawable.bar_back);
                this.mDefaultBar = mResources.getDrawable(R.drawable.bar_green);
                this.mWarningBar = mResources.getDrawable(R.drawable.bar_yellow);
                this.mCriticalBar = mResources.getDrawable(R.drawable.bar_red);
                this.mMidPoint = mResources.getDrawable(R.drawable.mid_mark);
                updateOffsets(paramDrawable, left);
            }

            public void onDraw(Canvas paramCanvas)
            {
                int i = mCanvasHeight;
                int j = this.mOffsetLeft;
                this.mIconImage.setBounds(this.mOffsetLeft - this.mIconImage.getIntrinsicWidth() / 2 - 10, i - this.mOffsetBottom - this.mIconImage.getIntrinsicHeight() / 2, this.mOffsetLeft - 10, i - this.mOffsetBottom);
                this.mIconImage.draw(paramCanvas);
                this.mBackgroundImage.setBounds(j, i - this.mOffsetBottom - this.mBackgroundImage.getIntrinsicHeight(), j + this.mWidth, i - this.mOffsetBottom);
                this.mBackgroundImage.draw(paramCanvas);
                Drawable localDrawable;
                switch (this.mMode)
                {
                    default:
                        localDrawable = this.mDefaultBar;
                        break;
                    case 0:
                        localDrawable = this.mDefaultBar;
                        break;
                    case 1:
                        localDrawable = this.mWarningBar;
                        break;
                    case 2:
                        localDrawable = this.mCriticalBar;
                        break;
                }
                localDrawable.setBounds(j, i - this.mOffsetBottom - localDrawable.getIntrinsicHeight(), j + this.mFillWidth, i - this.mOffsetBottom);
                localDrawable.draw(paramCanvas);
                if (this.mIsBallance)
                {
                    int k = j + (this.mWidth / 2 - this.mMidPoint.getIntrinsicWidth() / 2);
                    this.mMidPoint.setBounds(k, i - this.mOffsetBottom - this.mMidPoint.getIntrinsicHeight(), k + this.mMidPoint.getIntrinsicWidth(), i - this.mOffsetBottom);
                    this.mMidPoint.draw(paramCanvas);
                }
            }

            public void setPercent(float paramFloat)
            {
                if(paramFloat > 1.0F)
                    this.mFillWidth = this.mWidth;
                else
                    this.mFillWidth = (int)(paramFloat * this.mWidth);
                
                // set mode
                if(paramFloat <= 0.3F)
                    this.mMode = 2;
                else if(paramFloat >= 0.7F)
                    this.mMode = 0;
                else
                    this.mMode = 1;
                    
            }
            
            public void updateOffsets(int paramInt, boolean left)
            {
                this.mWidth = (int)((0.8F * mCanvasWidth) / 2);
                this.mOffsetBottom = paramInt;
                if(!left) 
                    this.mOffsetLeft = (mCanvasWidth - this.mWidth);
                else 
                    this.mOffsetLeft = (int)(mCanvasWidth - (mCanvasWidth * 0.9F));
            }
        }
    }
    
    /** Handle to the application context, used to e.g. fetch Drawables. */
    //private Context mContext;

    /** Pointer to the text view to display "Paused.." etc. */
    //private TextView mStatusText;
    
    private PlanetThread thread;
    
    public PlanetView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new PlanetThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                //mStatusText.setVisibility(m.getData().getInt("viz"));
                //mStatusText.setText(m.getData().getString("text"));
            }
        });

        setFocusable(true);
    }

    public PlanetThread getThread() {
        return thread;
    }
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) thread.pause();
    }

    /**
     * Installs a pointer to the text view used for messages.
     */
    public void setTextView(TextView textView) {
        //mStatusText = textView;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        thread.setSurfaceSize(width, height);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if(thread.getState() == Thread.State.NEW) {
            thread.setRunning(true);
            thread.start();
        }
        else {
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}
