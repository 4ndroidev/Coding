package com.androidev.coding.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androidev.coding.Coding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Coding(this).owner("facebook").repo("react-native").branch("master").attach(R.id.coding);
    }
}
