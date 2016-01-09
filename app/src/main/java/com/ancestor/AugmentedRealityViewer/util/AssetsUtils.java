package com.ancestor.AugmentedRealityViewer.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetsUtils extends FileUtils {

    private AssetManager assetManager;

    public AssetsUtils(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    public Bitmap getBitmapFromName(String name) {
        InputStream inputStream = getInputStreamFromName(name);
        return (inputStream == null) ? null : BitmapFactory.decodeStream(inputStream);
    }

    @Override
    public BufferedReader getReaderFromName(String name) {
        InputStream inputStream = getInputStreamFromName(name);
        return (inputStream == null) ? null : new BufferedReader(new InputStreamReader(inputStream));
    }

    private InputStream getInputStreamFromName(String name) {
        InputStream inputStream;
        if (folder != null) {
            try {
                inputStream = assetManager.open(folder + name);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                inputStream = assetManager.open(name);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return inputStream;
    }
}
