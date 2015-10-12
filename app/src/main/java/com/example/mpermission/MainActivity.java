package com.example.mpermission;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    /** Google Cloud Messagingオブジェクト */
    private GoogleCloudMessaging gcm;
    /** コンテキスト */
    private Context context;

    private final String PPROJECT_NUMBER = "<API_KEY>";

    // 定义LocationManager对象
    private LocationManager locationManager;
    private TextView tv1;
    private Button btn;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        gcm = GoogleCloudMessaging.getInstance(this);
        registerInBackground();

        tv1 = (TextView) findViewById(R.id.textView1);

        // 获取系统LocationManager服务
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessageOKCancel("You need to allow access to Location",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }

        // 从GPS获取最近的定位信息
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // 将location里的位置信息显示在EditText中
        updateView(location);
        // 设置每2秒获取一次GPS的定位信息
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 8, new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                // 当GPS定位信息发生改变时，更新位置
                updateView(location);
            }

            @Override
            public void onProviderDisabled(String provider) {
                updateView(null);
            }

            @Override
            public void onProviderEnabled(String provider) {
                // 当GPS LocationProvider可用时，更新位置
                updateView(locationManager.getLastKnownLocation(provider));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        });

        btn = (Button) findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sdcardIntent = new Intent(MainActivity.this, SdcardActivity.class);
                startActivity(sdcardIntent);
            }
        });
    }

    private void updateView(Location location) {
        if (location != null) {
            StringBuffer sb = new StringBuffer();
            sb.append("GPS情報：\n経度：");
            sb.append(location.getLongitude());
            sb.append("\n緯度：");
            sb.append(location.getLatitude());
            sb.append("\n高度：");
            sb.append(location.getAltitude());
            sb.append("\n速度：");
            sb.append(location.getSpeed());
            sb.append("\n方向：");
            sb.append(location.getBearing());
            sb.append("\n精度：");
            sb.append(location.getAccuracy());
            tv1.setText(sb.toString());
        } else {
            // 如果传入的Location对象为空则清空EditText
            tv1.setText("");
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    String regid = gcm.register(PPROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
            }
        }.execute(null, null, null);
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
