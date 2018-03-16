package doit.study.droid.data.source.local;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class Converters {
    @TypeConverter
    public static List<String> fromString(String value) {
        return value == null ? null : splitItems(value);
    }

    @TypeConverter
    public static String toString(List<String> data) {
        return data == null ? null : listToString(data);
    }

    public static class IdConverter {
        @TypeConverter
        public static List<Integer> stringToIntList(String data) {
            if (data == null) {
                return Collections.emptyList();
            }
            return splitToIntList(data);
        }

        @TypeConverter
        public static String intListToString(List<Integer> ints) {
            return joinIntoString(ints);
        }
    }


    /**
     * Splits a comma separated list of integers to integer list.
     * <p>
     * If an input is malformed, it is omitted from the result.
     *
     * @param input Comma separated list of integers.
     * @return A List containing the integers or null if the input is null.
     */
    @Nullable
    public static List<Integer> splitToIntList(@Nullable String input) {
        if (input == null) {
            return null;
        }
        List<Integer> result = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(input, ",");
        while (tokenizer.hasMoreElements()) {
            final String item = tokenizer.nextToken();
            try {
                result.add(Integer.parseInt(item));
            } catch (NumberFormatException ex) {
                Log.e("ROOM", "Malformed integer list", ex);
            }
        }
        return result;
    }

    /**
     * Joins the given list of integers into a comma separated list.
     *
     * @param input The list of integers.
     * @return Comma separated string composed of integers in the list. If the list is null, return
     * value is null.
     */
    @Nullable
    public static String joinIntoString(@Nullable List<Integer> input) {
        if (input == null) {
            return null;
        }

        final int size = input.size();
        if (size == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(Integer.toString(input.get(i)));
            if (i < size - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }


    public static String listToString(List<String> data) {
        StringBuilder sb = new StringBuilder();
        for (String d : data)
            sb.append(d).append("\n");
        return sb.toString();
    }

    private static List<String> splitItems(String s) {
        if ("".equals(s))
            return new ArrayList<>();
        else
            return Arrays.asList(s.split("\n"));
    }

    @TypeConverter
    public Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public Long dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        } else {
            return date.getTime();
        }
    }
}
