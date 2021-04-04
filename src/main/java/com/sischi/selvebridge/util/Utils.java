package com.sischi.selvebridge.util;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Utils {

    public static final int POSITION_MIN = 0;
    public static final int POSITION_MAX = 65535;

    public static String escapeString(String text) {
        return text.replace("\\", "\\\\").replace("\t", "\\t").replace("\b", "\\b").replace("\n", "\\n")
                .replace("\r", "\\r").replace("\f", "\\f").replace("\'", "\\'").replace("\"", "\\\"");
    }

    public static String base64ToBinary(String base64) {
        if(base64 == null) return null;
        byte[] bytes = Base64.getDecoder().decode(base64);
        String binary = "";
        for(byte b : bytes) {
            binary += ((binary.length() > 0 ? " " : "") + String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        return binary;
    }

    public static String binaryToBase64(String binary) {
        if(binary == null) throw new IllegalArgumentException("binary string cannot be 'null'!");
        
        int length = binary.length();
        if(length % 8 != 0) throw new IllegalArgumentException("length of the binary string has to be a multiple of 8 but is '"+ length +"'");

        byte[] bytes = new byte[length / 8];
        for(int i = 0; i < bytes.length; i++) {
            String chunk = binary.substring(i * 8, (i + 1) * 8);
            bytes[i] = (byte) Short.parseShort(chunk, 2);
        }

        String base64 = Base64.getEncoder().encodeToString(bytes);
        return base64;
    }

    public static List<Integer> readIdsFromBinaryMask(String binary) {
        Validator.validateBinaryDeviceIdMask(binary);

        List<Integer> ids = new ArrayList<>();
        for(int currentByte = 0; currentByte < 8; currentByte++) {
            for(int bit = 7; bit >= 0; bit--) {
                if(binary.charAt((currentByte * 8) + bit) == '1') {
                    int id = (currentByte * 8) + (7 - bit);
                    ids.add(id);
                }
            }
        }
        return ids;
    }

    public static String writeIdsAsBinaryMask(List<Integer> ids) {
        // initialize mask with '0'
        char[] bits = new char[64];
        for(int i = 0; i < 64; i++) {
            bits[i] = '0';
        }

        // set positions of ids to '1'
        ids.stream()
            .filter(id -> id != null)
            .forEach(id -> {
                int targetByte = id / 8;
                int bit = id % 8;
                int pos = (targetByte * 8) + 7 - bit;
                bits[pos] = '1';
            });

        return new String(bits);
    }


    public static int percentageToPosition(int percentage) {
        return POSITION_MIN + ((POSITION_MAX - POSITION_MIN) * percentage / 100);
    }

    public static int positionToPercentage(int position) {
        return (position - POSITION_MIN) / (POSITION_MAX - POSITION_MIN) * 100;
    }

}
