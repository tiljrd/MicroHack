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
    
    //TextView which shows the state of the door
    TextView textView;

    //Timer for checking the status of the door every three seconds
    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();

    //not yet completely implemented but needed for opening the door with the phone
    String pressed = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialise the Text view and the database reference
        textView = findViewById(R.id.textView);
        databaseReference = FirebaseDatabase.getInstance().getReference("lock");

        //check the door state by retrieving it from the database
        getDoorState();
        
        //start the timer to check for the state every three seconds
        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        //stop the timer when the activity is destroyed
        stopTimer();
    }

    //get the door state from the database
    //0 or 1 for closed or open respectively
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

            //when the connection to the database fails
            //still room for improvement here
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("Error", databaseError.toString());
            }
        });
    }

    //stop the timer which retrieves the state of the database
    private void stopTimer(){
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    //start the timer to retrieve the state of the door from the database every three seconds
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

    //using Tags find out what passcode was pressed by the user
    //still not fully implemented (opening the door with the phone)
    public void pressed(View view) {
        
        Button button = (Button) view;

        //upload the confiirmed code into the database
        if(button.getTag().toString().equals("C")) {
            databaseReference.child("pressed").setValue(Long.valueOf(pressed));
            
        //add the characters that the user is pressing together to a passcode
        } else {
            pressed += button.getTag().toString();
        }
        }
    }

