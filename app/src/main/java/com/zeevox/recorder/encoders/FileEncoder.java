package com.zeevox.recorder.encoders;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.File;

class FileEncoder {
    private static final String TAG = FileEncoder.class.getSimpleName();
    private Handler handler;
    private File in;
    private Encoder encoder;
    private Thread thread;
    private long samples;
    private long cur;
    private Throwable t;
    public FileEncoder(Context context, File in, Encoder encoder) {
        this.in = in;
        this.encoder = encoder;
        handler = new Handler();
    }
    public void run(final Runnable progress, final Runnable done, final Runnable error) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                cur = 0;
                RawSamples rs = new RawSamples(in);
                samples = rs.getSamples();
                short[] buf = new short[1000];
                rs.open(buf.length);
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        int len = rs.read(buf);
                        if (len <= 0) {
                            encoder.end();
                            handler.post(done);
                            return;
                        } else {
                            encoder.encode(buf, len);
                            handler.post(progress);
                            synchronized (thread) {
                                cur += len;
                            }
                        }
                    }
                } catch (RuntimeException e) {
                    Log.e(TAG, "Exception", e);
                    t = e;
                    handler.post(error);
                } finally {
                    encoder.close();
                    rs.close();
                }
            }
        });
        thread.start();
    }
    public int getProgress() {
        synchronized (thread) {
            return (int) (cur * 100 / samples);
        }
    }
    public Throwable getException() {
        return t;
    }
    public void close() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }
}
