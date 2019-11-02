package com.cchack.smartdoorlock;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //Database reference
    private DatabaseReference databaseReference;
    TextView textView;

    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();

    String pressed = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        databaseReference = FirebaseDatabase.getInstance().getReference("lock");

        getDoorState();
        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    private void getDoorState() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> snapshots = dataSnapshot.getChildren();

                for(DataSnapshot dataSnapshot1 : snapshots) {
                        Long state = dataSnapshot1.getValue(Long.class);
                        if (state == 1) {
                            textView.setText("Door is open");
                        } else if(state == 0) {
                            textView.setText("Door is closed");
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("Error", databaseError.toString());
            }
        });
    }

    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    private void startTimer(){
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run(){
                        getDoorState();
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 1, 3000);
    }

    public void pressed(View view) {
        Button button = (Button) view;

        if(button.getTag().toString().equals("C")) {
            databaseReference.child("pressed").setValue(Long.valueOf(pressed));
        } else {
            pressed += button.getTag().toString();
        }
        }
    }

