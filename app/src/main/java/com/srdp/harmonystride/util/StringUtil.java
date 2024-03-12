package com.srdp.harmonystride.util;

public class StringUtil {
    public static String removeHtmlTags(String str){
        // Step 1: Remove all HTML tags except <b> and <i>
        str = str.replaceAll("(?i)<(?!/?(b|i)\\b)[^>]+>", "");

        // Step 2: Preserve content inside <b> tags
        str = str.replaceAll("(?i)<b>(.*?)</b>", "$1");

        // Step 3: Preserve content inside <i> tags
        str = str.replaceAll("(?i)<i>(.*?)</i>", "$1");

        // Step 4: Remove all remaining HTML tags
        str = str.replaceAll("<[^>]+>", "");

        // Step 5: Restore preserved content
        //str = str.replaceAll("###B_START###", "<b>").replaceAll("###B_END###", "</b>");
        //str = str.replaceAll("###I_START###", "<i>").replaceAll("###I_END###", "</i>");
        return str;
    }

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

    /**
     * Check if the given string is a 6-12 password.
     *
     * @param string The string to be checked.
     * @return True if the string is an 6-12 password, otherwise false.
     */
    public static boolean isPassword(String string) {
        // Use a regular expression to match the 6-12 password
        String passwordPattern = "^(?=.*[0-9a-zA-Z]).{6,12}$";
        return string.matches(passwordPattern);
    }

}
