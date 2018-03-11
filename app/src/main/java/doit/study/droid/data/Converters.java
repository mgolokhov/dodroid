package doit.study.droid.data;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converters {
    @TypeConverter
    public static List<String> fromString(String value) {
        return value == null ? null : splitItems(value);
    }

    @TypeConverter
    public static String toString(List<String> data) {
        return data == null ? null : listToString(data);
    }

    public static String listToString(List<String> data){
        StringBuilder sb = new StringBuilder();
        for(String d: data)
            sb.append(d).append("\n");
        return sb.toString();
    }

    private static List<String> splitItems(String s){
        if ("".equals(s))
            return new ArrayList<>();
        else
            return Arrays.asList(s.split("\n"));
    }
}