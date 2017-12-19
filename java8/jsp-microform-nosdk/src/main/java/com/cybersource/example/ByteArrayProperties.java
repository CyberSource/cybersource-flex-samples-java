/**
 * Copyright (c) 2017 by CyberSource
 * Governing licence: https://github.com/CyberSource/cybersource-flex-samples/blob/master/LICENSE.md
 */
package com.cybersource.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

public class ByteArrayProperties extends HashMap<String, byte[]> {

    public ByteArrayProperties(InputStream inputStream) throws IOException {
        Reader reader = null;
        try {
            reader = new InputStreamReader(inputStream);

            StringBuilder key = new StringBuilder();
            StringBuilder value = new StringBuilder();
            boolean keyRead = false;

            boolean eof;
            int r;
            while (true) {
                eof = (r = reader.read()) < 0;
                char ch = eof ? '\0' : (char) r;
                if (ch == '=') {
                    if (!keyRead) {
                        keyRead = true;
                    } else {
                        value.append(ch);
                    }
                } else if (ch == '\r') {
                    continue;
                } else if (ch == '\n' || eof) {
                    final int valueLength = value.length();
                    byte[] valueChars = new byte[valueLength];
                    for (int i=0; i<valueLength; i++) {
                        valueChars[i] = (byte) value.charAt(i);
                    }
                    put(key.toString(), valueChars);

                    key.setLength(0);
                    value.setLength(0);
                    keyRead = false;

                    if (eof) {
                        break;
                    }
                } else if (keyRead) {
                    value.append(ch);
                } else {
                    key.append(ch);
                }
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
