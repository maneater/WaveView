/*
 *  Copyright (C) 2015, gelitenight(gelitenight@gmail.com).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.maneater.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

public class WaveView extends View {
    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private void init() {
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void demo() {

        List<Wave> waveList = new ArrayList<Wave>();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        waveList.add(new Wave().phase(0).speed(200).amplitude((int) (height * 0.05f)).waterLevel((int) (height * 0.1f)).waveColor(0x3fC9FC94).waveLength((int) (width * 2.8f)));
        waveList.add(new Wave().phase((float) Math.PI / 5).speed(450).amplitude((int) (height * 0.05f)).waterLevel((int) (height * 0.07f)).waveColor(0x3fA1FD7C).waveLength((int) (width * 3.5f)));
        waveList.add(new Wave().phase((float) Math.PI / 3).speed(600).amplitude((int) (height * 0.04f)).phase((float) Math.PI).waterLevel((int) (height * 0.14f)).waveColor(0x3fCBFFAA).waveLength(width * 4));

        waveList.add(new Wave().align(Align.TOP).phase((float) Math.PI / 6).speed(200).amplitude((int) (height * 0.04f)).phase((float) Math.PI).waterLevel((int) (height * 0.14f)).waveColor(0x3fCBFFAA).waveLength(width * 4));
        waveList.add(new Wave().align(Align.TOP).phase((float) Math.PI / 2).speed(1200).amplitude((int) (height * 0.05f)).waterLevel((int) (height * 0.1f)).waveColor(0x3fC9FC94).waveLength((int) (width * 2.8f)));
        waveList.add(new Wave().align(Align.TOP).phase((float) Math.PI / 3).speed(450).amplitude((int) (height * 0.16f)).waterLevel((int) (height * 0.07f)).waveColor(0x3fA1FD7C).waveLength((int) (width * 3.5f)));

        addWaves(waveList);
    }

    private List<Wave> waveList = new ArrayList<Wave>();

    private long mStartDrawTime = -1;

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        if (mStartDrawTime == -1) {
            mStartDrawTime = AnimationUtils.currentAnimationTimeMillis();
        }
        long pastTime = AnimationUtils.currentAnimationTimeMillis() - mStartDrawTime;

        //  y=Asin(ωx+φ)+h
        for (Wave wave : waveList) {
            mPaint.setColor(wave.mWaveColor);

            canvas.save();
            float transX = (wave.mSpeed * (pastTime * 1.0f / 1000)) % wave.mWaveLength;
            if (wave.mAlign == Align.BOTTOM) {
                canvas.scale(1, -1, getWidth() / 2, getHeight() / 2);
            }
            canvas.translate(transX, 0);
            canvas.drawPath(wave.mPath, mPaint);

            canvas.translate(-wave.mWaveLength * wave.mRepeatNum, 0);
            canvas.drawPath(wave.mPath, mPaint);
            canvas.restore();
        }
        postInvalidateDelayed(30);
    }


    public void addWaves(List<Wave> waveList) {
        this.waveList.addAll(waveList);
        buildWavesPath();
        invalidate();
    }

    public static class Wave {
        int mWaveColor = Color.GREEN;
        int mRepeatNum = 1;
        int mAmplitude = -1;
        int mWaterLevel = -1;
        int mWaveLength = -1;
        int mSpeed = 0;
        float mPhase = 0;
        Align mAlign = Align.BOTTOM;
        Path mPath = null;

        public Wave() {
        }

        public Wave waveColor(int mWaveColor) {
            this.mWaveColor = mWaveColor;
            return this;
        }

        public Wave amplitude(int mAmplitude) {
            this.mAmplitude = mAmplitude;
            return this;
        }

        public Wave waterLevel(int mWaterLevel) {
            this.mWaterLevel = mWaterLevel;
            return this;
        }

        public Wave waveLength(int mWaveLength) {
            this.mWaveLength = mWaveLength;
            return this;
        }

        public Wave speed(int speed) {
            this.mSpeed = speed;
            return this;
        }

        public Wave phase(float mPhase) {
            this.mPhase = mPhase;
            return this;
        }

        public Wave align(Align mAlign) {
            this.mAlign = mAlign;
            return this;
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        buildWavesPath();
    }

    //  y=Asin(ωx+φ)+h
    //最小正周期T=2π/|ω|  故 ω = 2π/T
    private void buildWavesPath() {
        int width = getWidth();
        int height = getHeight();

        for (Wave wave : waveList) {

            if (wave.mAmplitude == -1) {
                wave.mAmplitude = getWidth() / 8;
            }
            if (wave.mWaterLevel == -1) {
                wave.mWaterLevel = wave.mAmplitude;//2 * wave.mAmplitude;
            }

            if (wave.mWaveLength == -1) {
                wave.mWaveLength = width;
            }


            if (wave.mPhase == -1) {
                wave.mPhase = 0;//初始相位
            }

            float ω = (float) (2 * Math.PI / wave.mWaveLength);

            int repeatNum = (width / wave.mWaveLength) + (width % wave.mWaveLength > 0 ? 1 : 0);
            Path mPath = new Path();
            mPath.moveTo(0, 0);

            for (int i = 0; i < repeatNum; i++) {
                for (int x = 0; x < wave.mWaveLength; x++) {
                    int xOffset = x + i * wave.mWaveLength;
                    float y = (float) (wave.mWaterLevel + wave.mAmplitude * Math.sin(ω * xOffset + wave.mPhase));
                    mPath.lineTo(xOffset, y);
                    if (i == repeatNum - 1 && x == wave.mWaveLength - 1) {
                        mPath.lineTo(xOffset, 0);
                        mPath.close();
                    }
                }
            }
            wave.mPath = mPath;
        }
    }


    public enum Align {
        TOP, BOTTOM;
    }
}
