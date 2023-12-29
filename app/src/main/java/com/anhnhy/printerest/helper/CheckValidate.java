package com.anhnhy.printerest.helper;

public class CheckValidate {

    private static CheckValidate checkValidate = new CheckValidate();

    private CheckValidate() {

    }

    public static boolean isNone(String str) {
        if (str.isEmpty() || str == null) {
            return true;
        }
        return false;
    }
}
