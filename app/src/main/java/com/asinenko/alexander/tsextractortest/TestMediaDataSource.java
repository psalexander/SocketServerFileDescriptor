package com.asinenko.alexander.tsextractortest;

import android.annotation.TargetApi;
import android.media.MediaDataSource;
import android.os.Build;

import java.io.IOException;

/**
 * Created by alexander on 15.03.16.
 *
 * https://medium.com/@jacks205/implementing-your-own-android-mediadatasource-e67adb070731#.peixmslnd
 *
 */
@TargetApi(Build.VERSION_CODES.M)
public class TestMediaDataSource extends MediaDataSource {

    private volatile byte[] videoBuffer;
    private volatile boolean isDownloading;


    @Override
    public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
        synchronized (videoBuffer){

            int length = videoBuffer.length;
            if (position >= length) {
                return -1; // -1 indicates EOF
            }

            if (position + size > length) {
                size -= (position + size) - length;
            }

            System.arraycopy(videoBuffer, (int)position, buffer, offset, size);
            return size;
        }
    }

    @Override
    public long getSize() throws IOException {
        synchronized (videoBuffer) {
            return videoBuffer.length;
        }
    }

    @Override
    public void close() throws IOException {

    }
}
