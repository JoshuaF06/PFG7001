package com.android.pfg7001;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.pfg7001.fragments.FindPeersFragment;
import com.android.pfg7001.fragments.SettingsFragment;
import com.android.pfg7001.fragments.StreamFragment;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener, FindPeersFragment.IFindPeersFragment,
        StreamFragment.IStreamFragment, TabLayout.OnTabSelectedListener {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private final List<WifiP2pDevice> peers = new ArrayList<>();
    private String[] deviceNameArray;
    private WifiP2pDevice[] deviceArray;
    private boolean retryChannel = false;
    FindPeersFragment findPeersFragment;
    StreamFragment streamFragment;
    SettingsFragment settingsFragment;
    private AlertDialog mDialog;
    private boolean isActive = false;
    private float gain1 = 1;
    private float gain2 = 1;
    private float gain3 = 1;
    private float gain4 = 1;
    private TabLayout tabLayout;

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e("TAG1", "Fine location permission is not granted!");
                    finish();
                }
                break;
        }
    }

    private boolean initP2p() {
        // Device capability definition check
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Log.e("HOLA", "Wi-Fi Direct is not supported by this device.");
            return false;
        }

        // Hardware capability check
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            Log.e("HOLA", "Cannot get Wi-Fi system service.");
            return false;
        }

        if (!wifiManager.isP2pSupported()) {
            Log.e("HOLA", "Wi-Fi Direct is not supported by the hardware or Wi-Fi is off.");
            //return false;
        }

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        if (mManager == null) {
            Log.e("HOLA", "Cannot get Wi-Fi Direct system service.");
            return false;
        }

        mChannel = mManager.initialize(this, getMainLooper(), null);
        if (mChannel == null) {
            Log.e("HOLA", "Cannot initialize Wi-Fi Direct.");
            return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("HOLA", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SocketHandler.setSocket1(null);
        SocketHandler.setSocket2(null);
        SocketHandler.setSocket3(null);
        SocketHandler.setSocket4(null);


        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        if (!initP2p()) {
            finish();
        }

        tabLayout = findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            tabLayout.addOnTabSelectedListener(this);
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            if (tab != null) {
                tab.select();
                tab.setText(R.string.conection);
            }
        }

        findPeersFragment = new FindPeersFragment();
        streamFragment = StreamFragment.getInstance(false);
        settingsFragment = new SettingsFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, findPeersFragment)
                .commit();

        mDialog = new SpotsDialog.Builder().setContext(MainActivity.this).setMessage("Buscando").build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);

            // After this point you wait for callback in
            // onRequestPermissionsResult(int, String[], int[]) overridden method
        }


    }

    @Override
    public void btnDiscover() {
        Log.i("HOLA", "btnDiscover");
        mDialog.show();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("HOLA", "Fantan permisos");
        }
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i("HOLA", "onSuccess");
            }

            @Override
            public void onFailure(int reason) {
                Log.i("HOLA", "onFailure");
                mDialog.dismiss();
                Toast.makeText(MainActivity.this, "Error al buscar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onListClick(int position) {
        final WifiP2pDevice device = deviceArray[position];
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("HOLA", "Fantan permisos");
        }
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Error connecting to" + device.deviceName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {
        Log.i("HOLA", "disconnect");
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Disconnect successful", Toast.LENGTH_SHORT).show();
                streamFragment = StreamFragment.getInstance(false);
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Failed to disconnect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void gain1(int gain) {
        gain1 = ((float) gain / 100);
    }

    @Override
    public void gain2(int gain) {
        gain2 = ((float) gain / 100);
    }

    @Override
    public void gain3(int gain) {
        gain3 = ((float) gain / 100);
    }

    @Override
    public void gain4(int gain) {
        gain4 = ((float) gain / 100);
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peersList) {
            Log.i("HOLA", "peerListListener");
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            if (!peersList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peersList.getDeviceList());

                deviceNameArray = new String[peersList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peersList.getDeviceList().size()];

                int index = 0;
                for (WifiP2pDevice device : peersList.getDeviceList()) {
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }
                findPeersFragment.updateList(deviceNameArray);
            }

            if (peers.size() == 0) {
                Toast.makeText(MainActivity.this, "No devices found", Toast.LENGTH_SHORT).show();
            }
        }
    };

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            Log.i("HOLA", "connectionInfoListener");
            final InetAddress groupOwnerAddress = info.groupOwnerAddress;

            if (info.groupFormed && info.isGroupOwner) {
                Log.i("HOLA", "Error al configurar con cliente");
            } else if (info.groupFormed) {
                isActive = true;
                streamFragment = StreamFragment.getInstance(true);

                TabLayout.Tab tab = tabLayout.getTabAt(1);
                if (tab != null) {
                    tab.select();
                    tab.setText(R.string.music);
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, streamFragment)
                        .commit();

                new setSocket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupOwnerAddress.getHostAddress(), "8888");
                new setSocket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupOwnerAddress.getHostAddress(), "8889");
                new setSocket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupOwnerAddress.getHostAddress(), "8898");
                new setSocket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupOwnerAddress.getHostAddress(), "8899");
            } else {
                Log.i("HOLA", "Error al configurar con cliente");
            }
        }
    };

    @Override
    public void onChannelDisconnected() {
        Log.i("HOLA", "onChannelDisconnected");
        if (mManager != null && !retryChannel) {
            Toast.makeText(this, "Conexion perdida, intentado conectar", Toast.LENGTH_LONG).show();
            retryChannel = true;
            mManager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Se perdio la conexion con el servidor. Intenta deshabilitar/Re-habilitar P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        String[] tabsText = getResources().getStringArray(R.array.tab_text);
        int position = tab.getPosition();
        tab.setText(tabsText[position]);

        Fragment fragment;
        switch (position) {
            case 0:
                fragment = findPeersFragment;
                break;
            case 1:
                if (!isActive){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TemaDialogoAlerta);

                    builder.setTitle("Alerta");
                    builder.setMessage("No hay ninguna conexión activa");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.create();
                    builder.show();
                }
                fragment = streamFragment;
                break;
            case 2:
                fragment = settingsFragment;
                break;
            default:
                fragment = null;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        tab.setText("");
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public class setSocket extends AsyncTask<String, Void, String> {
        String hostAdd;
        String port;

        @Override
        protected String doInBackground(String... strings) {
            hostAdd = strings[0];
            port = strings[1];
            Log.i("HOLA", "" + hostAdd + " " + port);
            try {
                Socket client = new Socket();
                client.connect(new InetSocketAddress(hostAdd, Integer.parseInt(port)), 5000);
                Log.i("HOLA", "socket " + client);
                switch (port) {
                    case "8888":
                        SocketHandler.setSocket1(client);
                        break;
                    case "8889":
                        SocketHandler.setSocket2(client);
                        break;
                    case "8898":
                        SocketHandler.setSocket3(client);
                        break;
                    case "8899":
                        SocketHandler.setSocket4(client);
                        break;
                }
                return port;
            } catch (IOException e) {
                Log.i("HOLA", "Error " + e);
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            int selectPort;

            switch (s) {
                case "8888":
                    selectPort = 1;
                    break;
                case "8889":
                    selectPort = 2;
                    break;
                case "8898":
                    selectPort = 3;
                    break;
                case "8899":
                    selectPort = 4;
                    break;
                default:
                    selectPort = 0;
            }

            if (selectPort != 0) {
                ReceiveAudio receiveAudio = new ReceiveAudio(selectPort);
                receiveAudio.start();
            }
        }
    }

    public class ReceiveAudio extends Thread {
        private AudioTrack audioTrack;
        private final int port;

        public ReceiveAudio(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            try {
                Log.i("HOLA", "ReceiveAudio run");
                Log.i("HOLA", "port " + port);
                Socket client;
                switch (port) {
                    case 1:
                        client = SocketHandler.getSocket1();
                        break;
                    case 2:
                        client = SocketHandler.getSocket2();
                        break;
                    case 3:
                        client = SocketHandler.getSocket3();
                        break;
                    case 4:
                        client = SocketHandler.getSocket4();
                        break;
                    default:
                        client = null;
                }
                Log.i("HOLA", "socket " + client);
                InputStream inputStream = client.getInputStream();

                int intBufferSize = AudioRecord.getMinBufferSize(4000,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                byte[] shortAudioData = new byte[intBufferSize];

                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC
                        , 4000
                        , AudioFormat.CHANNEL_IN_STEREO
                        , AudioFormat.ENCODING_PCM_16BIT
                        , intBufferSize
                        , AudioTrack.MODE_STREAM);

//                audioTrack.setPlaybackRate(intRecordSampleRate);
                audioTrack.play();

                while (isActive) {
                    int bytes = inputStream.read(shortAudioData);

                    switch (port) {
                        case 1:
                            for (int i = 0; i < shortAudioData.length; i++) {
                                shortAudioData[i] = (byte) Math.min(shortAudioData[i] * gain1, Short.MAX_VALUE);
                            }
                            break;
                        case 2:
                            for (int i = 0; i < shortAudioData.length; i++) {
                                shortAudioData[i] = (byte) Math.min(shortAudioData[i] * gain2, Short.MAX_VALUE);
                            }
                            break;
                        case 3:
                            for (int i = 0; i < shortAudioData.length; i++) {
                                shortAudioData[i] = (byte) Math.min(shortAudioData[i] * gain3, Short.MAX_VALUE);
                            }
                            break;
                        case 4:
                            for (int i = 0; i < shortAudioData.length; i++) {
                                shortAudioData[i] = (byte) Math.min(shortAudioData[i] * gain4, Short.MAX_VALUE);
                            }
                            break;
                    }

                    audioTrack.write(shortAudioData, 0, bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (audioTrack != null) {
                    audioTrack.stop();
                    audioTrack.release();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        Log.i("HOLA", "onResume");
        super.onResume();
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        Log.i("HOLA", "onPause");
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}