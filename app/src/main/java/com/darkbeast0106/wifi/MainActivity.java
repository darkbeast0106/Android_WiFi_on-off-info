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
            // Android 10-től (API 29) az alkalmazások nem kapcsolgathatják a wifit.
            // Éppen ezért meg kell vizsgálnunk a telepített Android verzióját.
            // Ha ez újabb akkor mást kell csinálnunk.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                textInfo.setText("Nincs jogosultság a wifi állapot módosítására");
                // Megnyitunk 1 beállítási panelt
                Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                // Panel bezárásakor szerentnénk valamit csinálni
                startActivityForResult(panelIntent, 0);
                return;
            }
            // Szükséges engedély: CHANGE_WIFI_STATE
            wifiManager.setWifiEnabled(true);
            textInfo.setText("Wifi bekapcsolva");
        });

        btnOff.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                textInfo.setText("Nincs jogosultság a wifi állapot módosítására");
                // Másik panelen is megtalálható a wifi kapcsolásához szükséges gomb.
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
            // Szükséges engedély: ACCESS_NETWORK_STATE
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

    // Akkor fog meghívódni amikor bezárjuk a megnyitott panelt.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // A requestCode az az érték amit mi adunk paraméterül startActivityForResult függvénynek.
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
        // Szükséges engedély: ACCESS_WIFI_STATE
        wifiInfo = wifiManager.getConnectionInfo();
    }
}