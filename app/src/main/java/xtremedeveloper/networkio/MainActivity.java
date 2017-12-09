package xtremedeveloper.networkio;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView mac;
    ProgressBar pro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mac=findViewById(R.id.mac);
        mac.setText(getMacAddr());

        pro=findViewById(R.id.pro);
        pro.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.orange),android.graphics.PorterDuff.Mode.MULTIPLY);
    }
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {return "";}
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {res1.append(Integer.toHexString(b & 0xFF) + ":");}
                if (res1.length() > 0) {res1.deleteCharAt(res1.length() - 1);}
                return res1.toString();
            }
        } catch (Exception ex) {}
        return "02:00:00:00:00:00";
    }

}
