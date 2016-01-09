package com.ancestor.AugmentedRealityViewer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class SDCardFileUtils extends FileUtils {

    public BufferedReader getReaderFromName(String name) {
        if (folder != null) {
            try {
                return new BufferedReader(new FileReader(new File(folder, name)));
            } catch (FileNotFoundException e) {
                return null;
            }
        } else {
            try {
                return new BufferedReader(new FileReader(new File(name)));
            } catch (FileNotFoundException e) {
                return null;
            }
        }
    }

    public Bitmap getBitmapFromName(String name) {
        if (folder != null) {
            String path = new File(folder, name).getAbsolutePath();
            return BitmapFactory.decodeFile(path);
        } else {
            return BitmapFactory.decodeFile(name);
        }
    }
}
