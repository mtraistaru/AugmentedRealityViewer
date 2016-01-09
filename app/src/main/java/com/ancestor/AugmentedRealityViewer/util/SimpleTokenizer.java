package com.ancestor.AugmentedRealityViewer.util;

public class SimpleTokenizer {

    String string = "";
    String delimiter = " ";
    int delimiterLength = delimiter.length();
    int i = 0;
    int j = 0;

    public final void setString(String string) {
        this.string = string;
        i = 0;
        j = string.indexOf(delimiter);
    }

    public final void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
        delimiterLength = delimiter.length();
    }

    public final String next() {
        if (j >= 0) {
            String result = string.substring(i, j);
            i = j + 1;
            j = string.indexOf(delimiter, i);
            return result;
        } else {
            return string.substring(i);
        }
    }

    public final int delimOccurCount() {
        int result = 0;
        if (delimiterLength > 0) {
            int start = string.indexOf(delimiter);
            while (start != -1) {
                result++;
                start = string.indexOf(delimiter, start + delimiterLength);
            }
        }
        return result;
    }
}
