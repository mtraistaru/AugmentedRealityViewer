package com.ancestor.AugmentedRealityViewer.domain;

import com.ancestor.AugmentedRealityViewer.util.FileUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

public class Model implements Serializable {

    public static final int DYNAMIC = 0;
    public static final int FINALIZED = 1;
    public float rotationX = 90;
    public float rotationY = 0;
    public float rotationZ = 0;
    public float translationX = 0;
    public float translationY = 0;
    public float translationZ = 0;
    public float scaleRatio = 4f;
    public int STATE = DYNAMIC;
    protected HashMap<String, Material> materialHashMap = new HashMap<>();
    private Vector<Group> groupVector = new Vector<>();

    public Model() {
        materialHashMap.put("default", new Material("default"));
    }

    public void addMaterial(Material mat) {
        materialHashMap.put(mat.getName(), mat);
    }

    public Material getMaterial(String name) {
        return materialHashMap.get(name);
    }

    public void addGroup(Group group) {
        if (STATE == FINALIZED) group.finalize();
        groupVector.add(group);
    }

    public Vector<Group> getGroupVector() {
        return groupVector;
    }

    public void setFileUtil(FileUtils fileUtils) {
        for (Material mat : materialHashMap.values()) {
            mat.setFileUtils(fileUtils);
        }
    }

    public HashMap<String, Material> getMaterialHashMap() {
        return materialHashMap;
    }

    public void setScaleRatio(float f) {
        this.scaleRatio += f;
        if (this.scaleRatio < 0.0001f) this.scaleRatio = 0.0001f;
    }

    public void setRotationX(float dY) {
        this.rotationX += dY;
    }

    public void setRotationY(float dX) {
        this.rotationY += dX;
    }

    public void setTranslationX(float f) {
        this.translationX += f;
    }

    public void setTranslationY(float f) {
        this.translationY += f;
    }

    public void finalize() {
        if (STATE != FINALIZED) {
            STATE = FINALIZED;
            for (Group group : groupVector) {
                group.finalize();
                group.setMaterial(materialHashMap.get(group.getMaterialName()));
            }
            for (Material material : materialHashMap.values()) {
                material.finalize();
            }
        }
    }
}
