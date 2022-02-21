package com.gerwalex.counterimageview;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gerwalex.counterimageview.charts.SeriesItem;
import com.gerwalex.counterimageview.databinding.CounterImageViewFragmentBinding;
import com.gerwalex.counterimageview.events.DecoEvent;

public class CounterImageViewFirstFragment extends SampleFragment {

    private int backIndex;
    private @NonNull
    CounterImageViewFragmentBinding binding;
    private int series1Index;

    @Override
    protected void createTracks() {
        DecoView decoView = binding.counterimage.getDecoView();
        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2")).setRange(0, 50, 0).build();
        backIndex = decoView.addSeries(seriesItem);
        seriesItem = new SeriesItem.Builder(Color.parseColor("#FF008800")).setRange(0, 50, 0).build();
        series1Index = decoView.addSeries(seriesItem);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = CounterImageViewFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void setupEvents() {
        DecoView decoView = binding.counterimage.getDecoView();
        decoView.addEvent(new DecoEvent.Builder(50).setIndex(backIndex).build());
        decoView.addEvent(new DecoEvent.Builder(16.3f).setIndex(series1Index).setDelay(5000).build());
        decoView.addEvent(new DecoEvent.Builder(30f).setIndex(series1Index).setDelay(10000).build());
    }
}