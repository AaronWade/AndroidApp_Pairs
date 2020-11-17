package com.example.aaron.assignment2;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    CustomView cv;
    Activity mActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = MainActivity.this;
    }

    /**
     * Takes in the players scores and current player from the CustomView
     * and updates the display so they can be read by the user
     * @param score
     * @param currentPlayer
     */
    public void updateMainActivity(String score, String currentPlayer){
        TextView tv_score = findViewById(R.id.score);
        TextView tv_currentPlayer = findViewById(R.id.currentPlayer);
        tv_score.setText(score);
        tv_currentPlayer.setText(currentPlayer);

        cv = (CustomView)findViewById(R.id.myCustomView);

        Button restart = findViewById(R.id.restart);
        restart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                restart(mActivity);
            }
        });
    }

    /**
     * Read in the MainActivity and call it to restart the application and
     * return all variables to their original state
     * @param activity
     */
    public void restart(Activity activity){
        if(Build.VERSION.SDK_INT >= 11){
            activity.recreate();
        }
        else{
            activity.finish();
            activity.startActivity(activity.getIntent());
        }
    }
}
