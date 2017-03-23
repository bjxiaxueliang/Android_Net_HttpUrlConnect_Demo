package com.example.scalephoto;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";


    private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_main);
        //
        mBtn = (Button) findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Task.call(new Callable<PubIP>() {
                    @Override
                    public PubIP call() throws Exception {
                        PubIP pubIP = HttpAgent.get_Sync("http://ip.chinaz.com/getip.aspx", null, null, PubIP.class);
                        return pubIP;
                    }
                }, Task.BACKGROUND_EXECUTOR).continueWith(new Continuation<PubIP, Object>() {
                    @Override
                    public Object then(Task<PubIP> task) throws Exception {
                        if (task.getResult() != null) {
                            mBtn.setText(task.getResult().toString());
                        }
                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);

            }
        });


    }


    /**
     * 公网Ip
     */
    public static class PubIP {
        private String ip;
        private String address;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("ip: ");
            sb.append(ip);
            sb.append(" address: ");
            sb.append(address);
            return sb.toString();
        }
    }
}