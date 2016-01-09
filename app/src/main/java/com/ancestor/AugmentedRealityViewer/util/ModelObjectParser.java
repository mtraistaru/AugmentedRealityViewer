package com.ancestor.AugmentedRealityViewer.util;

import com.ancestor.AugmentedRealityViewer.domain.Group;
import com.ancestor.AugmentedRealityViewer.domain.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class ModelObjectParser {

    private final int VERTEX_DIMENSIONS = 3;
    private final int TEXTURE_COORD_DIMENSIONS = 2;

    private FileUtils fileUtils;

    public ModelObjectParser(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    public Model parseModel(String modelName, BufferedReader is) throws IOException, ParseException {

        ArrayList<float[]> vertices = new ArrayList<>(1000);
        ArrayList<float[]> normals = new ArrayList<>(1000);
        ArrayList<float[]> texcoords = new ArrayList<>();

        Model model = new Model();
        Group curGroup = new Group();
        MaterialLibraryFileParser materialLibraryFileParser = new MaterialLibraryFileParser(fileUtils);
        SimpleTokenizer spaceTokenizer = new SimpleTokenizer();
        SimpleTokenizer slashTokenizer = new SimpleTokenizer();
        slashTokenizer.setDelimiter("/");

        String line;
        int lineNum = 1;
        for (line = is.readLine(); line != null; line = is.readLine(), lineNum++) {
            if (line.length() > 0) {
                if (line.startsWith("#")) {
                    //ignore comments
                } else if (line.startsWith("v ")) {
                    //add new vertex to vector
                    String endOfLine = line.substring(2);
                    spaceTokenizer.setString(endOfLine);
                    vertices.add(new float[]{
                            Float.parseFloat(spaceTokenizer.next()),
                            Float.parseFloat(spaceTokenizer.next()),
                            Float.parseFloat(spaceTokenizer.next())});
                } else if (line.startsWith("vt ")) {
                    //add new texture vertex to vector
                    String endOfLine = line.substring(3);
                    spaceTokenizer.setString(endOfLine);
                    texcoords.add(new float[]{
                            Float.parseFloat(spaceTokenizer.next()),
                            Float.parseFloat(spaceTokenizer.next())});
                } else if (line.startsWith("f ")) {
                    //add face to group
                    String endOfLine = line.substring(2);
                    spaceTokenizer.setString(endOfLine);
                    int faces = spaceTokenizer.delimOccurCount() + 1;
                    if (faces != 3) {
                        throw new ParseException(modelName,
                                lineNum, "only triangle faces are supported");
                    }
                    for (int i = 0; i < 3; i++) {//only triangles supported
                        String face = spaceTokenizer.next();
                        slashTokenizer.setString(face);
                        int vertexCount = slashTokenizer.delimOccurCount() + 1;
                        int vertexID = 0;
                        int textureID = -1;
                        int normalID = 0;
                        if (vertexCount == 2) {
                            vertexID = Integer.parseInt(slashTokenizer.next()) - 1;
                            normalID = Integer.parseInt(slashTokenizer.next()) - 1;
                            throw new ParseException(modelName, lineNum, "vertex normal needed.");
                        } else if (vertexCount == 3) {
                            //vertex reference
                            vertexID = Integer.parseInt(slashTokenizer.next()) - 1;
                            String texCoord = slashTokenizer.next();
                            if (!texCoord.equals("")) {
                                textureID = Integer.parseInt(texCoord) - 1;
                            }
                            normalID = Integer.parseInt(slashTokenizer.next()) - 1;
                        } else {
                            throw new ParseException(modelName, lineNum, "a faces needs reference a vertex, a normal vertex and optionally a texture coordinate per vertex.");
                        }
                        float[] vec;
                        try {
                            vec = vertices.get(vertexID);
                        } catch (IndexOutOfBoundsException ex) {
                            throw new ParseException(modelName, lineNum, "non existing vertex referenced.");
                        }
                        if (vec == null)
                            throw new ParseException(modelName, lineNum, "non existing vertex referenced.");
                        for (int j = 0; j < VERTEX_DIMENSIONS; j++)
                            curGroup.groupVertices.add(vec[j]);
                        if (textureID != -1) {
                            try {
                                vec = texcoords.get(textureID);
                            } catch (IndexOutOfBoundsException ex) {
                                throw new ParseException(modelName, lineNum, "non existing texture coord referenced.");
                            }
                            if (vec == null)
                                throw new ParseException(modelName, lineNum, "non existing texture coordinate referenced.");
                            for (int j = 0; j < TEXTURE_COORD_DIMENSIONS; j++)
                                curGroup.groupTextureCoordinates.add(vec[j]);
                        }
                        try {
                            vec = normals.get(normalID);
                        } catch (IndexOutOfBoundsException ex) {
                            throw new ParseException(modelName, lineNum, "non existing normal vertex referenced.");
                        }
                        if (vec == null)
                            throw new ParseException(modelName, lineNum, "non existing normal vertex referenced.");
                        for (int j = 0; j < VERTEX_DIMENSIONS; j++)
                            curGroup.groupNormals.add(vec[j]);
                    }
                } else if (line.startsWith("vn ")) {
                    String endOfLine = line.substring(3);
                    spaceTokenizer.setString(endOfLine);
                    normals.add(new float[]{
                            Float.parseFloat(spaceTokenizer.next()),
                            Float.parseFloat(spaceTokenizer.next()),
                            Float.parseFloat(spaceTokenizer.next())});
                } else if (line.startsWith("mtllib ")) {
                    String filename = line.substring(7);
                    String[] files = StringUtils.splitBySpace(filename);
                    for (String file : files) {
                        BufferedReader mtlFile = fileUtils.getReaderFromName(file);
                        if (mtlFile != null)
                            materialLibraryFileParser.parse(model, mtlFile);
                    }
                } else if (line.startsWith("usemtl ")) {
                    if (curGroup.groupVertices.size() > 0) {
                        model.addGroup(curGroup);
                        curGroup = new Group();
                    }
                    curGroup.setMaterialName(line.substring(7));
                } else if (line.startsWith("g ")) {
                    if (curGroup.groupVertices.size() > 0) {
                        model.addGroup(curGroup);
                        curGroup = new Group();
                    }
                }
            }
        }
        if (curGroup.groupVertices.size() > 0) {
            model.addGroup(curGroup);
        }
        for (Group group : model.getGroupVector()) {
            group.setMaterial(model.getMaterial(group.getMaterialName()));
        }
        return model;
    }
}