package com.darkbeast0106.wifi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private Button btnOn, btnOff, btnInfo;
    private TextView textInfo;
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        btnOn.setOnClickListener(v -> {
            // API 29-től az alkalmazások nem kapcsolgathatják a wifit.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                textInfo.setText("Nincs jogosultság a wifi állapot módosítására");
                Intent panelIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                startActivityForResult(panelIntent, 0);
                return;
            }
            wifiManager.setWifiEnabled(true);
            textInfo.setText("Wifi bekapcsolva");
        });

        btnOff.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                textInfo.setText("Nincs jogosultság a wifi állapot módosítására");
                Intent panelIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                startActivityForResult(panelIntent, 0);
                return;
            }
            wifiManager.setWifiEnabled(false);
            textInfo.setText("Wifi kikapcsolva");
        });

        btnInfo.setOnClickListener(v -> {
            ConnectivityManager conManager =
                    (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (netInfo.isConnected()){
                int ip_number = wifiInfo.getIpAddress();
                String ip = Formatter.formatIpAddress(ip_number);
                textInfo.setText("IP: "+ip);
            } else {
                textInfo.setText("Nem csatlakoztál wifi hálózatra");
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED
                || wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING
            ){
                textInfo.setText("Wifi bekapcsolva");
            }else if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED
                    || wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLING
            ){
                textInfo.setText("Wifi kikapcsolva");
            }
        }
    }

    private void init() {
        btnOn = findViewById(R.id.btn_wifi_on);
        btnOff = findViewById(R.id.btn_wifi_off);
        btnInfo = findViewById(R.id.btn_wifi_info);
        textInfo = findViewById(R.id.text_info);

        wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
    }
}