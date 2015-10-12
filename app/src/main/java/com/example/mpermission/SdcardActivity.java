package com.example.mpermission;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class SdcardActivity extends AppCompatActivity {

    private Button bt1, bt2;
    private EditText et1, et2;

    private static final String FILENAME = "temp_file.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdcard);
        bt1 = (Button) this.findViewById(R.id.bt1);
        bt2 = (Button) this.findViewById(R.id.bt2);
        et1 = (EditText) this.findViewById(R.id.et1);
        et2 = (EditText) this.findViewById(R.id.et2);
        bt1.setOnClickListener(new MySetOnClickListener());
        bt2.setOnClickListener(new MySetOnClickListener());
    }

    private class MySetOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            File file = new File(Environment.getExternalStorageDirectory(), FILENAME);
            switch (v.getId()) {
                case R.id.bt1:// 使用SDcard写操作
                    if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED)) {
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(et1.getText().toString().getBytes());
                            fos.close();
                            Toast.makeText(SdcardActivity.this, "写入文件成功", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(SdcardActivity.this, "写入文件失败",  Toast.LENGTH_SHORT).show();
                        }
                    };
                    break;

                case R.id.bt2:
                    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        try {
                            FileInputStream fis = new FileInputStream(file);
                            int length = fis.available();
                            byte [] buffer = new byte[length];
                            fis.read(buffer);
                            String text = new String(buffer, "UTF-8");
                            et2.setText(text);
                            fis.close();
                            Toast.makeText(SdcardActivity.this, "读取文件成功", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(SdcardActivity.this, "读取文件失败",  Toast.LENGTH_SHORT).show();
                        }
                    };
                    break;
            }
        }
    }
}
