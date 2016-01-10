package com.ancestor.AugmentedRealityViewer.domain;

import android.opengl.GLUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import edu.dhbw.andar.ARObject;

public class MarkerARObject extends ARObject implements Serializable {

    private Model model;
    private Group[] texturedGroups;
    private Group[] nonTexturedGroups;
    private HashMap<Material, Integer> textureIDs = new HashMap<>();

    public MarkerARObject(Model model) {
        super("model", "barcode.patt", 80.0, new double[]{0, 0});
        this.model = model;
        model.finalize();
        Vector<Group> groups = model.getGroupVector();
        Vector<Group> texturedGroups = new Vector<>();
        Vector<Group> nonTexturedGroups = new Vector<>();
        for (Group group : groups) {
            if (group.isTextured()) {
                texturedGroups.add(group);
            } else {
                nonTexturedGroups.add(group);
            }
        }
        this.texturedGroups = texturedGroups.toArray(new Group[texturedGroups.size()]);
        this.nonTexturedGroups = nonTexturedGroups.toArray(new Group[nonTexturedGroups.size()]);
    }

    @Override
    public void init(GL10 gl) {
        int[] temporaryTextureID = new int[1];
        for (Material material : model.getMaterialHashMap().values()) {
            if (material.hasTexture()) {
                gl.glGenTextures(1, temporaryTextureID, 0);
                gl.glBindTexture(GL10.GL_TEXTURE_2D, temporaryTextureID[0]);
                textureIDs.put(material, temporaryTextureID[0]);
                GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, material.getTexture(), 0);
                material.getTexture().recycle();
                gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
                gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            }
        }
    }

    @Override
    public void draw(GL10 gl) {
        super.draw(gl);
        gl.glScalef(model.scaleRatio, model.scaleRatio, model.scaleRatio);
        gl.glTranslatef(model.translationX, model.translationY, model.translationZ);
        gl.glRotatef(model.rotationX, 1, 0, 0);
        gl.glRotatef(model.rotationY, 0, 1, 0);
        gl.glRotatef(model.rotationZ, 0, 0, 1);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        int counter = nonTexturedGroups.length;
        for (int i = 0; i < counter; i++) {
            Group group = nonTexturedGroups[i];
            Material material = group.getMaterial();
            if (material != null) {
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, material.specularLight);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, material.ambientLight);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, material.diffuseLight);
                gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, material.shininess);
            }
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, group.vertices);
            gl.glNormalPointer(GL10.GL_FLOAT, 0, group.normals);
            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, group.vertexCount);
        }
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        counter = texturedGroups.length;
        for (int i = 0; i < counter; i++) {
            Group group = texturedGroups[i];
            Material material = group.getMaterial();
            if (material != null) {
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, material.specularLight);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, material.ambientLight);
                gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, material.diffuseLight);
                gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, material.shininess);
                if (material.hasTexture()) {
                    gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, group.textureCoordinates);
                    gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs.get(material));
                }
            }
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, group.vertices);
            gl.glNormalPointer(GL10.GL_FLOAT, 0, group.normals);
            gl.glDrawArrays(GL10.GL_TRIANGLES, 0, group.vertexCount);
        }
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }
}
