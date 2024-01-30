package com.srdp.harmonystride.util;

public class StringUtil {
    /**
     * Check if the given string is an 11-digit phone number.
     *
     * @param string The string to be checked.
     * @return True if the string is null or length <= 0, otherwise false.
     */
    public static boolean isEmpty(String string){
        if(string == null || string.length()<=0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Check if the given string is an 11-digit phone number.
     *
     * @param string The string to be checked.
     * @return True if the string is an 11-digit phone number, otherwise false.
     */
    public static boolean isPhone(String string) {
        // Use a regular expression to match the 11-digit phone number pattern
        String phonePattern = "^[1-9]\\d{10}$";
        return string.matches(phonePattern);
    }

}
