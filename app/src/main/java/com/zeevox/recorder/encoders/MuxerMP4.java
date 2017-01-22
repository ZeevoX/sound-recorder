package com.zeevox.recorder.encoders;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.media.MediaCodecList.getCodecCount;

@TargetApi(25)
class MuxerMP4 implements Encoder {
    private EncoderInfo info;
    private MediaCodec encoder;
    private MediaMuxer muxer;
    private int audioTrackIndex;
    private long NumSamples;
    private ByteBuffer input;
    private int inputIndex;

    static Map<String, MediaCodecInfo> findEncoder(String mime) {
        Map<String, MediaCodecInfo> map = new HashMap<>();

        mime = mime.toLowerCase();

        int numCodecs = getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {
                continue;
            }

            String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                String t = type.toLowerCase();
                if (t.startsWith(mime)) {
                    map.put(t, codecInfo);
                }
            }
        }
        return map;
    }

    private static String prefered(String pref, Map<String, MediaCodecInfo> map) {
        pref = pref.toLowerCase();
        Iterator i = map.keySet().iterator();
        while (i.hasNext()) {
            String m = (String) i.next();
            m = m.toLowerCase();
            if (m.startsWith(pref))
                return m;
        }
        i = map.keySet().iterator();
        while (true) {
            if (!(i.hasNext())) break;
            return (String) i.next();
        }
        return null;
    }

    static MediaFormat getDefault(String pref, Map<String, MediaCodecInfo> map) {
        String p = prefered(pref, map);
        return map.get(p).getCapabilitiesForType(p).getDefaultFormat();
    }

    void create(EncoderInfo info, MediaFormat format, File out) {
        this.info = info;

        try {
            encoder = MediaCodec.createEncoderByType(format.getString(MediaFormat.KEY_MIME));
            encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            encoder.start();

            muxer = new MediaMuxer(out.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void encode(short[] buf, int len) {
        for (int offset = 0; offset < len; offset++) {
            if (input == null) {
                inputIndex = encoder.dequeueInputBuffer(-1);
                if (inputIndex < 0)
                    throw new RuntimeException("unable to open encoder input buffer");
                input = encoder.getInputBuffer(inputIndex);
                assert input != null;
                input.clear();
            }

            input.putShort(buf[offset]);

            if (!input.hasRemaining()) {
                queue();
            }
        }
    }

    private void queue() {
        if (input == null)
            return;
        encoder.queueInputBuffer(inputIndex, 0, input.position(), getCurrentTimeStamp(), 0);
        NumSamples += input.position() / info.channels;
        input = null;

        while (encode()) {
            // do encode()
        }
    }

    private boolean encode() {
        MediaCodec.BufferInfo outputInfo = new MediaCodec.BufferInfo();
        int outputIndex = encoder.dequeueOutputBuffer(outputInfo, 0);
        if (outputIndex == MediaCodec.INFO_TRY_AGAIN_LATER)
            return false;

        if (outputIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            audioTrackIndex = muxer.addTrack(encoder.getOutputFormat());
            muxer.start();
            return true;
        }

        if (outputIndex >= 0) {
            ByteBuffer output = encoder.getOutputBuffer(outputIndex);
            assert output != null;
            output.position(outputInfo.offset);
            output.limit(outputInfo.offset + outputInfo.size);
            muxer.writeSampleData(audioTrackIndex, output, outputInfo);
            encoder.releaseOutputBuffer(outputIndex, false);
        }

        return true;
    }

    public void close() {
        encoder.release();
        muxer.release();
    }

    private long getCurrentTimeStamp() {
        return NumSamples * 1000 * 1000 / info.sampleRate;
    }

    public void end() {
        if (input != null) {
            queue();
        }
        int inputIndex = encoder.dequeueInputBuffer(-1);
        if (inputIndex >= 0) {
            ByteBuffer input = encoder.getInputBuffer(inputIndex);
            assert input != null;
            input.clear();
            encoder.queueInputBuffer(inputIndex, 0, 0, getCurrentTimeStamp(), MediaCodec.BUFFER_FLAG_END_OF_STREAM);
        }
        while (encode()) {
            // do encode()
        }
        encoder.stop();
        muxer.stop();
    }

    public EncoderInfo getInfo() {
        return info;
    }

}