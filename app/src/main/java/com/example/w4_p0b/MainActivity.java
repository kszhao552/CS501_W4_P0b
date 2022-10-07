package com.example.w4_p0b;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {
    private Switch sw;
    private EditText edt;
    private Camera cm;
    private Camera.Parameters p;
    private GestureDetectorCompat mDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sw = (Switch) findViewById(R.id.switch1);
        edt = (EditText) findViewById(R.id.action);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        cm = getCameraInstance();

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setLight(b);
            }
        });

        edt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String s = edt.getText().toString();
                if (s.toLowerCase().matches("on")){
                    setChecked(true);
                    setLight(true);
                    edt.setText("");
                } else if (s.toLowerCase().matches("off")){
                    setChecked(false);
                    setLight(false);
                    edt.setText("");
                }
            }
        });
    }

    private void setChecked(boolean b){
            sw.setChecked(b);
    }

    private void setLight(boolean b) {
        try {
            if (b) {
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cm.setParameters(p);
            } else {
                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                cm.setParameters(p);
            }
        } catch (Exception e){

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cm == null) {
            cm = getCameraInstance();
        }
        if (cm == null) {
            sw.setEnabled(false);
        }else {
            cm.startPreview();
            p = cm.getParameters();
            if (sw.isChecked()){
                setLight(true);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        cm.stopPreview();
        cm.release();
        cm = null;
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            if (Math.abs(velocityY) > 5000) {
                if (velocityY < 0){
                    setChecked(true);
                    setLight(true);
                    Log.w("TAG", "onFling: On" );
                } else {
                    setChecked(false);
                    setLight(false);
                    Log.w("TAG", "onFling: Off" );

                }
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}