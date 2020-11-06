package com.android.pfg7001.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.android.pfg7001.R;

public class StreamFragment extends Fragment {

    private IStreamFragment myInterface;
    private boolean playSound1;
    private boolean playSound2;
    private boolean playSound3;
    private boolean playSound4;
    private static final String KEY_VIEW = "setView";
    private boolean setView;

    public interface IStreamFragment {
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

    public static StreamFragment getInstance(boolean setView){
        StreamFragment fragment = new StreamFragment();

        Bundle argumentos = new Bundle();
        argumentos.putBoolean(KEY_VIEW, setView);

        fragment.setArguments(argumentos);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle argumentos = getArguments();
        if (argumentos != null)
            this.setView = argumentos.getBoolean(KEY_VIEW);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stream, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        playSound1 = true;
        playSound2 = true;
        playSound3 = true;
        playSound4 = true;
        SwitchCompat switchCompat1 = view.findViewById(R.id.switchAudio1);
        SwitchCompat switchCompat2 = view.findViewById(R.id.switchAudio2);
        SwitchCompat switchCompat3 = view.findViewById(R.id.switchAudio3);
        SwitchCompat switchCompat4 = view.findViewById(R.id.switchAudio4);
        AppCompatSeekBar seekBar1 = view.findViewById(R.id.seekBar1);
        AppCompatSeekBar seekBar2 = view.findViewById(R.id.seekBar2);
        AppCompatSeekBar seekBar3 = view.findViewById(R.id.seekBar3);
        AppCompatSeekBar seekBar4 = view.findViewById(R.id.seekBar4);
        View viewIsStreaming = view.findViewById(R.id.viewIsStreaming);
        if (setView){
            viewIsStreaming.setVisibility(View.GONE);
        }

        switchCompat1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                seekBar1.setEnabled(true);
                myInterface.gain1(seekBar1.getProgress());
            } else {
                seekBar1.setEnabled(false);
                myInterface.gain1(0);
            }
        });
        switchCompat2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                seekBar2.setEnabled(true);
                myInterface.gain2(seekBar2.getProgress());
            } else {
                seekBar2.setEnabled(false);
                myInterface.gain2(0);
            }
        });
        switchCompat3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                seekBar3.setEnabled(true);
                myInterface.gain3(seekBar3.getProgress());
            } else {
                seekBar3.setEnabled(false);
                myInterface.gain3(0);
            }
        });
        switchCompat4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                seekBar4.setEnabled(true);
                myInterface.gain4(seekBar4.getProgress());
            } else {
                seekBar4.setEnabled(false);
                myInterface.gain4(0);
            }
        });

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
