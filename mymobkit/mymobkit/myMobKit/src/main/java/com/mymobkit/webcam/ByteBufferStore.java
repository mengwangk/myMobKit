package com.mymobkit.webcam;

import java.nio.ByteBuffer;

public class ByteBufferStore extends Chain<ByteBuffer, byte[]> {

    private static final int DEFAULT_CHAIN_COUNT = 2;

    private ByteBuffer[] frames;
    private int bufferSize;

	/*public ByteBufferStore(final int chainCount, final int bufferSize) {
        super(chainCount);
		this.bufferSize = bufferSize;
		setup();
	}*/

    public ByteBufferStore(int bufferSize) {
        super(DEFAULT_CHAIN_COUNT);
        if (bufferSize <= 0) {
            bufferSize = 1280 * 720;
        }
        this.bufferSize = bufferSize;
        setup();
    }

    private void setup() {
        frames = new ByteBuffer[this.chainCount];
        for (int i = 0; i < this.chainCount; i++) {
            frames[i] = ByteBuffer.allocate(bufferSize);
        }
    }

    public void assign(final byte[] frame) {
        //if (frame.length != this.bufferSize) {
        //	release();
        //	this.bufferSize = frame.length;
        //	setup();
        //}
        frames[currentPos].clear();
        frames[currentPos].put(frame);
    }

    public ByteBuffer current() {
        if (frames[chainIdx].hasArray()) {
            return frames[chainIdx];
        } else {
            return null;
        }
    }

    public void release() {
        for (int i = 0; i < this.chainCount; i++) {
            frames[i].clear();
        }
    }
}
