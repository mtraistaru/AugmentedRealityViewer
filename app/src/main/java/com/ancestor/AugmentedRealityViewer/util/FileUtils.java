package com.ancestor.AugmentedRealityViewer.util;

import android.graphics.Bitmap;

import java.io.BufferedReader;

public abstract class FileUtils {

    protected String folder = null;

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public abstract BufferedReader getReaderFromName(String name);

    public abstract Bitmap getBitmapFromName(String name);
}