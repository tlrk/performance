package com.immomo.performance;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by tlrk on 8/23/18.
 */
public class InjectUtils {

    public static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            return null;
        }
    }
}
