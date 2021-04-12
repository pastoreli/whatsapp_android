package com.pastoreli.whatsapp.helpers;

import android.util.Base64;

public class CustomBase64 {

    public static String Base64Encode (String text) {
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static String Base64Decode (String codeText) {
        return new String( Base64.decode(codeText, Base64.DEFAULT) );
    }

}
