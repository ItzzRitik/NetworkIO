package xtremedeveloper.networkio;

import android.animation.Animator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    TextView mac,res,reset;
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

        reset=findViewById(R.id.reset);
        results=findViewById(R.id.results);
        results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animator revealAnimator = ViewAnimationUtils.createCircularReveal(results,card.getWidth()/2,card.getHeight()/2, card.getWidth()*707/500,0);
                revealAnimator.addListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animator) {reg.setText("");}
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        results.setVisibility(View.GONE);pro.setVisibility(View.VISIBLE);
                        res.setText("");
                        reg.requestFocus();
                    }
                    @Override public void onAnimationCancel(Animator animator) {}
                    @Override public void onAnimationRepeat(Animator animator) {}
                });
                revealAnimator.setInterpolator(new DecelerateInterpolator());
                revealAnimator.setDuration(500);
                revealAnimator.start();
            }
        });

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
                                pro.setVisibility(View.VISIBLE);pro.requestFocus();
                                revealAnimator.setDuration(500);
                                revealAnimator.setInterpolator(new AccelerateInterpolator());
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
    public void getResults(final String data)
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    URL u = new URL("https://android-club-project.herokuapp.com/upload_details?reg_no="+data+"&mac="+macAdd);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("GET");
                    c.connect();
                    InputStream in = c.getInputStream();
                    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    in.read(buffer);
                    bo.write(buffer);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                res.setText(bo.toString().trim());pro.setVisibility(View.GONE);
                                reset.setVisibility(View.VISIBLE);
                                bo.close();
                            } catch (IOException e) {
                                res.setText("Something Went\nWrong");pro.setVisibility(View.GONE);reset.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                catch (Exception e){res.setText("Something Went\nWrong");pro.setVisibility(View.GONE);reset.setVisibility(View.VISIBLE);}
            }
        }).start();
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
