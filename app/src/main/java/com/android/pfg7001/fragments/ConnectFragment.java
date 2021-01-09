package com.android.pfg7001.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.pfg7001.R;

import java.util.Locale;

public class ConnectFragment extends Fragment {

    private Button btnDisconnect;
    private Button btnConnect;
    private IFindPeersFragment myInterface;
    private TextView mTextviewDeviceName;
    private TextView mTextviewDeviceState;
    private String mDeviceName;
    private String mDeviceState;
    private boolean connected = false;
    private static final String KEY_NAME = "setName";
    private static final String KEY_NETWORK = "setNetwork";
    private static final String KEY_STATE = "setState";

    public static ConnectFragment getInstance(String deviceName, String wifiName) {
        ConnectFragment fragment = new ConnectFragment();

        Bundle argumentos = new Bundle();
        argumentos.putString(KEY_NAME, deviceName);
        argumentos.putString(KEY_NETWORK, wifiName);

        fragment.setArguments(argumentos);
        return fragment;
    }

    public interface IFindPeersFragment {
        void connect();

        void disconnect();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof IFindPeersFragment) {
            this.myInterface = (IFindPeersFragment) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mDeviceName = savedInstanceState.getString(KEY_NAME);
            mDeviceState = savedInstanceState.getString(KEY_NETWORK);
            connected = savedInstanceState.getBoolean(KEY_STATE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle argumentos = getArguments();
        if (argumentos != null) {
            this.mDeviceName = argumentos.getString(KEY_NAME);
            this.mDeviceState = argumentos.getString(KEY_NETWORK);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnDisconnect = view.findViewById(R.id.btnDisconnect);
        btnConnect = view.findViewById(R.id.btnConnect);
        mTextviewDeviceName = view.findViewById(R.id.my_name);
        mTextviewDeviceState = view.findViewById(R.id.my_status);

        if (connected) {
            btnDisconnect.setClickable(true);
            btnDisconnect.setBackgroundColor(btnDisconnect.getContext().getResources().getColor(R.color.bluePrimary));
        } else {
            btnDisconnect.setClickable(false);
            btnDisconnect.setBackgroundColor(btnDisconnect.getContext().getResources().getColor(R.color.grayPrimary));
        }

        btnConnect.setOnClickListener(v -> {
            myInterface.connect();
            connected = true;
            btnDisconnect.setClickable(true);
            btnDisconnect.setBackgroundColor(btnDisconnect.getContext().getResources().getColor(R.color.bluePrimary));
        });
        btnDisconnect.setOnClickListener(v -> {
            myInterface.disconnect();
            connected = false;
            btnDisconnect.setClickable(false);
            btnDisconnect.setBackgroundColor(btnDisconnect.getContext().getResources().getColor(R.color.grayPrimary));
        });

        if (mDeviceName != null) {
            updateStrings();
        }
    }

    private void updateStrings() {
        mTextviewDeviceName.setText(String.format(Locale.getDefault(), "Dispositivo: %s", mDeviceName));
        mTextviewDeviceState.setText(String.format(Locale.getDefault(), "Red WI-FI: %s", mDeviceState));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_NAME, mDeviceName);
        outState.putString(KEY_NETWORK, mDeviceState);
        outState.putBoolean(KEY_STATE, connected);
    }
}
