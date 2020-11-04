package com.android.pfg7001.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.android.pfg7001.R;

public class StreamFragment extends Fragment {

    private IStreamFragment myInterface;
    private boolean playSound1 = true;
    private boolean playSound2 = true;
    private boolean playSound3 = true;
    private boolean playSound4 = true;

    public interface IStreamFragment {
        void disconnect();

        void gain1(int gain);

        void gain2(int gain);

        void gain3(int gain);

        void gain4(int gain);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof IStreamFragment) {
            this.myInterface = (IStreamFragment) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stream, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnDesconectar = view.findViewById(R.id.btnDesconectar);
        SwitchCompat switchCompat1 = view.findViewById(R.id.switchAudio1);
        SwitchCompat switchCompat2 = view.findViewById(R.id.switchAudio2);
        SwitchCompat switchCompat3 = view.findViewById(R.id.switchAudio3);
        SwitchCompat switchCompat4 = view.findViewById(R.id.switchAudio4);
        AppCompatSeekBar seekBar1 = view.findViewById(R.id.seekBar1);
        AppCompatSeekBar seekBar2 = view.findViewById(R.id.seekBar2);
        AppCompatSeekBar seekBar3 = view.findViewById(R.id.seekBar3);
        AppCompatSeekBar seekBar4 = view.findViewById(R.id.seekBar4);

        btnDesconectar.setOnClickListener(v -> myInterface.disconnect());

        switchCompat1.setOnCheckedChangeListener((buttonView, isChecked) -> playSound1 = isChecked);
        switchCompat2.setOnCheckedChangeListener((buttonView, isChecked) -> playSound2 = isChecked);
        switchCompat3.setOnCheckedChangeListener((buttonView, isChecked) -> playSound3 = isChecked);
        switchCompat4.setOnCheckedChangeListener((buttonView, isChecked) -> playSound4 = isChecked);

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (playSound1) {
                    myInterface.gain1(progress);
                } else {
                    myInterface.gain1(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (playSound2) {
                    myInterface.gain2(progress);
                } else {
                    myInterface.gain2(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (playSound3) {
                    myInterface.gain3(progress);
                } else {
                    myInterface.gain3(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (playSound4) {
                    myInterface.gain4(progress);
                } else {
                    myInterface.gain4(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
