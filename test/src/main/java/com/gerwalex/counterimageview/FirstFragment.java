package com.gerwalex.counterimageview;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gerwalex.counterimageview.databinding.FragmentFirstBinding;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
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
        DecoView decoView = binding.counterimage.getDecoView();
        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2")).setRange(0, 50, 0).build();
        int backIndex = decoView.addSeries(seriesItem);
        seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF8800")).setRange(0, 50, 0).build();
        int series1Index = decoView.addSeries(seriesItem);
        decoView.addEvent(new DecoEvent.Builder(50).setIndex(backIndex).build());
        decoView.addEvent(new DecoEvent.Builder(16.3f).setIndex(series1Index).setDelay(5000).build());
        decoView.addEvent(new DecoEvent.Builder(30f).setIndex(series1Index).setDelay(10000).build());
    }
}