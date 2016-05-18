package com.maneater.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.maneater.library.WaveView;

public class MainActivity extends AppCompatActivity {

    private WaveView vWaveView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vWaveView = (WaveView) findViewById(R.id.vWaveView);
        vWaveView.demo();
    }
}
