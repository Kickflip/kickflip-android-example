package io.kickflip.sample.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.melnykov.fab.FloatingActionButton;

import net.hockeyapp.android.CrashManager;

import java.util.Map;

import io.kickflip.sample.MainFragmentInteractionListener;
import io.kickflip.sample.R;
import io.kickflip.sample.SECRETS;
import io.kickflip.sample.Util;
import io.kickflip.sample.fragment.MainFragment;
import io.kickflip.sample.fragment.StreamListFragment;
import io.kickflip.sdk.Kickflip;
import io.kickflip.sdk.api.KickflipApiClient;
import io.kickflip.sdk.api.KickflipCallback;
import io.kickflip.sdk.api.json.Stream;
import io.kickflip.sdk.api.json.User;
import io.kickflip.sdk.av.BroadcastListener;
import io.kickflip.sdk.av.SessionConfig;
import io.kickflip.sdk.exception.KickflipException;
import io.kickflip.sdk.fragment.BroadcastFragment;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

import static io.kickflip.sdk.Kickflip.isKickflipUrl;


public class MainActivity extends AppCompatActivity implements MainFragmentInteractionListener, StreamListFragment.StreamListFragmenListener {
    private static final String TAG = "MainActivity";

    private FloatingActionButton mFab;
    private boolean mKickflipReady = false;

    private BroadcastListener mBroadcastListener = new BroadcastListener() {
        @Override
        public void onBroadcastStart() {
            Log.i(TAG, "onBroadcastStart");
        }

        @Override
        public void onBroadcastLive(Stream stream) {
            Log.i(TAG, "onBroadcastLive @ " + stream.getKickflipUrl());
        }

        @Override
        public void onBroadcastStop() {
            Log.i(TAG, "onBroadcastStop");

            // If you're manually injecting the BroadcastFragment,
            // you'll want to remove/replace BroadcastFragment
            // when the Broadcast is over.

            //getFragmentManager().beginTransaction()
            //    .replace(R.id.container, MainFragment.getInstance())
            //    .commit();
        }

        @Override
        public void onBroadcastError(KickflipException error) {
            Log.i(TAG, "onBroadcastError " + error.getMessage());
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        setContentView(R.layout.activity_main);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.hide(false);

        final SharedPreferences prefs = getSharedPreferences("app", Context.MODE_PRIVATE);
        // This must happen before any other Kickflip interactions
        Kickflip.setup(this, SECRETS.CLIENT_KEY, SECRETS.CLIENT_SECRET, true, new KickflipCallback<KickflipApiClient>() {
            @Override
            public void onSuccess(KickflipApiClient apiClient) {
                Log.i(TAG, "successfully registered KF app creds");
                mKickflipReady = true;
                mFab.show();

                // We passed 'true' to autoCreateUser in Kickflip#setup
                // but if we wanted to manually create a user with a specified username
                // we'd do something like below:
                /*
                if (!prefs.getBoolean("madeuser", false)) {
                    createUser(apiClient);
                } else {
                    loginUser(apiClient);
                }*/

                if (!handleLaunchingIntent()) {
                    if (savedInstanceState == null) {
                        getFragmentManager().beginTransaction()
                                .replace(R.id.content_fragment, new StreamListFragment())
                                .commit();
                    }
                }
            }

            @Override
            public void onError(KickflipException error) {
                Log.e(TAG, "Failed to setup kickflip");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForCrashes();
    }

    private void checkForCrashes() {
        CrashManager.register(this, SECRETS.HOCKEY_ID);
    }

    /**
     * Demonstrate creating a new Kickflip User with a specified username.
     * Note that all parameters beside username are mutable after creation
     * {@link KickflipApiClient#setUserInfo(String, String, String, Map, KickflipCallback)}
     */
    private void createUser(KickflipApiClient apiClient) {
        Log.i(TAG, "Creating new KF user...");
        apiClient.createNewUser("robertscoble", "testPass", "dbro@test.bork", "Nexus 5", null)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        // Handle error
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        Log.i(TAG, "successfully created new KF user " + user.getName());
                        getSharedPreferences("app", Context.MODE_PRIVATE).edit().putBoolean("madeuser", true).apply();
                    }
                });
    }

    /**
     * Demonstrate logging in an existing user with specified username and password.
     * You'd use this method to restore a Kickflip user across app installs or
     * across Kickflip SDKs. e.g: an Android user switches to iOS and wants to
     * carry over their Kickflip account.
     */
    private void loginUser(final KickflipApiClient apiClient) {
        Log.i(TAG, "Logging in KF user...");
        apiClient.loginUser("robertscoble", "testPass")
                .flatMap(new Func1<User, Observable<User>>() {
                    @Override
                    public Observable<User> call(User user) {
                        return apiClient.getUserInfo(user.getName());
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        // Handle error
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        Log.i(TAG, "successfully logged in KF user " + user.getName());
                        getSharedPreferences("app", Context.MODE_PRIVATE).edit().putBoolean("loggedIn", true).apply();
                    }
                });
    }

    public void onBroadcastButtonClicked(View v) {
        // Button shouldn't be visible before Kickflip is ready, but for clarity...
        if (mKickflipReady)
            startBroadcastingActivity();
        else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_title_not_ready))
                    .setMessage(getString(R.string.dialog_msg_not_ready))
                    .setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    public void onFragmentEvent(MainFragment.EVENT event) {
        startBroadcastingActivity();
    }

    /**
     * Unused method demonstrating how to use
     * Kickflip's BroadcastFragment.
     * <p/>
     * Note that in this scenario your Activity is responsible for
     * removing the BroadcastFragment in your onBroadcastStop callback.
     * When the user stops recording, the BroadcastFragment begins releasing
     * resources and freezes the camera preview.
     */
    public void startBroadcastFragment() {
        // Before using the BroadcastFragment, be sure to
        // register your BroadcastListener with Kickflip
        configureNewBroadcast();
        Kickflip.setBroadcastListener(mBroadcastListener);
        getFragmentManager().beginTransaction()
                .replace(R.id.content_fragment, BroadcastFragment.getInstance())
                .commit();
    }


    @Override
    public void onStreamPlaybackRequested(String streamUrl) {
        // Play with Kickflip's built-in Media Player
        Kickflip.startMediaPlayerActivity(this, streamUrl, false);

        // Play via Intent for 3rd party Media Player
        //Intent i = new Intent(Intent.ACTION_VIEW);
        //i.setDataAndType(Uri.parse(stream.getStreamUrl()), "application/vnd.apple.mpegurl");
        //startActivity(i);
    }

    private void startBroadcastingActivity() {
        configureNewBroadcast();
        Kickflip.startBroadcastActivity(this, mBroadcastListener);
    }

    private void configureNewBroadcast() {
        // Should reset mRecordingOutputPath between recordings
        SessionConfig config = Util.create720pSessionConfig(this);
        //SessionConfig config = Util.create420pSessionConfig(this);
        Kickflip.setSessionConfig(config);
    }

    private boolean handleLaunchingIntent() {
        Uri intentData = getIntent().getData();
        if (isKickflipUrl(intentData)) {
            Kickflip.startMediaPlayerActivity(this, intentData.toString(), true);
            finish();
            return true;
        }
        return false;
    }
}
