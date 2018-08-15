package com.example.vitor.appdecoder.sound;

import android.util.Log;
import android.os.Handler;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;


public class SoundReceiver {

    static final String LOG_TAG = SoundReceiver.class.getSimpleName();
    private final Analyzer mAnalyzer;
    private AudioRecord mAudioInput;
    private AudioReaderThread mAudioReader;

    public SoundReceiver(final Handler handler) {
        mAnalyzer = new Analyzer(new Decoder(handler));
    }

    public void start() {
        Log.d("TAG","Start recording...");
        final int recBufferSize = 4 * AudioRecord.getMinBufferSize(Const.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        Log.d("tag",Integer.toString(recBufferSize) + " " + Integer.toString(Const.SAMPLES));

        assert (recBufferSize > Const.SAMPLES);

        mAudioInput = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                Const.SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                recBufferSize  // most likely 1600
        );

        mAudioReader = new AudioReaderThread(mAudioInput, mAnalyzer);
        mAudioReader.start();
        mAudioInput.startRecording();
    }

    public void stop() {
        if (mAudioReader != null && mAudioReader.isAlive()) {
            mAudioReader.interrupt();
        }

        if (mAudioInput != null) {
            try {
                mAudioInput.stop();
            } catch (IllegalStateException e) {
                // intentionally empty
            }
        }
        mAudioReader = null;
        Log.i(LOG_TAG, "SoundReceiver was stopped.");
    }

    public void destroy() {
        stop();
        if (mAudioInput != null) {
            try {
                mAudioInput.stop();
            } catch (IllegalStateException e) {
                // intentionally empty
            }
            mAudioInput.release();
            Log.i(LOG_TAG, "AudioRecord was release.");
        }
    }



}

class AudioReaderThread extends Thread {
    final AudioRecord mAudioInput;
    final Analyzer mAnalyzer;


    /**
     * @param audioInput <code>AudioRecord</code> needs to be configured and ready to go.
     * @param analyzer   <code>Analyzer</code> decoder instance
     */
    AudioReaderThread(final AudioRecord audioInput, final Analyzer analyzer) {
        super(AudioReaderThread.class.getSimpleName());
        this.setDaemon(true);
        this.mAudioInput = audioInput;
        this.mAnalyzer = analyzer;
    }

    @Override
    public void run() {
        final short[] buffer = new short[Const.SAMPLES];
        while (!Thread.interrupted()) {
            int offset = 0;
            while (offset < Const.SAMPLES && !Thread.interrupted()) {
                offset += mAudioInput.read(buffer, offset, Const.SAMPLES - offset);
            }
            if (offset == Const.SAMPLES) {
                mAnalyzer.analyze(buffer);
            }
        }
    }
}

class Analyzer {
    final Decoder mDecoder;

    final double[] xr;
    final double[] xi;
    final double[] magSQR;

    final double amplification = 100.0 / 32768.0; // choose a number as you like
    final FFT fft = new FFT(Const.BITS);

    Analyzer(Decoder decoder) {

        this.mDecoder = decoder;

        xr = new double[Const.SAMPLES];
        xi = new double[Const.SAMPLES];
        magSQR = new double[Const.SAMPLES];
    }

    public void analyze(short[] buffer) {
        assert (Const.SAMPLES <= buffer.length);
        for (int i = 0; i < buffer.length; i++) {
            xr[i] = amplification * buffer[i];
            xi[i] = 0;
        }

        fft.doFFT(xr, xi, true);

        // compute the magnitudes, freq, and max:
        int mi = 0;
        for (int i = 0; i < Const.SAMPLES / 2; i++) {
            double re = xr[i];
            double im = xi[i];
            magSQR[i] = re * re + im * im;
            if (magSQR[mi] < magSQR[i]) {
                mi = i;
            }
        }
        double d0 = 0 < mi ? Math.abs(magSQR[mi] - magSQR[mi - 1]) : 0;
        double d1 = Math.abs(magSQR[mi] - magSQR[mi + 1]);
        final int confidence = 0;
        if (confidence < (d0 + d1)) {
            byte b = mDecoder.put(mi);
            if (b == Decoder.STX) {
                mDecoder.getHandler().sendEmptyMessage(0);
            }
            final double Freq_Res = (double) Const.SAMPLE_RATE / Const.SAMPLES;
            int frq = (int) (Freq_Res * mi + Freq_Res / 2);
            Log.v(SoundReceiver.LOG_TAG, "Bin, Symbol_ID, Freq : " + mi + " ,  " + b + " ,  " + frq);
        }
    }
}