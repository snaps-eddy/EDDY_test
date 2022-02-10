package com.snaps.mobile.utils.pref;

import com.snaps.common.utils.log.Dlog;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class StringCrypto {
    private static final String TAG = StringCrypto.class.getSimpleName();
    private static final String SNAPS_AES_128_SECRET_KEY = "snapssnapssnaps!";

    private static final String SNAPS_AES_SECRET_KEY = "SNAPSAES256KEY!!";

    private static final String ALGO = "AES/CBC/PKCS7Padding";

    public static String convertStrToAES(String... arStr) {
        if (arStr == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (int ii = 0; ii < arStr.length; ii++) {
            if (ii > 0) {
                builder.append("|");
            }
            builder.append(arStr[ii]);
        }

        try {
            return StringCrypto.encrypt(builder.toString());
        } catch (Exception e1) {
            Dlog.e(TAG, e1);
        }

        return "";
    }

    private static String encrypt(String cleartext)
            throws Exception {
        byte[] result = encrypt(cleartext.getBytes());
        return FabricBase64.encodeBytes(result);
    }

    public static String decrypt(String encrypted)
            throws Exception {
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(enc);
        return new String(result);
    }

    private static byte[] encrypt(byte[] clear) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);

        byte[] keyData = SNAPS_AES_SECRET_KEY.getBytes("UTF-8");

        cipher.init(Cipher.ENCRYPT_MODE, getSecreteKey(SNAPS_AES_SECRET_KEY), new IvParameterSpec(keyData));
        return cipher.doFinal(clear);
    }

    private static byte[] decrypt(byte[] encrypted)
            throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);

        byte[] keyData = SNAPS_AES_SECRET_KEY.getBytes("UTF-8");

        cipher.init(Cipher.DECRYPT_MODE, getSecreteKey(SNAPS_AES_SECRET_KEY), new IvParameterSpec(keyData));
        return cipher.doFinal(encrypted);
    }

    private static SecretKey getSecreteKey(String secretKey) throws Exception {
        byte[] keyData = secretKey.getBytes("UTF-8");
        return new SecretKeySpec(keyData, "AES");
    }

    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        }
        return result;
    }

    // 암호화
    public static String encAES128(String str) throws Exception {
        Key keySpec = getAES128Key();
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        String iv = SNAPS_AES_128_SECRET_KEY.substring(0, 16);
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
        c.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
        return new String(Base64.encodeBase64(encrypted));
    }

    // 복호화
    public static String decAES128(String enStr) throws Exception {
        String iv = SNAPS_AES_128_SECRET_KEY.substring(0, 16);
        IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
        Key keySpec = getAES128Key();
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] byteStr = Base64.decodeBase64(enStr.getBytes("UTF-8"));
        return new String(c.doFinal(byteStr), "UTF-8");
    }

    private static Key getAES128Key() throws Exception {
        byte[] keyBytes = new byte[16];
        byte[] b = SNAPS_AES_128_SECRET_KEY.getBytes("UTF-8");

        int len = b.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }

        System.arraycopy(b, 0, keyBytes, 0, len);
        return new SecretKeySpec(keyBytes, "AES");
    }

    // 전혀 Fabric 과 상관 없는 코드에서 암호화 시 Base 64를 fabric 라이브러리에 포함된걸 사용하고 있었다.
	// 필요한 부분만 가져와서 붙혀넣음
	// https://github.com/JackChan1999/letv/blob/master/src/main/java/io/fabric/sdk/android/services/network/HttpRequest.java
	// 안드로이드에서 제공하는 base64 와 큰 차이가 없다면 삭제하고 안드로이드 기본 base64 를 사용하자.
    public static class FabricBase64 {
        private static final byte EQUALS_SIGN = (byte) 61;
        private static final String PREFERRED_ENCODING = "US-ASCII";
        private static final byte[] _STANDARD_ALPHABET = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 43, (byte) 47};

        private FabricBase64() {
        }

        private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset) {
            int i;
            int i2 = 0;
            byte[] ALPHABET = _STANDARD_ALPHABET;
            if (numSigBytes > 0) {
                i = (source[srcOffset] << 24) >>> 8;
            } else {
                i = 0;
            }
            int i3 = (numSigBytes > 1 ? (source[srcOffset + 1] << 24) >>> 16 : 0) | i;
            if (numSigBytes > 2) {
                i2 = (source[srcOffset + 2] << 24) >>> 24;
            }
            int inBuff = i3 | i2;
            switch (numSigBytes) {
                case 1:
                    destination[destOffset] = ALPHABET[inBuff >>> 18];
                    destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 63];
                    destination[destOffset + 2] = EQUALS_SIGN;
                    destination[destOffset + 3] = EQUALS_SIGN;
                    break;
                case 2:
                    destination[destOffset] = ALPHABET[inBuff >>> 18];
                    destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 63];
                    destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 63];
                    destination[destOffset + 3] = EQUALS_SIGN;
                    break;
                case 3:
                    destination[destOffset] = ALPHABET[inBuff >>> 18];
                    destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 63];
                    destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 63];
                    destination[destOffset + 3] = ALPHABET[inBuff & 63];
                    break;
            }
            return destination;
        }

        public static String encode(String string) {
            byte[] bytes;
            try {
                bytes = string.getBytes(PREFERRED_ENCODING);
            } catch (UnsupportedEncodingException e) {
                bytes = string.getBytes();
            }
            return encodeBytes(bytes);
        }

        public static String encodeBytes(byte[] source) {
            return encodeBytes(source, 0, source.length);
        }

        public static String encodeBytes(byte[] source, int off, int len) {
            byte[] encoded = encodeBytesToBytes(source, off, len);
            try {
                return new String(encoded, PREFERRED_ENCODING);
            } catch (UnsupportedEncodingException e) {
                return new String(encoded);
            }
        }

        public static byte[] encodeBytesToBytes(byte[] source, int off, int len) {
            if (source == null) {
                throw new NullPointerException("Cannot serialize a null array.");
            } else if (off < 0) {
                throw new IllegalArgumentException("Cannot have negative offset: " + off);
            } else if (len < 0) {
                throw new IllegalArgumentException("Cannot have length offset: " + len);
            } else if (off + len > source.length) {
                throw new IllegalArgumentException(String.format("Cannot have offset of %d and length of %d with array of length %d", new Object[]{Integer.valueOf(off), Integer.valueOf(len), Integer.valueOf(source.length)}));
            } else {
                int i;
                int i2 = (len / 3) * 4;
                if (len % 3 > 0) {
                    i = 4;
                } else {
                    i = 0;
                }
                byte[] outBuff = new byte[(i2 + i)];
                int d = 0;
                int e = 0;
                int len2 = len - 2;
                while (d < len2) {
                    encode3to4(source, d + off, 3, outBuff, e);
                    d += 3;
                    e += 4;
                }
                if (d < len) {
                    encode3to4(source, d + off, len - d, outBuff, e);
                    e += 4;
                }
                if (e > outBuff.length - 1) {
                    return outBuff;
                }
                byte[] finalOut = new byte[e];
                System.arraycopy(outBuff, 0, finalOut, 0, e);
                return finalOut;
            }
        }
    }
}
