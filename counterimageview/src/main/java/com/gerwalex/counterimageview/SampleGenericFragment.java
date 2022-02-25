/*
 * Copyright (C) 2015 Brent Marriott
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gerwalex.counterimageview;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.gerwalex.counterimageview.charts.EdgeDetail;
import com.gerwalex.counterimageview.charts.SeriesItem;
import com.gerwalex.counterimageview.events.DecoEvent;
import com.gerwalex.counterimageview.events.DecoEvent.EventType;

public abstract class SampleGenericFragment extends Fragment {
    private final static boolean[] mClockwise = {true, true, true, false, true};
    private final static float[] mDetailEdge = {0.3f, 0.2f, 0.4f, 0.21f, 0.25f};
    private final static boolean[] mPie = {false, false, false, false, true};
    private final static int[] mRotateAngle = {0, 180, 180, 0, 270};
    private final static boolean[] mRounded = {true, true, true, true, true};
    private final static int[] mTotalAngle = {360, 360, 320, 260, 360};
    private final static float[] mTrackBackWidth = {30f, 60f, 30f, 40f, 20f};
    private final static float[] mTrackWidth = {30f, 60f, 30f, 40f, 20f};
    private int mBackIndex;
    private int mSeries1Index;
    private int mSeries2Index;
    private int mStyleIndex;
    private boolean mUpdateListeners;

    /**
     * Add a listener to update the progress on a TextView
     *
     * @param seriesItem ArcItem to listen for update changes
     * @param view       View to update
     * @param format     String.format to display the progress
     *                   <p/>
     *                   If the string format includes a percentage character we assume that we should set
     *                   a percentage into the string, otherwise the current position is added into the string
     *                   For example if the arc has a min of 0 and a max of 50 and the current position is 20
     *                   Format -> "%.0f%% Complete" -> "40% Complete"
     *                   Format -> "%.1f Km" -> "20.0 Km"
     *                   Format -> "%.0f/40 Levels Complete" -> "20/40 Levels Complete"
     */
    protected void addProgressListener(@NonNull final SeriesItem seriesItem, @NonNull final TextView view,
                                       @NonNull final String format) {
        if (format.length() <= 0) {
            throw new IllegalArgumentException("String formatter can not be empty");
        }
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                if (mUpdateListeners) {
                    if (format.contains("%%")) {
                        // We found a percentage so we insert a percentage
                        float percentFilled = (currentPosition - seriesItem.getMinValue()) /
                                (seriesItem.getMaxValue() - seriesItem.getMinValue());
                        view.setText(String.format(format, percentFilled * 100f));
                    } else {
                        view.setText(String.format(format, currentPosition));
                    }
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {
            }
        });
    }

    protected void addProgressRemainingListener(@NonNull final SeriesItem seriesItem, @NonNull final TextView view,
                                                @NonNull final String format, final float maxValue) {
        if (format.length() <= 0) {
            throw new IllegalArgumentException("String formatter can not be empty");
        }
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {

            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                if (mUpdateListeners) {
                    if (format.contains("%%")) {
                        // We found a percentage so we insert a percentage
                        view.setText(
                                String.format(format, (1.0f - (currentPosition / seriesItem.getMaxValue())) * 100f));
                    } else {
                        view.setText(String.format(format, maxValue - currentPosition));
                    }
                }
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {
            }
        });
    }

    protected abstract boolean createTracks();

    private void createTracks(DecoView decoView) {
        final View view = getView();
        if (decoView != null && view != null && !createTracks()) {
            decoView.deleteAll();
            decoView.configureAngles(mTotalAngle[mStyleIndex], mRotateAngle[mStyleIndex]);
            final float seriesMax = 50f;
            SeriesItem arcBackTrack =
                    new SeriesItem.Builder(Color.argb(255, 228, 228, 228)).setRange(0, seriesMax, seriesMax)
                            .setInitialVisibility(false).setLineWidth(getDimension(mTrackBackWidth[mStyleIndex]))
                            .setChartStyle(mPie[mStyleIndex] ? SeriesItem.ChartStyle.STYLE_PIE :
                                    SeriesItem.ChartStyle.STYLE_DONUT).build();
            mBackIndex = decoView.addSeries(arcBackTrack);
            float inset = 0;
            if (mTrackBackWidth[mStyleIndex] != mTrackWidth[mStyleIndex]) {
                inset = getDimension((mTrackBackWidth[mStyleIndex] - mTrackWidth[mStyleIndex]) / 2);
            }
            SeriesItem seriesItem1 = new SeriesItem.Builder(Color.argb(255, 255, 165, 0)).setRange(0, seriesMax, 0)
                    .setInitialVisibility(false).setLineWidth(getDimension(mTrackWidth[mStyleIndex]))
                    .setInset(new PointF(-inset, -inset)).setSpinClockwise(mClockwise[mStyleIndex])
                    .setCapRounded(mRounded[mStyleIndex]).setChartStyle(
                            mPie[mStyleIndex] ? SeriesItem.ChartStyle.STYLE_PIE : SeriesItem.ChartStyle.STYLE_DONUT)
                    .build();
            if (mDetailEdge[mStyleIndex] > 0) {
                seriesItem1.addEdgeDetail(new EdgeDetail(EdgeDetail.EdgeType.EDGE_OUTER, Color.parseColor("#33000000"),
                        mDetailEdge[mStyleIndex]));
            }
            mSeries1Index = decoView.addSeries(seriesItem1);
            SeriesItem seriesItem2 = new SeriesItem.Builder(Color.argb(255, 255, 51, 51)).setRange(0, seriesMax, 0)
                    .setInitialVisibility(false).setCapRounded(true)
                    .setLineWidth(getDimension(mTrackWidth[mStyleIndex])).setInset(new PointF(inset, inset))
                    .setCapRounded(mRounded[mStyleIndex]).build();
            if (mDetailEdge[mStyleIndex] > 0) {
                seriesItem2.addEdgeDetail(new EdgeDetail(EdgeDetail.EdgeType.EDGE_INNER, Color.parseColor("#20000000"),
                        mDetailEdge[mStyleIndex]));
            }
            mSeries2Index = decoView.addSeries(seriesItem2);
            final TextView textPercent = view.findViewById(R.id.textPercentage);
            textPercent.setVisibility(View.INVISIBLE);
            textPercent.setText("");
            addProgressListener(seriesItem1, textPercent, "%.0f%%");
        }
    }

    protected abstract DecoView getDecoView();

    /**
     * Convert base dip into pixels based on the display metrics of the current device
     *
     * @param base dip value
     * @return pixels from base dip
     */
    protected float getDimension(float base) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, base, getResources().getDisplayMetrics());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sample_generic, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        createTracks(getDecoView());
        //        if (super.getUserVisibleHint()) {
        //            setupEvents(getDecoView());
        //        }
        setupEvents(getDecoView());
    }

    @Override
    public void onStop() {
        super.onStop();
        stopFragment(getDecoView());
    }

    /**
     * Override to create events for demo. For example move, reveal or effect
     */
    abstract protected boolean setupEvents();

    private void setupEvents(DecoView decoView) {
        if (decoView != null && !decoView.isEmpty() && !setupEvents()) {
            mUpdateListeners = true;
            decoView.addEvent(
                    new DecoEvent.Builder(EventType.EVENT_SHOW, true).setDelay(1000).setDuration(2000).build());
            decoView.addEvent(new DecoEvent.Builder(10).setIndex(mSeries2Index).setDelay(3900).build());
            decoView.addEvent(new DecoEvent.Builder(22).setIndex(mSeries2Index).setDelay(7000).build());
            decoView.addEvent(new DecoEvent.Builder(25).setIndex(mSeries1Index).setDelay(3300).build());
            decoView.addEvent(
                    new DecoEvent.Builder(50).setIndex(mSeries1Index).setDuration(1500).setDelay(9000).build());
            decoView.addEvent(new DecoEvent.Builder(EventType.EVENT_HIDE, false).setDelay(11000).setDuration(2000)
                    .setListener(new DecoEvent.ExecuteEventListener() {
                        @Override
                        public void onEventEnd(DecoEvent event) {
                            mStyleIndex++;
                            if (mStyleIndex >= mTrackBackWidth.length) {
                                mStyleIndex = 0;
                                return;
                            }
                            createTracks(decoView);
                            setupEvents(decoView);
                        }

                        @Override
                        public void onEventStart(DecoEvent event) {
                        }
                    }).build());
        }
    }

    /**
     * Override to stop all once it is no longer displayed
     */
    protected void stopFragment(DecoView decoView) {
        if (decoView != null && !decoView.isEmpty()) {
            decoView.executeReset();
            decoView.deleteAll();
        }
    }
}