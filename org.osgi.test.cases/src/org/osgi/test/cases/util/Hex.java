package org.osgi.test.cases.util;

public class Hex {

    private static final String hexchars = "0123456789abcdef";

    public static String toHex(byte[] array) {
        StringBuffer strbuf = new StringBuffer();
        for(int i = 0; i < array.length; i++) {
            strbuf.append(hexchars.charAt((array[i] & 0xF0) >>> 4));
            strbuf.append(hexchars.charAt(array[i] & 0x0F));
        }
        return strbuf.toString();
    }
}

