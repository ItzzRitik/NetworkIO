package xtremedeveloper.networkio;

import android.animation.Animator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    TextView mac,res;
    ProgressBar pro;
    EditText reg;
    RelativeLayout results,card;
    String macAdd="";
    Pattern pat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mac=findViewById(R.id.mac);
        macAdd=getMacAddr();
        mac.setText(macAdd);

        card=findViewById(R.id.card);
        results=findViewById(R.id.results);

        pat = Pattern.compile("[1-2][0-9][a-z]{3}[1-9]{4}");
        reg = findViewById(R.id.reg);
        reg.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            if(pat.matcher(reg.getText().toString().toLowerCase()).matches())
                            {
                                Animator revealAnimator = ViewAnimationUtils.createCircularReveal(results,card.getWidth()/2,card.getHeight()/2, 0, card.getWidth()*707/500);
                                results.setVisibility(View.VISIBLE);
                                pro.setVisibility(View.VISIBLE);
                                revealAnimator.setDuration(500);
                                revealAnimator.start();
                                getResults(reg.getText().toString().substring(reg.length() - 4));
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this, "Invalid Registration Number", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        res=findViewById(R.id.res);

        pro=findViewById(R.id.pro);
        pro.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.orange),android.graphics.PorterDuff.Mode.MULTIPLY);
    }
    public void getResults(String data)
    {
        new StringRequest(Request.Method.GET,"https://android-club-project.herokuapp.com/upload_details?reg_no="+data+"&mac="+macAdd,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        res.setText("Recieved Message\n"+ response.substring(0,500).trim());pro.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                res.setText("Something Went\nWrong");pro.setVisibility(View.GONE);
            }
        });
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
