package edu.poly.androidtodoapp;

import android.app.ProgressDialog;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private Fragment mContent;
    public final static String FRAGMENT_TAG = "MyFragment";
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        //get firebase auth instance
        fAuth = FirebaseAuth.getInstance();
        //firebase messaging instance
        FirebaseMessaging fMsg = FirebaseMessaging.getInstance();
        //firebase instance to subscribe topics
        fMsg.subscribeToTopic("todos");
        //firebase analytics instance
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Sets whether analytics collection is enabled for this app on this device.
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);

        //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 20 seconds just for the fun
        mFirebaseAnalytics.setMinimumSessionDuration(20000);

        //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes).
        mFirebaseAnalytics.setSessionTimeoutDuration(600000);
        if (savedInstanceState == null) {
            // To get the currently signed-in user by calling getCurrentUser
            // If no currently user signed in, we'll redirect the user to login screen else home screen
            Bundle bundle = new Bundle();
            if (fAuth.getCurrentUser() != null) {
                mContent = HomeFragment.newInstance();
                bundle.putString(FirebaseAnalytics.Param.DESTINATION, "Home Fragment");
            } else {
                mContent = LoginSignUpFragment.newInstance();
                bundle.putString(FirebaseAnalytics.Param.DESTINATION, "Login Fragment");
            }
            FragmentManager fm = getSupportFragmentManager();
            // This Log event logs whether the user has logged in before or not
            bundle.putString(FirebaseAnalytics.Param.ORIGIN, "Main Activity");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.content, mContent, FRAGMENT_TAG);
            ft.commit();
        } else {
            mContent = getSupportFragmentManager().getFragment(
                    savedInstanceState, FRAGMENT_TAG);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mContent = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (mContent != null)
            getSupportFragmentManager().putFragment(outState, FRAGMENT_TAG, mContent);
    }


}
