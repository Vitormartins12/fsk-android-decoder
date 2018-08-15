package com.example.vitor.appdecoder.sound;

public interface Const {
    int SAMPLE_RATE = 44100;    // in Hz
    int SAMPLES = 512;          // number of samples
    int BITS = 9;               // a power of two such that 2^b is the number_of_samples
    int REPEAT = 3;             // repeat each signal
    int OFFSET = 60;

}
