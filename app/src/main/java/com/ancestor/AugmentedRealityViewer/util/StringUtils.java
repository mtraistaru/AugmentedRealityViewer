package com.ancestor.AugmentedRealityViewer.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern trimWhiteSpaces = Pattern.compile("[\\s]+");
    private static final Pattern removeInlineComments = Pattern.compile("#");
    private static final Pattern splitBySpace = Pattern.compile(" ");

    public static String getCanonicalLine(String line) {
        line = trimWhiteSpaces.matcher(line).replaceAll(" ");
        if (line.contains("#")) {
            String[] parts = removeInlineComments.split(line);
            if (parts.length > 0)
                line = parts[0];
        }
        return line;
    }

    public static String[] splitBySpace(String str) {
        return splitBySpace.split(str);
    }

    public static void trim(BufferedReader in, BufferedWriter out) throws IOException {
        String line;
        out.write("#trimmed\n");
        for (line = in.readLine();
             line != null;
             line = in.readLine()) {
            line = getCanonicalLine(line);
            if (line.length() > 0) {
                out.write(line.trim());
                out.write('\n');
            }
        }
        in.close();
        out.close();
    }
}
