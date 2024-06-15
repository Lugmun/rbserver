package ru.cargaman.rbserver.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiscUtils {
    public static boolean matchString(String task, String searchStr){
        Pattern pattern = Pattern.compile(searchStr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(task);
        return matcher.find();
    }
}
