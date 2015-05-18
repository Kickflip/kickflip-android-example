package io.kickflip.sample.activity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

import io.kickflip.sample.R;
import io.kickflip.sample.Util;
import io.kickflip.sdk.activity.ImmersiveActivity;
import io.kickflip.sdk.av.AVRecorder;
import io.kickflip.sdk.av.SessionConfig;

/**
 * Demonstrates using the Kickflip SDK components to
 * create a more traditional Camera app that allows creating multiple
 * recordings per Activity lifecycle.
 *
 * In this example recording will stop if the Activity proceeds through {@link #onStop()}
 *
 * <b>Note:</b> This Activity is marked in AndroidManifest.xml with the property android:configChanges="orientation|screenSize"
 *              This is a shortcut to prevent the onDestroy ... onCreate ... onDestroy cycle when the screen powers off. Without this
 *              shortcut, more careful management of application state is required to make sure you don't create a recorder and set the preview
 *              display as a result of that ephemeral onCreate. If you do, you'll get a crash as the Camera won't be able to be acquired.
 *              See: http://stackoverflow.com/questions/10498636/prevent-android-activity-from-being-recreated-on-turning-screen-off
 */
public class CameraActivity extends ImmersiveActivity {

    private static final String TAG = "CameraActivity";

    private AVRecorder mRecorder;
    private Button mRecordingButton;
    private boolean mFirstRecording = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mRecordingButton = (Button) findViewById(R.id.recordButton);
        mRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecorder.isRecording()) {
                    mRecordingButton.setBackgroundResource(R.drawable.red_dot);
                    mRecorder.stopRecording();
                } else {
                    if (!mFirstRecording) {
                        resetAVRecorder();
                    } else {
                        mFirstRecording = false;
                    }
                    mRecorder.startRecording();
                    mRecordingButton.setBackgroundResource(R.drawable.red_dot_stop);
                }
            }
        });
        SessionConfig config = Util.create720pSessionConfig(createNewRecordingFile());
        if (mRecorder == null) {
            // This null check exists because onCreate may be called in the process of
            // destroying the activity
            try {
                mRecorder = new AVRecorder(config);
                mRecorder.setPreviewDisplay((io.kickflip.sdk.view.GLCameraView) findViewById(R.id.cameraPreview));
            } catch (IOException e) {
                // Could not create recording at given file path
                Log.e(TAG, "Failed to start AVRecorder!");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mRecorder.isRecording()) mRecorder.stopRecording();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecorder.release();
    }

    private String createNewRecordingFile() {
        return new File(Environment.getExternalStorageDirectory(), String.format("MySampleApp/%d.mp4",System.currentTimeMillis())).getAbsolutePath();
    }

    private void resetAVRecorder() {
        try {
            mRecorder.reset(Util.create720pSessionConfig(createNewRecordingFile()));
        } catch (IOException e) {
            // Could not create recording at given file path
            Log.e(TAG, "Failed to reset AVRecorder!");
            e.printStackTrace();
        }
    }

}
