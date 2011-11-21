package com.beecub.games.planet;

import java.util.Calendar;

import android.beecub.games.planet.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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
        
        private static final String KEY_DX = "mDX";

        private static final String KEY_DY = "mDY";

        private static final String KEY_HEADING = "mHeading";
        
        private static final String KEY_Planet_HEIGHT = "mPlanetHeight";
        private static final String KEY_Planet_WIDTH = "mPlanetWidth";
        private static final String KEY_Moon_HEIGHT = "mMoonHeight";
        private static final String KEY_Moon_WIDTH = "mMoonWidth";
        private static final String KEY_Sun_HEIGHT = "mSunHeight";
        private static final String KEY_Sun_WIDTH = "mSunWidth";

        private static final String KEY_X = "mX";
        private static final String KEY_Y = "mY";
        
        public static final int PHYS_DOWN_ACCEL_SEC = 35;
        public static final int PHYS_SLEW_SEC = 120; // degrees/second rotate
        
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
        
        private float mRotation;
        
        private int mMode;
        
        private boolean mRun = false;
        
        private Resources mResources;
        
        
        public PlanetThread(SurfaceHolder surfaceHolder, Context context,
                Handler handler) {
            
            mSurfaceHolder = surfaceHolder;
            //mContext = context;

            mResources = context.getResources();

            // planet
            mPlanetImage = context.getResources().getDrawable(R.drawable.planet);
            
            // moon
            mMoonImage = context.getResources().getDrawable(R.drawable.moon);
            
            // sun
            mSunImage = context.getResources().getDrawable(R.drawable.sun);

            
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
            /*
             * This method optionally can cause a text message to be displayed
             * to the user when the mode changes. Since the View that actually
             * renders that text is part of the main View hierarchy and not
             * owned by this thread, we can't touch the state of that View.
             * Instead we use a Message + Handler to relay commands to the main
             * thread, which updates the user-text View.
             */
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
                        if (mMode == STATE_RUNNING) updatePhysics();
                        doDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
        
        /**
         * Used to signal the thread whether it should be running or not.
         * Passing true allows the thread to run; passing false will shut it
         * down if it's already running. Calling start() after this was most
         * recently called with false will result in an immediate shutdown.
         * 
         * @param b true to run, false to shut down
         */
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
                

                // don't forget to resize the background image
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
            
            drawBackground(canvas);            
            canvas.save();
            
            //Log.v(Planet.LOG_TAG, "Time: " + calendar.get(Calendar.HOUR_OF_DAY));
            
            //Log.v(Planet.LOG_TAG, "Canvas Width: " + mCanvasWidth + "| Planet Width: " + mPlanetWidth);
            //Log.v(Planet.LOG_TAG, "Canvas Height: " + mCanvasHeight + "| Planet Height: " + mPlanetHeight);
            
            mPlanetImage.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, 
                    mCanvasHeight / 2 - mPlanetHeight / 2, 
                    mCanvasWidth / 2 + mPlanetWidth / 2, 
                    mCanvasHeight / 2 + mPlanetHeight / 2);
            
            canvas.rotate(mRotation, mCanvasWidth / 2, mCanvasHeight / 2);
            
            mRotation += 0.03;
            if(mRotation >= 360) mRotation = 0;
            
            mPlanetImage.draw(canvas);
            
            canvas.restore();
            
            drawMoonSun(canvas);
            
            drawText(canvas);
        }
        
        private void drawBackground(Canvas canvas) {
            mBackgroundOffset += 0.1;
            if (mBackgroundOffset > mCanvasWidth)
              this.mBackgroundOffset -= mBackgroundImage.getWidth();
            canvas.save();
            canvas.translate(mBackgroundOffset, 0);
            canvas.drawBitmap(mBackgroundImage, 0, 0, null);
            canvas.restore();
            canvas.save();
            canvas.translate(mBackgroundOffset - mBackgroundImage.getWidth(), 0);
            canvas.drawBitmap(mBackgroundImage, 0, 0, null);
            canvas.restore();
        }
        
        private void drawText(Canvas canvas) {
            Paint paint = new Paint();
            int textSize = 27;
            
            canvas.save();
            
            //Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/YanoneKaffeesatz.ttf");
            //paint.setTypeface(typeface);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setTextSize(textSize + 10);
            canvas.drawText(Planet.mName, 5, 5 + textSize, paint);
            paint.setTextSize(textSize);
            canvas.drawText(mResources.getString(R.string.population) + ": " + Planet.mPopulation, 5, 10 + textSize * 2, paint);
            
            canvas.restore();
        }
        
        private void drawMoonSun(Canvas canvas) {
            boolean bDaytime = true;
            Calendar calendar = Calendar.getInstance();
            double hour = calendar.get(Calendar.HOUR_OF_DAY);
            double minutes = calendar.get(Calendar.MINUTE);
            
            hour = 3;
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
        
        /**
         * Figures the Planet state (x, y, fuel, ...) based on the passage of
         * realtime. Does not invalidate(). Called at the start of draw().
         * Detects the end-of-game and sets the UI to the next state.
         */
        private void updatePhysics() {
        }
    }
    
    /** Handle to the application context, used to e.g. fetch Drawables. */
    //private Context mContext;

    /** Pointer to the text view to display "Paused.." etc. */
    //private TextView mStatusText;

    /** The thread that actually draws the animation */
    private PlanetThread thread;
    
    public PlanetView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new PlanetThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                //mStatusText.setVisibility(m.getData().getInt("viz"));
                //mStatusText.setText(m.getData().getString("text"));
            }
        });

        setFocusable(true); // make sure we get key events
    }

    /**
     * Fetches the animation thread corresponding to this LunarView.
     * 
     * @return the animation thread
     */
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

    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        thread.setSurfaceSize(width, height);
    }

    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }

    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
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



//public class PlanetView extends View implements OnTouchListener{
//    Bitmap bitmap;
//    Canvas bitmapCanvas;
//    
//    boolean isInitialized;
//    Paint paint = new Paint();
//
//    public PlanetView(Context context)
//    {
//      super(context);
//      setFocusable(true);
//      setFocusableInTouchMode(true);
//
//      this.setOnTouchListener(this);
//
//      paint.setColor(Color.WHITE);
//      paint.setAntiAlias(true);
//      paint.setStyle(Style.FILL_AND_STROKE);
//     
//      isInitialized = false;
//    }
//
//    private void init()
//    {
//      bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
//     
//      bitmapCanvas = new Canvas();
//      bitmapCanvas.setBitmap(bitmap);
//      bitmapCanvas.drawColor(Color.BLACK);
//     
//      isInitialized = true;
//
//    }
//   
//    @Override
//    public void onDraw(Canvas canvas)
//    {
//      if (!isInitialized)
//        init();
//     
//      canvas.drawBitmap(bitmap, 0, 0, paint);
//    }
//
//    public boolean onTouch(View view, MotionEvent event)
//    {
//      bitmapCanvas.drawCircle(event.getX(), event.getY(), 5, paint);
//
//     
//      invalidate();
//      return true;
//    }
