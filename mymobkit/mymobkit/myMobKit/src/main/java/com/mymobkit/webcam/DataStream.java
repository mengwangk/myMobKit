package com.mymobkit.webcam;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;

public class DataStream {
	private static final String TAG = makeLogTag(DataStream.class);

	private static final int MAX_RETRY_COUNT = 40;

	private LocalSocket receiver, sender;
	private LocalServerSocket lss;
	private String localAddress;
	private boolean isConnected = false;
	private boolean inStreamingMode = false;

	private int retryCount;

	public DataStream(final String addr) {
		this.retryCount = 0;
		this.localAddress = addr;
		try {
			lss = new LocalServerSocket(localAddress);
		} catch (IOException e) {
			LOGE(TAG, "[DataStream] Error creating server socket", e);
		}
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public InputStream getInputStream() throws IOException {
		return receiver.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		if (sender == null)
			return null;
		return sender.getOutputStream();
	}

	public FileDescriptor getOutputStreamDescriptor() {
		return sender.getFileDescriptor();
	}

	public LocalSocket getSender() {
		return sender;
	}

	public LocalSocket getReceiver() {
		return receiver;
	}

	public void release() {
		try {
			if (receiver != null) {
				receiver.close();
			}
			if (sender != null) {
				sender.close();
			}
		} catch (IOException ioEx) {
			LOGW(TAG, "[release] Error releasing receiver", ioEx);
		}

		sender = null;
		receiver = null;
		isConnected = false;
	}

	public boolean prepare(int recvBufferSize, int sendBufferSize) {
		receiver = new LocalSocket();
		try {
			receiver.connect(new LocalSocketAddress(localAddress));
			receiver.setReceiveBufferSize(recvBufferSize);
			sender = lss.accept();
			sender.setSoTimeout(15000); // To detected disconnect socket - Important
			sender.setSendBufferSize(sendBufferSize);
		} catch (IOException e) {
			LOGE(TAG, "[prepare] Error initialising receiver and sender", e);
			return false;
		}
		isConnected = true;
		return true;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void failedStreaming() {
		retryCount++;
	}

	public void succeedStreaming() {
		retryCount = 0;
	}

	public boolean cannotBeReovered() {
		return retryCount > MAX_RETRY_COUNT;
	}

	public void setStreamingMode(boolean mode) {
		this.inStreamingMode = mode;
	}

	public boolean isStreaming() {
		return this.inStreamingMode;
	}

	@Override
	public String toString() {
		return "DataStream [receiver=" + receiver + ", sender=" + sender + ", lss=" + lss + ", localAddress=" + localAddress + ", isConnected=" + isConnected + "]";
	}

}
