//App that records a sound from any audio inputs of the telephone.
package com.example.vitor.appdecoder;

import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.vitor.appdecoder.sound.SoundReceiver;
import com.example.vitor.appdecoder.sound.APIConection;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.widget.Button;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ExecutionException;

public class decodeAudio extends AppCompatActivity {

    private Button gravarButton;
    private Button stopButton;
    private Button destroyButton;
    private Button rqstButton;
    private TextView mTextView;
    private TextView mTextView2;
    private SoundReceiver mReceiver;
    private APIConection mAPI;


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (0 < msg.what) {
                mTextView.setText("");
            } else {
                if (msg.getData() != null) {
                    String s = msg.getData().getString("Text");
                    mTextView.setText(s);
                    mAPI.logar(s);
                    try {
                        //mAPI.logar(s);
                        //mProgressBar.setProgress(Integer.parseInt(s) / 10);
                    } catch (NumberFormatException e) {
                        // intentionally empty
                    }
                }
            }
        }
    };

    //Starts graphical elements of the page
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode_audio);
        Intent intent = getIntent();

        mTextView = (TextView) findViewById(R.id.tv);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        //mTextView.setText("");
        mTextView2 = (TextView) findViewById(R.id.tv2);

        gravarButton = (Button) findViewById(R.id.recordButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        destroyButton = (Button) findViewById(R.id.destroyButton);
        rqstButton = (Button) findViewById(R.id.rqstButton);


        gravarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReceiver.start();
                //mReceiver.stop();
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

        rqstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject obj = mAPI.mandar();
                    mTextView2.setText(obj.getString("data"));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mReceiver = new SoundReceiver(mHandler);
        mAPI = new APIConection();
    }

    @Override
    protected void onDestroy() {
        mReceiver.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mReceiver.stop();
        super.onPause();
        System.exit(0);
    }

}

