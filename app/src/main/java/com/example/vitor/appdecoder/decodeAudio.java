//App that records a sound from any audio inputs of the telephone.
package com.example.vitor.appdecoder;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.example.vitor.appdecoder.sound.SoundReceiver;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.widget.Button;
import android.util.Log;

public class decodeAudio extends AppCompatActivity {

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            if (0 < msg.what) {
                mTextView.setText("");
            } else {
                if (msg.getData() != null) {
                    String s = msg.getData().getString("Text");
                    mTextView.setText(s);
                    try {
                        mProgressBar.setProgress(Integer.parseInt(s) / 10);
                    } catch (NumberFormatException e) {
                        // intentionally empty
                    }
                }
            }
        }


    };

    private Button gravarButton;
    private Button stopButton;
    private Button destroyButton;

    private ProgressBar mProgressBar;
    private TextView mTextView;



    private SoundReceiver mReceiver;
    //SoundReceiver client = new SoundReceiver(mHandler);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_decode_audio);
        gravarButton = (Button) findViewById(R.id.recordButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        destroyButton = (Button) findViewById(R.id.destroyButton);

        mProgressBar = (ProgressBar) findViewById(R.id.pb);
        mProgressBar.setProgress(0);

        mTextView = (TextView) findViewById(R.id.tv);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        mTextView.setText("");


        gravarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReceiver.start();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReceiver.stop();
            }
        });

        destroyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReceiver.destroy();
            }
        });

        mReceiver = new SoundReceiver(mHandler);
    }
    @Override
    protected void onDestroy() {
        mReceiver.destroy();
        super.onDestroy();
    }
}
