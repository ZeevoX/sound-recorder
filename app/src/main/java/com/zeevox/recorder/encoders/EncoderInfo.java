package com.zeevox.recorder.encoders;

public class EncoderInfo {
    public int channels;
    public int sampleRate;
    int bps;

    public EncoderInfo(int channels, int sampleRate, int bps) {
        this.channels = channels;
        this.sampleRate = sampleRate;
        this.bps = bps;
    }
}
