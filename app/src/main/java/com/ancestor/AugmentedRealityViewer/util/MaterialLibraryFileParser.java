package com.ancestor.AugmentedRealityViewer.util;

import com.ancestor.AugmentedRealityViewer.domain.Material;
import com.ancestor.AugmentedRealityViewer.domain.Model;

import java.io.BufferedReader;
import java.io.IOException;

public class MaterialLibraryFileParser {

    private FileUtils fileUtils;

    public MaterialLibraryFileParser(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    private static float[] parseTriple(String str) {
        String[] colorValues = str.split(" ");
        return new float[]{Float.parseFloat(colorValues[0]), Float.parseFloat(colorValues[1]), Float.parseFloat(colorValues[2])};
    }

    public void parse(Model model, BufferedReader is) {
        Material material = null;
        int lineNumber = 1;
        String currentLine;
        try {
            for (currentLine = is.readLine(); currentLine != null; currentLine = is.readLine(), lineNumber++) {
                currentLine = StringUtils.getCanonicalLine(currentLine).trim();
                if (currentLine.length() > 0) {
                    if (currentLine.startsWith("newmtl ")) {
                        String materialName = currentLine.substring(7);
                        material = new Material(materialName);
                        model.addMaterial(material);
                    } else if (material == null) {
                        //if the current material is not set, there is no need to parse anything
                    } else if (currentLine.startsWith("# ")) {
                        //ignore comments
                    } else if (currentLine.startsWith("Ka ")) {
                        String endOfLine = currentLine.substring(3);
                        material.setAmbient(parseTriple(endOfLine));
                    } else if (currentLine.startsWith("Kd ")) {
                        String endOfLine = currentLine.substring(3);
                        material.setDiffuse(parseTriple(endOfLine));
                    } else if (currentLine.startsWith("Ks ")) {
                        String endOfLine = currentLine.substring(3);
                        material.setSpecular(parseTriple(endOfLine));
                    } else if (currentLine.startsWith("Ns ")) {
                        String endOfLine = currentLine.substring(3);
                        material.setShininess(Float.parseFloat(endOfLine));
                    } else if (currentLine.startsWith("Tr ")) {
                        String endOfLine = currentLine.substring(3);
                        material.setAlpha(Float.parseFloat(endOfLine));
                    } else if (currentLine.startsWith("d ")) {
                        String endOfLine = currentLine.substring(2);
                        material.setAlpha(Float.parseFloat(endOfLine));
                    } else if (currentLine.startsWith("map_Kd ")) {
                        String imageFileName = currentLine.substring(7);
                        material.setFileUtils(fileUtils);
                        material.setBitmapFileName(imageFileName);
                    } else if (currentLine.startsWith("mapKd ")) {
                        String imageFileName = currentLine.substring(6);
                        material.setFileUtils(fileUtils);
                        material.setBitmapFileName(imageFileName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
