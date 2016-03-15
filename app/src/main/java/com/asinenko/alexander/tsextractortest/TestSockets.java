package com.asinenko.alexander.tsextractortest;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Created by alexander on 15.03.16.
 */
public class TestSockets {

    public static String TAG = "TestSockets";

    /** A LocalSocket will be used to feed the MediaRecorder object */
    public static final byte PIPE_API_LS = 0x01;
    /** A ParcelFileDescriptor will be used to feed the MediaRecorder object */
    public static final byte PIPE_API_PFD = 0x02;

    public OutputStream mOutputStream = null;
    public InetAddress mDestination;
    public LocalSocket mReceiver, mSender = null;
    private LocalServerSocket mLss = null;
    private int mSocketId, mTTL = 64;

    public ParcelFileDescriptor mParcelRead;
    public ParcelFileDescriptor mParcelWrite;
    public ParcelFileDescriptor[] mParcelFileDescriptors;

    public final static byte sPipeApi = PIPE_API_LS;

    public FileDescriptor fileDescriptor = null;
    public OutputStream out = null;

    public TestSockets(){
        try {
            createSockets();
            if (sPipeApi == PIPE_API_LS) {
                out = mSender.getOutputStream();
                fileDescriptor = mReceiver.getFileDescriptor();
            }else{

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeSockets();
        }

    }

    public void mediaExtractor() throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(fileDescriptor);
        int numTracks = extractor.getTrackCount();

        Log.d(TAG, "Track count: " + numTracks );

        for (int i = 0; i < numTracks; ++i) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            Log.d(TAG, "mime: " + mime);
        }

        extractor.selectTrack(0);


        ByteBuffer inputBuffer = ByteBuffer.allocate(1500);
        while (extractor.readSampleData(inputBuffer, 0) >= 0) {
            int trackIndex = extractor.getSampleTrackIndex();
            long presentationTimeUs = extractor.getSampleTime();
            extractor.advance();
        }
        extractor.release();
        extractor = null;
    }

    public void createSockets() throws IOException {
        if (sPipeApi == PIPE_API_LS) {
            final String LOCAL_ADDR = "com.asinenko-";
            for (int i=0;i<10;i++) {
                try {
                    mSocketId = new Random().nextInt();
                    mLss = new LocalServerSocket(LOCAL_ADDR+mSocketId);
                    break;
                } catch (IOException e1) {}
            }
            mReceiver = new LocalSocket();
            mReceiver.connect( new LocalSocketAddress(LOCAL_ADDR+mSocketId));
            mReceiver.setReceiveBufferSize(500000);
            mReceiver.setSoTimeout(3000);
            mSender = mLss.accept();
            mSender.setSendBufferSize(500000);
        } else {
            Log.e(TAG, "parcelFileDescriptors createPipe version = Lollipop");
            mParcelFileDescriptors = ParcelFileDescriptor.createPipe();
            mParcelRead = new ParcelFileDescriptor(mParcelFileDescriptors[0]);
            mParcelWrite = new ParcelFileDescriptor(mParcelFileDescriptors[1]);
        }
    }

    public void closeSockets() {
        if (sPipeApi == PIPE_API_LS) {
            try {
                if(null != mReceiver)
                    mReceiver.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if(null != mSender)
                    mSender.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if(null != mLss)
                    mLss.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mLss = null;
            mSender = null;
            mReceiver = null;
        } else {
            try {
                if (mParcelRead != null) {
                    mParcelRead.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (mParcelWrite != null) {
                    mParcelWrite.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
