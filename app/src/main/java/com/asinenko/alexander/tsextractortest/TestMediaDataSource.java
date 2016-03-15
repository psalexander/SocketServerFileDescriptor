package com.asinenko.alexander.tsextractortest;

import android.annotation.TargetApi;
import android.media.MediaDataSource;
import android.os.Build;

import java.io.IOException;

/**
 * Created by alexander on 15.03.16.
 */
@TargetApi(Build.VERSION_CODES.M)
public class TestMediaDataSource extends MediaDataSource {
    @Override
    public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
        return 0;
    }

    @Override
    public long getSize() throws IOException {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }
}
