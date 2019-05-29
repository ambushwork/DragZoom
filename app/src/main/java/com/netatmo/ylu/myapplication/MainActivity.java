package com.netatmo.ylu.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button reset;
    private HeaderView headerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reset = findViewById(R.id.reset);
        headerView = findViewById(R.id.header_view);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headerView.reset();
            }
        });
    }
}
