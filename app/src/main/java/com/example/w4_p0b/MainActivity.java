package com.example.w4_p0b;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
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
    private boolean avail = true;
    private GestureDetectorCompat mDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sw = (Switch) findViewById(R.id.switch1);
        edt = (EditText) findViewById(R.id.action);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

//        try {
//        cm = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
//            p = cm.getParameters();
//            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
//                sw.setEnabled(false);
//                avail = false;
//        } catch (Exception e){
//            sw.setEnabled(false);
//            avail = false;
//        }

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
        if (avail) {
            sw.setEnabled(b);
        }
    }

    private void setLight(boolean b){
        if (avail){
            if (b){
               p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
               cm.setParameters(p);
            }
            else{
                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                cm.setParameters(p);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                sw.setEnabled(false);
                avail = false;
            } else if (p.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
                avail = true;
                sw.setEnabled(true);
                setChecked(false);
            } else if (p.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                avail = true;
                sw.setEnabled(true);
                setChecked(true);
            }
        } catch (Exception e) {
            avail = false;
            sw.setEnabled(false);
        }
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

}