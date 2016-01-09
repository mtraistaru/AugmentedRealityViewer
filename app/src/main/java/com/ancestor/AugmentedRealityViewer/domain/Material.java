package com.ancestor.AugmentedRealityViewer.domain;

import android.graphics.Bitmap;

import com.ancestor.AugmentedRealityViewer.util.BufferUtils;
import com.ancestor.AugmentedRealityViewer.util.FileUtils;

import java.io.Serializable;
import java.nio.FloatBuffer;

public class Material implements Serializable {

    public static final int DYNAMIC = 0;
    public static final int FINALIZED = 1;
    public transient FloatBuffer ambientLight = BufferUtils.makeFloatBuffer(4);
    public transient FloatBuffer diffuseLight = BufferUtils.makeFloatBuffer(4);
    public transient FloatBuffer specularLight = BufferUtils.makeFloatBuffer(4);
    public float shininess = 0;
    public int STATE = DYNAMIC;
    private float[] ambientLightArray = {0.2f, 0.2f, 0.2f, 1.0f};
    private float[] diffuseLightArray = {0.8f, 0.8f, 0.8f, 1.0f};
    private float[] specularLightArray = {0.0f, 0.0f, 0.0f, 1.0f};
    private transient Bitmap texture = null;
    private String bitmapFileName = null;
    private transient FileUtils fileUtils = null;

    private String name = "defaultMaterial";

    public Material() {

    }

    public Material(String name) {
        this.name = name;
        ambientLight.put(new float[]{0.2f, 0.2f, 0.2f, 1.0f});
        ambientLight.position(0);
        diffuseLight.put(new float[]{0.8f, 0.8f, 0.8f, 1.0f});
        diffuseLight.position(0);
        specularLight.put(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
        specularLight.position(0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFileUtils(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    public String getBitmapFileName() {
        return bitmapFileName;
    }

    public void setBitmapFileName(String bitmapFileName) {
        this.bitmapFileName = bitmapFileName;
    }

    public void setAmbient(float[] arr) {
        ambientLightArray = arr;
    }

    public void setDiffuse(float[] arr) {
        diffuseLightArray = arr;
    }

    public void setSpecular(float[] arr) {
        specularLightArray = arr;
    }

    public void setShininess(float ns) {
        shininess = ns;
    }

    public void setAlpha(float alpha) {
        ambientLight.put(3, alpha);
        diffuseLight.put(3, alpha);
        specularLight.put(3, alpha);
    }

    public Bitmap getTexture() {
        return texture;
    }

    public void setTexture(Bitmap texture) {
        this.texture = texture;
    }

    public boolean hasTexture() {
        if (STATE == DYNAMIC)
            return this.bitmapFileName != null;
        else
            return STATE == FINALIZED && this.texture != null;
    }

    public void finalize() {
        ambientLight = BufferUtils.makeFloatBuffer(ambientLightArray);
        diffuseLight = BufferUtils.makeFloatBuffer(diffuseLightArray);
        specularLight = BufferUtils.makeFloatBuffer(specularLightArray);
        ambientLightArray = null;
        diffuseLightArray = null;
        specularLightArray = null;
        if (fileUtils != null && bitmapFileName != null) {
            texture = fileUtils.getBitmapFromName(bitmapFileName);
        }
    }
}
