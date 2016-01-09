package com.ancestor.AugmentedRealityViewer.domain;

import com.ancestor.AugmentedRealityViewer.util.BufferUtils;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Group implements Serializable {

    public transient FloatBuffer vertices = null;
    public transient FloatBuffer textureCoordinates = null;
    public transient FloatBuffer normals = null;
    public int vertexCount = 0;
    public ArrayList<Float> groupVertices = new ArrayList<>(500);
    public ArrayList<Float> groupNormals = new ArrayList<>(500);
    public ArrayList<Float> groupTextureCoordinates = new ArrayList<>();
    private String materialName = "default";
    private transient Material material;
    private boolean textured = false;

    public Group() {
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String currMat) {
        this.materialName = currMat;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        if (textureCoordinates != null && material != null && material.hasTexture()) {
            textured = true;
        }
        if (material != null)
            this.material = material;
    }

    public boolean containsVertices() {
        if (groupVertices != null)
            return groupVertices.size() > 0;
        else
            return vertices != null && vertices.capacity() > 0;
    }

    public boolean isTextured() {
        return textured;
    }

    public void setTextured(boolean b) {
        textured = b;
    }

    public void finalize() {
        if (groupTextureCoordinates.size() > 0) {
            textured = true;
            textureCoordinates = BufferUtils.makeFloatBuffer(groupTextureCoordinates.size());
            for (Float groupTextureCoordinate : groupTextureCoordinates) {
                textureCoordinates.put(groupTextureCoordinate);
            }
            textureCoordinates.position(0);
            textured = material != null && material.hasTexture();
        }
        groupTextureCoordinates = null;
        vertices = BufferUtils.makeFloatBuffer(groupVertices.size());
        vertexCount = groupVertices.size() / 3;
        for (Float curVal : groupVertices) {
            vertices.put(curVal);
        }
        groupVertices = null;
        normals = BufferUtils.makeFloatBuffer(groupNormals.size());
        for (Float curVal : groupNormals) {
            normals.put(curVal);
        }
        groupNormals = null;
        vertices.position(0);
        normals.position(0);
    }
}
