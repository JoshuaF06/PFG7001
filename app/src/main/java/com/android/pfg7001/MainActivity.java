package com.android.pfg7001;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.pfg7001.dialogs.ConnectionDialog;
import com.android.pfg7001.fragments.ConnectFragment;
import com.android.pfg7001.fragments.StreamFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements ConnectFragment.IFindPeersFragment, ConnectionDialog.IConnectionDialog,
        StreamFragment.IStreamFragment {

    ConnectFragment connectFragment;
    StreamFragment streamFragment;
    private boolean isActive = false;
    private float gain1 = 1;
    private float gain2 = 1;
    private float gain3 = 1;
    private float gain4 = 1;
    private BottomNavigationView bottomNavigation;
    private static final String SERVER_IP = "192.168.4.1";
    private AudioTrack audioTrack1;
    private AudioTrack audioTrack2;
    private AudioTrack audioTrack3;
    private AudioTrack audioTrack4;
    private WifiManager wifimgr;
    private final String deviceName = android.os.Build.MODEL;
    private String wifiName;

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

    @Override
    protected void onResume() {
        super.onResume();

        wifiName = getRed();
        connectFragment = ConnectFragment.getInstance(deviceName, wifiName);

        openFragment(connectFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("HOLA", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifimgr = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        SocketHandler.setSocket1(null);
        SocketHandler.setSocket2(null);
        SocketHandler.setSocket3(null);
        SocketHandler.setSocket4(null);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        wifiName = getRed();

        connectFragment = ConnectFragment.getInstance(deviceName, wifiName);
        streamFragment = StreamFragment.getInstance(false);

       openFragment(connectFragment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MainActivity.PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);

            // After this point you wait for callback in
            // onRequestPermissionsResult(int, String[], int[]) overridden method
        }
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.itemConnect:
                        openFragment(connectFragment);
                        return true;
                    case R.id.itemStream:
                        openFragment(streamFragment);
                        if (!isActive) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TemaDialogoAlerta);

                            builder.setTitle("Alerta");
                            builder.setMessage("No hay ninguna conexión activa");
                            builder.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
                            builder.create();
                            builder.show();
                        }
                        return true;
                }
                return false;
            };

    public boolean conexion() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;

    }

    private String getRed() {
        WifiInfo info = wifimgr.getConnectionInfo();
        return info.getSSID();
    }

    private void comprobarRed() {
        ConnectionDialog dialog = new ConnectionDialog();
        if (conexion()) {
            WifiInfo info = wifimgr.getConnectionInfo();

            try {
                if (info.getSSID().equals("\"PFG7001\"")) {
                    isActive = true;
                    streamFragment = StreamFragment.getInstance(true);

                    bottomNavigation.setSelectedItemId(R.id.itemStream);

                    new setSocket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "8888");
                    new setSocket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "8889");
                    new setSocket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "8898");
                    new setSocket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "8899");

                } else {
                    dialog.show(getSupportFragmentManager(), "confirmación");
                }

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Error al leer la informacion", Toast.LENGTH_SHORT).show();

            }
        } else {
            dialog.show(getSupportFragmentManager(), "confirmación");
        }
    }

    @Override
    public void connect() {
        comprobarRed();
    }

    @Override
    public void disconnect() {
        Log.i("HOLA", "disconnect");
        Toast.makeText(MainActivity.this, "Desconexión exitosa", Toast.LENGTH_SHORT).show();
        streamFragment = StreamFragment.getInstance(false);
        isActive = false;

        bottomNavigation.setSelectedItemId(R.id.itemConnect);

        gain1 = 1;
        gain2 = 1;
        gain3 = 1;
        gain4 = 1;
    }

    @Override
    public void gain1(int gain) {
        gain1 = ((float) gain / 100);
        if (audioTrack1 != null)
            audioTrack1.setVolume(gain1);
    }

    @Override
    public void gain2(int gain) {
        gain2 = ((float) gain / 100);
        if (audioTrack2 != null)
            audioTrack2.setVolume(gain2);
    }

    @Override
    public void gain3(int gain) {
        gain3 = ((float) gain / 100);
        if (audioTrack3 != null)
            audioTrack3.setVolume(gain3);
    }

    @Override
    public void gain4(int gain) {
        gain4 = ((float) gain / 100);
        if (audioTrack4 != null)
            audioTrack4.setVolume(gain4);
    }

    @Override
    public void openSettings() {
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    public class setSocket extends AsyncTask<String, Void, String> {
        String port;

        @Override
        protected String doInBackground(String... strings) {
            port = strings[0];
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                Socket client = new Socket(serverAddr, Integer.parseInt(port));
                client.setTcpNoDelay(true);
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
                e.printStackTrace();
                return "";
            }
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
        private final int port;

        public ReceiveAudio(int port) {
            this.port = port;
        }

        @Override
        public void run() {
            try {
                Socket client;
                int intBufferSize = AudioRecord.getMinBufferSize(16000,
                        AudioFormat.CHANNEL_IN_STEREO,
                        AudioFormat.ENCODING_PCM_16BIT);

                byte[] shortAudioData = new byte[intBufferSize];

                switch (port) {
                    case 1:
                        client = SocketHandler.getSocket1();
                        audioTrack1 = new AudioTrack(AudioManager.STREAM_MUSIC
                                , 16000
                                , AudioFormat.CHANNEL_IN_STEREO
                                , AudioFormat.ENCODING_PCM_16BIT
                                , intBufferSize
                                , AudioTrack.MODE_STREAM);

                        audioTrack1.play();
                        break;
                    case 2:
                        client = SocketHandler.getSocket2();
                        audioTrack2 = new AudioTrack(AudioManager.STREAM_MUSIC
                                , 16000
                                , AudioFormat.CHANNEL_IN_STEREO
                                , AudioFormat.ENCODING_PCM_16BIT
                                , intBufferSize
                                , AudioTrack.MODE_STREAM);

                        audioTrack2.play();
                        break;
                    case 3:
                        client = SocketHandler.getSocket3();
                        audioTrack3 = new AudioTrack(AudioManager.STREAM_MUSIC
                                , 16000
                                , AudioFormat.CHANNEL_IN_STEREO
                                , AudioFormat.ENCODING_PCM_16BIT
                                , intBufferSize
                                , AudioTrack.MODE_STREAM);

                        audioTrack3.play();
                        break;
                    case 4:
                        client = SocketHandler.getSocket4();
                        audioTrack4 = new AudioTrack(AudioManager.STREAM_MUSIC
                                , 16000
                                , AudioFormat.CHANNEL_IN_STEREO
                                , AudioFormat.ENCODING_PCM_16BIT
                                , intBufferSize
                                , AudioTrack.MODE_STREAM);

                        audioTrack4.play();
                        break;
                    default:
                        client = null;
                }

                InputStream inputStream = client.getInputStream();

                while (isActive) {
                    int bytes = inputStream.read(shortAudioData);

                    switch (port) {
                        case 1:
                            audioTrack1.write(shortAudioData, 0, bytes);
                            Log.i("Tiempo", "" + Arrays.toString(shortAudioData));
                            break;
                        case 2:
                            audioTrack2.write(shortAudioData, 0, bytes);
                            break;
                        case 3:
                            audioTrack3.write(shortAudioData, 0, bytes);
                            break;
                        case 4:
                            audioTrack4.write(shortAudioData, 0, bytes);
                            break;
                    }
                }
                inputStream.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (audioTrack1 != null) {
                    audioTrack1.release();
                }
                if (audioTrack2 != null) {
                    audioTrack2.release();
                }
                if (audioTrack3 != null) {
                    audioTrack3.release();
                }
                if (audioTrack4 != null) {
                    audioTrack4.release();
                }
            }
        }
    }
}