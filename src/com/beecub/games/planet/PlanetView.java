package com.beecub.games.planet;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.beecub.games.planet.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PlanetView extends SurfaceView implements SurfaceHolder.Callback {    
    class PlanetThread extends Thread {
        
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
        
        // active power
        private Drawable mPower;
        private Drawable mPowerBorder;
        
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
        
        // space platform
        private Drawable mSpacePlatformImage;
        private int mSpacePlatformHeight;
        private int mSpacePlatformWidth;
        
        // planet deluge
        private Drawable mPlanetDeluge1;
        private Drawable mPlanetDeluge2;
        private Drawable mPlanetDeluge3;
        private Drawable mPlanetDeluge4;
        // planet fog
        private Drawable mPlanetFog;
        // planet storm
        private Drawable mPlanetStorm;
        private Drawable mPlanetStorm_l1;
        private Drawable mPlanetStorm_l2;
        private Drawable mPlanetStorm_l3;
        private Drawable mPlanetStorm_l4;
        private Drawable mPlanetStorm_l5;
        private Drawable mPlanetStorm_l6;
        private Drawable mPlanetStorm_l7;
        private Drawable mPlanetStorm_l8;
        
        private double mDaytime;
        private boolean bDaytime = false;
        
        private float mRotation;
        private float mRotation2;
        
        private boolean mRun = false;
        private boolean mPause = false;
        
        private Resources mResources;
        
        // bars
        private Bar mHappinessBar;
        private Bar mEnvironmentBar;
        private Bar mTemperatureBar;
        
        
        public PlanetThread(SurfaceHolder surfaceHolder, Context context) {
            
            mSurfaceHolder = surfaceHolder;
            //mContext = context;

            mResources = context.getResources();

            // planet
            mPlanetImage = mResources.getDrawable(R.drawable.planet);
            mPlanetBorder = mResources.getDrawable(R.drawable.planet_border);
            
            // moon
            mMoonImage = mResources.getDrawable(R.drawable.moon);
            
            // sun
            mSunImage = mResources.getDrawable(R.drawable.sun);
            
            // planet deluge
            mPlanetDeluge1 = mResources.getDrawable(R.drawable.planet_deluge_1);
            mPlanetDeluge2 = mResources.getDrawable(R.drawable.planet_deluge_2);
            mPlanetDeluge3 = mResources.getDrawable(R.drawable.planet_deluge_3);
            mPlanetDeluge4 = mResources.getDrawable(R.drawable.planet_deluge_4);
            // planet fog
            mPlanetFog = mResources.getDrawable(R.drawable.fog);
            // planet storm
            mPlanetStorm = mResources.getDrawable(R.drawable.storm);
            mPlanetStorm_l1 = mResources.getDrawable(R.drawable.storm_l1);
            mPlanetStorm_l2 = mResources.getDrawable(R.drawable.storm_l2);
            mPlanetStorm_l3 = mResources.getDrawable(R.drawable.storm_l3);
            mPlanetStorm_l4 = mResources.getDrawable(R.drawable.storm_l4);
            mPlanetStorm_l5 = mResources.getDrawable(R.drawable.storm_l5);
            mPlanetStorm_l6 = mResources.getDrawable(R.drawable.storm_l6);
            mPlanetStorm_l7 = mResources.getDrawable(R.drawable.storm_l7);
            mPlanetStorm_l8 = mResources.getDrawable(R.drawable.storm_l8);
            
            // space platform
            mSpacePlatformImage = mResources.getDrawable(R.drawable.space_platform);

            
            mBackgroundImage = BitmapFactory.decodeResource(mResources,
                    R.drawable.background_overview); 
            mDarknessImage  = BitmapFactory.decodeResource(mResources,
                    R.drawable.overlay_dark);
            
        }
        
        public void doStart() {
            synchronized (mSurfaceHolder) {
                mDaytime = 0;                
                setRunning(true);
                mPause = false;
            }
        }
        
        public void pause() {
            synchronized (mSurfaceHolder) {
                Log.v("beecub", "Pause");
                if (mRun == true) setRunning(false);
                mPause = true;
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
                setRunning(false);

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
        
        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if(!mPause) {
                            doDraw(c);
                        }
                    }
                } finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
        
        public void setRunning(boolean b) {
            mRun = true;
            //mRun = b;
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
                
                mSpacePlatformWidth = mPlanetWidth / 5;
                mSpacePlatformHeight = mPlanetHeight / 5;
                
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
            
            mRotation += 0.03;
            if(mRotation >= 360) mRotation = 0;
            mRotation2 += 0.05;
            if(mRotation2 >= 360) mRotation2 = 0;
            
            if(PlanetActivity.mEnvironment < 30) 
                mPlanetSurface = mResources.getDrawable(R.drawable.planet_surface_low);
            else if(PlanetActivity.mEnvironment >= 70)
                mPlanetSurface = mResources.getDrawable(R.drawable.planet_surface_full);
            else
                mPlanetSurface = mResources.getDrawable(R.drawable.planet_surface_half);
            
            if(PlanetActivity.mPower == 7) {
                mPlanetSurface = mResources.getDrawable(R.drawable.planet_surface_low);
            }
            
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
            
            mPlanetImage.draw(canvas);
            mPlanetSurface.draw(canvas);
            if(bDaytime)
                drawPopulation(canvas);
            
            canvas.restore();
            
            drawPower(canvas);
            
            drawMoonSun(canvas);
            if(!bDaytime)
                drawPopulation(canvas);
            
            drawObjects(canvas);
            
            mPlanetBorder.draw(canvas);
            
            drawBars(canvas);
            
            drawPowerNotification(canvas);
            
            drawBasis(canvas);
            
            PlanetActivity.progress(false);
        }
        
        private void drawBasis(Canvas canvas) {
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Style.FILL);
            paint.setTextSize(30);
            paint.setTypeface(PlanetActivity.mTypeface);
            paint.setAntiAlias(true);
            canvas.drawText(PlanetActivity.mName, 10, 30, paint);
            paint.setTextSize(20);
            canvas.drawText(mResources.getString(R.string.elements) + ": " + (long)(PlanetActivity.mElements) + "/100", 10, 55, paint);
            canvas.drawText(mResources.getString(R.string.population) + ": " + (long)(PlanetActivity.mPopulation), 10, 80, paint);
        }
        
        private void drawPowerNotification(Canvas canvas) {
            String number = String.valueOf(PlanetActivity.mPower);
            String name = "Nothing";
            boolean found = false;
            
            if(number.length() < 2) 
                number = "0" + number;
            if(number.length() < 3)
                number = "0" + number;
            
            String[] powers = mResources.getStringArray(R.array.powers);
            int i = 0;
            while(i < powers.length && !found) {
                if(powers[i].equalsIgnoreCase(number)) {
                    name = powers[i+1];
                    int resID = getResources().getIdentifier("power_" + powers[i+4], "drawable", PlanetActivity.mPackageName);
                    mPower = mResources.getDrawable(resID);
                    found = true;
                }
                i+=10;
            }
            
            if(!found) {
                mPower = mResources.getDrawable(R.drawable.power_000);
                name = mResources.getString(R.string.nothing);
            }
            mPowerBorder = mResources.getDrawable(R.drawable.powers_border);
            mPower.setBounds(10, mCanvasHeight - 66 - 10 - mResources.getDrawable(R.drawable.bar_green).getIntrinsicHeight(), 10 + 66 + mResources.getDrawable(R.drawable.bar_green).getIntrinsicHeight(), mCanvasHeight - 10);
            mPowerBorder.setBounds(10, mCanvasHeight - 66 - 10 - mResources.getDrawable(R.drawable.bar_green).getIntrinsicHeight(), 10 + 66 + mResources.getDrawable(R.drawable.bar_green).getIntrinsicHeight(), mCanvasHeight - 10);
            mPower.draw(canvas);
            mPowerBorder.draw(canvas);
            
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Style.FILL);
            paint.setTextSize(20);
            paint.setTypeface(PlanetActivity.mTypeface);
            paint.setAntiAlias(true);
            canvas.drawText(mResources.getString(R.string.active), 10 + 66 + mResources.getDrawable(R.drawable.bar_green).getIntrinsicHeight() + 10F, (mCanvasHeight - 43 - 10 - mResources.getDrawable(R.drawable.bar_green).getIntrinsicHeight()), paint);
            canvas.drawText(name, 10 + 66 + mResources.getDrawable(R.drawable.bar_green).getIntrinsicHeight() + 10F, (mCanvasHeight - 20 - 10 - mResources.getDrawable(R.drawable.bar_green).getIntrinsicHeight()), paint);
        }
        
        private void drawPower(Canvas canvas) {
            float time = new Date().getTime();
            canvas.save();
            
            if(PlanetActivity.mPower == 8) {
                canvas.rotate(mRotation, mCanvasWidth / 2, mCanvasHeight / 2);
                time = time - PlanetActivity.mPowerStartTime;
                time = time / (1000 * 60);
                
                mPlanetDeluge1.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, mCanvasHeight / 2 - mPlanetHeight / 2, mCanvasWidth / 2 + mPlanetWidth / 2, mCanvasHeight / 2 + mPlanetHeight / 2);
                mPlanetDeluge1.draw(canvas);
                if(time > 60) {
                    mPlanetDeluge2.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, mCanvasHeight / 2 - mPlanetHeight / 2, mCanvasWidth / 2 + mPlanetWidth / 2, mCanvasHeight / 2 + mPlanetHeight / 2);
                    mPlanetDeluge2.draw(canvas);
                }
                if(time > 120) {
                    mPlanetDeluge3.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, mCanvasHeight / 2 - mPlanetHeight / 2, mCanvasWidth / 2 + mPlanetWidth / 2, mCanvasHeight / 2 + mPlanetHeight / 2);
                    mPlanetDeluge3.draw(canvas);
                }
                if(time > 180) {
                    mPlanetDeluge4.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, mCanvasHeight / 2 - mPlanetHeight / 2, mCanvasWidth / 2 + mPlanetWidth / 2, mCanvasHeight / 2 + mPlanetHeight / 2);
                    mPlanetDeluge4.draw(canvas);
                }
            } else if(PlanetActivity.mPower == 3) {
                canvas.rotate(mRotation2, mCanvasWidth / 2, mCanvasHeight / 2);
                mPlanetFog.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, mCanvasHeight / 2 - mPlanetHeight / 2, mCanvasWidth / 2 + mPlanetWidth / 2, mCanvasHeight / 2 + mPlanetHeight / 2);
                mPlanetFog.draw(canvas);
            } else if(PlanetActivity.mPower == 2 || PlanetActivity.mPower == 4) {
                canvas.rotate(mRotation2, mCanvasWidth / 2, mCanvasHeight / 2);
                mPlanetStorm.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, mCanvasHeight / 2 - mPlanetHeight / 2, mCanvasWidth / 2 + mPlanetWidth / 2, mCanvasHeight / 2 + mPlanetHeight / 2);
                mPlanetStorm.draw(canvas);
            } else if(PlanetActivity.mPower == 5 || PlanetActivity.mPower == 6) {
                canvas.rotate(mRotation2, mCanvasWidth / 2, mCanvasHeight / 2);
                mPlanetStorm.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, mCanvasHeight / 2 - mPlanetHeight / 2, mCanvasWidth / 2 + mPlanetWidth / 2, mCanvasHeight / 2 + mPlanetHeight / 2);
                mPlanetStorm.draw(canvas);
                
                Random random = new Random();
                int r = random.nextInt(100) + 1;
                if(r == 1) {
                    mPlanetStorm_l1.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, mCanvasHeight / 2 - mPlanetHeight / 2, mCanvasWidth / 2 + mPlanetWidth / 2, mCanvasHeight / 2 + mPlanetHeight / 2);
                    mPlanetStorm_l1.draw(canvas);
                } else if(r == 2) {
                    mPlanetStorm_l2.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, mCanvasHeight / 2 - mPlanetHeight / 2, mCanvasWidth / 2 + mPlanetWidth / 2, mCanvasHeight / 2 + mPlanetHeight / 2);
                    mPlanetStorm_l2.draw(canvas);
                } else if(r == 3) {
                    mPlanetStorm_l3.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, mCanvasHeight / 2 - mPlanetHeight / 2, mCanvasWidth / 2 + mPlanetWidth / 2, mCanvasHeight / 2 + mPlanetHeight / 2);
                    mPlanetStorm_l3.draw(canvas);
                } else if(r == 4) {
                    mPlanetStorm_l4.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, mCanvasHeight / 2 - mPlanetHeight / 2, mCanvasWidth / 2 + mPlanetWidth / 2, mCanvasHeight / 2 + mPlanetHeight / 2);
                    mPlanetStorm_l4.draw(canvas);
                } else if(r == 5) {
                    mPlanetStorm_l5.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, mCanvasHeight / 2 - mPlanetHeight / 2, mCanvasWidth / 2 + mPlanetWidth / 2, mCanvasHeight / 2 + mPlanetHeight / 2);
                    mPlanetStorm_l5.draw(canvas);
                } else if(r == 6) {
                    mPlanetStorm_l6.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, mCanvasHeight / 2 - mPlanetHeight / 2, mCanvasWidth / 2 + mPlanetWidth / 2, mCanvasHeight / 2 + mPlanetHeight / 2);
                    mPlanetStorm_l6.draw(canvas);
                } else if(r == 7) {
                    mPlanetStorm_l7.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, mCanvasHeight / 2 - mPlanetHeight / 2, mCanvasWidth / 2 + mPlanetWidth / 2, mCanvasHeight / 2 + mPlanetHeight / 2);
                    mPlanetStorm_l7.draw(canvas);
                } else if(r == 8) {
                    mPlanetStorm_l8.setBounds(mCanvasWidth / 2 - mPlanetWidth / 2, mCanvasHeight / 2 - mPlanetHeight / 2, mCanvasWidth / 2 + mPlanetWidth / 2, mCanvasHeight / 2 + mPlanetHeight / 2);
                    mPlanetStorm_l8.draw(canvas);
                }
            }
            
            canvas.restore();
        }
        
        private void drawObjects(Canvas canvas) {
            double left = 0;
            double right = 0;
            double top = 0;
            double bottom = 0;
            
            left = (mCanvasWidth / 10) * 2.5 - mSpacePlatformWidth / 2;
            right = left + mSpacePlatformWidth;
            
            top = (mCanvasHeight / 10) * 7.5 - mSpacePlatformHeight / 2;
            bottom = top + mSpacePlatformHeight;
            
            mSpacePlatformImage.setBounds((int)left, (int)top, (int)right, (int)bottom);
            mSpacePlatformImage.draw(canvas);
        }
        
        private void drawPopulation(Canvas canvas) {
            canvas.save();
            if(bDaytime) {
                mPlanetPopulation = mResources.getDrawable(R.drawable.planet_popuatlion_1);
                if(PlanetActivity.mPopulation < 1000)
                    mPlanetPopulation = mResources.getDrawable(R.drawable.planet_popuatlion_1);
                else if(PlanetActivity.mPopulation < 10000)
                    mPlanetPopulation = mResources.getDrawable(R.drawable.planet_popuatlion_2);
            } else {
                mPlanetPopulation = mResources.getDrawable(R.drawable.planet_popuatlion_1_night);
                if(PlanetActivity.mPopulation < 1000)
                    mPlanetPopulation = mResources.getDrawable(R.drawable.planet_popuatlion_1_night);
                else if(PlanetActivity.mPopulation < 10000)
                    mPlanetPopulation = mResources.getDrawable(R.drawable.planet_popuatlion_2_night);
                canvas.rotate(mRotation, mCanvasWidth / 2, mCanvasHeight / 2);
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
                mHappinessBar = new Bar(0, false, 66, false, mResources.getDrawable(R.drawable.icon_face_angry));
            else if(PlanetActivity.mMood >= 70)
                mHappinessBar = new Bar(0, false, 66, false, mResources.getDrawable(R.drawable.icon_face_grin));
            else
                mHappinessBar = new Bar(0, false, 66, false, mResources.getDrawable(R.drawable.icon_face_plain));
            mHappinessBar.setPercent(PlanetActivity.mMood / 100.0F);
            mHappinessBar.onDraw(canvas);
            
            // temperature bar
            mTemperatureBar = new Bar(1, true, 43, false, mResources.getDrawable(R.drawable.icon_temperature));
            mTemperatureBar.setPercent(PlanetActivity.mTemperature / 100.0F);
            mTemperatureBar.onDraw(canvas);
            
            // environment bar
            mEnvironmentBar = new Bar(0, true, 20, false, mResources.getDrawable(R.drawable.icon_environment));
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
                
                canvas.drawBitmap(mDarknessImage, 0, 0 , null);
            } else {
                canvas.restore();
                right = left + mSunWidth / 2;
                bottom = top + mSunHeight / 2;
                left = left - mSunWidth / 2;
                top = top - mSunHeight / 2;
                mSunImage.setBounds((int)left, (int)top, (int)right, (int)bottom);
                mSunImage.draw(canvas);
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
            private Drawable mYellowBar;
            private Drawable mRedBar;
            private Drawable mGreenBar;
            private Drawable mBlueBar;
            private Drawable mWhiteBar;
            private int mFillWidth = 0;
            private Drawable mIconImage;
            private boolean mIsBallance = false;
            private Drawable mMidPoint;
            private int mType;
            private int mMode;
            private int mOffsetBottom;
            private int mOffsetLeft;
            private int mWidth = 0;

            public Bar(int type, boolean paramInt, int paramDrawable, boolean left, Drawable paramIcon)
            {
                this.mType = type;
                this.mIsBallance = paramInt;
                this.mIconImage = paramIcon;
                this.mBackgroundImage = mResources.getDrawable(R.drawable.bar_back);
                this.mGreenBar = mResources.getDrawable(R.drawable.bar_green);
                this.mYellowBar = mResources.getDrawable(R.drawable.bar_yellow);
                this.mRedBar = mResources.getDrawable(R.drawable.bar_red);
                this.mBlueBar = mResources.getDrawable(R.drawable.bar_blue);
                this.mWhiteBar = mResources.getDrawable(R.drawable.bar_white);
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
                switch (this.mType) {
                default:
                    switch (this.mMode)
                    {
                        default:
                            localDrawable = this.mYellowBar;
                            break;
                        case 0:
                            localDrawable = this.mGreenBar;
                            break;
                        case 1:
                            localDrawable = this.mYellowBar;
                            break;
                        case 2:
                            localDrawable = this.mRedBar;
                            break;
                    }
                    break;
                case 0:
                    switch (this.mMode)
                    {
                        default:
                            localDrawable = this.mYellowBar;
                            break;
                        case 0:
                            localDrawable = this.mGreenBar;
                            break;
                        case 1:
                            localDrawable = this.mYellowBar;
                            break;
                        case 2:
                            localDrawable = this.mRedBar;
                            break;
                    }
                    break;
                case 1:
                    switch (this.mMode)
                    {
                        default:
                            localDrawable = this.mWhiteBar;
                            break;
                        case 0:
                            localDrawable = this.mRedBar;
                            break;
                        case 1:
                            localDrawable = this.mWhiteBar;
                            break;
                        case 2:
                            localDrawable = this.mBlueBar;
                            break;
                    }
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
    
    private PlanetThread thread;
    
    public PlanetView(Context context) {
        super(context);

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        thread = new PlanetThread(holder, context);

        setFocusable(true);
    }

    public PlanetThread getThread() {
        return thread;
    }
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
//        if (!hasWindowFocus) thread.pause();
//        else thread.doStart();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        thread.setSurfaceSize(width, height);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        //thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}
