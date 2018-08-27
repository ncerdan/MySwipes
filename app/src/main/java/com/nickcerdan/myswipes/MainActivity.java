package com.nickcerdan.myswipes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    int swipesLeft;
    Button swipeBtn;
    EditText swipesLeftNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeBtn = (Button) findViewById(R.id.swipeBtn);
        swipesLeftNum = (EditText) findViewById(R.id.swipesLeftNum);
        swipesLeft = 70;

        swipesLeftNum.setText(String.valueOf(swipesLeft));

        swipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipesLeft = swipesLeft - 1;
                swipesLeftNum.setText(String.valueOf(swipesLeft));
            }
        });
    }
}
