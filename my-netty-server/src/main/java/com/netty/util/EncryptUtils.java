package com.netty.util;

import com.netty.kyrocodec.KryoSerializer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtils {
    private static String EncryptStr(String strSrc, String encName) {
        MessageDigest md = null;
        String strDes = null;

        byte[] bt = strSrc.getBytes();
        try {
            if (encName == null || encName.equals("")) {
                encName = "MD5";
            }
            md = MessageDigest.getInstance(encName);
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Invalid algorithm.");
            return null;
        }
        return strDes;
    }

    public static String EncryptByMD5(String str) {
        return EncryptStr(str, "MD5");
    }

    public static String EncryptBySHA1(String str) {
        return EncryptStr(str, "SHA-1");
    }

    public static String EncryptBySHA256(String str) {
        return EncryptStr(str, "SHA-256");
    }

    private static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

    public static String encrypt(String str)   {
        String encryptStr = EncryptByMD5(str);
        if(encryptStr!=null ){
            encryptStr = encryptStr + encryptStr.charAt(0)+encryptStr.charAt(2)+encryptStr.charAt(4);
            encryptStr = EncryptByMD5(encryptStr);
        }
        return encryptStr;
    }

    public static String encryptObj(Object o){
        return encrypt(bytes2Hex(KryoSerializer.obj2Bytes(o)));
    }
}
