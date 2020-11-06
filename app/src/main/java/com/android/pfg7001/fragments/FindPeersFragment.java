package com.android.pfg7001.fragments;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.pfg7001.R;

import java.util.Locale;

public class FindPeersFragment extends Fragment {

    private ListView listView;
    private Button btnDesconectar;
    private IFindPeersFragment myInterface;
    String[] deviceNameArray;
    private TextView mTextviewDeviceName;
    private TextView mTextviewDeviceState;
    private String mDeviceName;
    private String mDeviceState;
    private static final String KEY_NAME = "setName";
    private static final String KEY_STATE = "setState";

    public interface IFindPeersFragment {
        void btnDiscover();

        void onListClick(int position);

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
            mDeviceState = savedInstanceState.getString(KEY_STATE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_peers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnDiscover = view.findViewById(R.id.discover);
        btnDesconectar = view.findViewById(R.id.btnDesconectar);
        listView = view.findViewById(R.id.peerListView);
        mTextviewDeviceName = view.findViewById(R.id.my_name);
        mTextviewDeviceState = view.findViewById(R.id.my_status);

        btnDiscover.setOnClickListener(v -> myInterface.btnDiscover());
        btnDesconectar.setOnClickListener(v -> myInterface.disconnect());
        listView.setOnItemClickListener((parent, view1, position, id) -> myInterface.onListClick(position));

        if (mDeviceName != null) {
            updateStrings();
        }
    }

    private void updateStrings() {
        mTextviewDeviceName.setText(String.format(Locale.getDefault(), "Dispositivo: %s", mDeviceName));
        mTextviewDeviceState.setText(String.format(Locale.getDefault(), "Estado: %s", mDeviceState));
        if (mDeviceState.equals("Conectado")) {
            btnDesconectar.setClickable(true);
            btnDesconectar.setBackgroundColor(btnDesconectar.getContext().getResources().getColor(R.color.bluePrimary));
        } else {
            btnDesconectar.setClickable(false);
            btnDesconectar.setBackgroundColor(btnDesconectar.getContext().getResources().getColor(R.color.grayPrimary));
        }
    }

    public void updateList(String[] deviceNameArray) {
        this.deviceNameArray = deviceNameArray;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, deviceNameArray);
        listView.setAdapter(adapter);
    }

    public void updateThisDevice(WifiP2pDevice device) {
        mDeviceName = device.deviceName;
        mDeviceState = getDeviceStatus(device.status);
        updateStrings();
    }

    private static String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Disponible";
            case WifiP2pDevice.INVITED:
                return "Invitado";
            case WifiP2pDevice.CONNECTED:
                return "Conectado";
            case WifiP2pDevice.FAILED:
                return "Fallido";
            case WifiP2pDevice.UNAVAILABLE:
                return "No disponible";
            default:
                return "Desconocido";

        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_NAME, mDeviceName);
        outState.putString(KEY_STATE, mDeviceState);
    }
}
