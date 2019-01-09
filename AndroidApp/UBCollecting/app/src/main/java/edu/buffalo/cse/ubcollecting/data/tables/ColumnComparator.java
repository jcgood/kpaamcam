package edu.buffalo.cse.ubcollecting.data.tables;

import java.util.Comparator;

/**
 * Created by aamel786 on 3/31/18.
 */

public class ColumnComparator implements Comparator<String> {

    @Override
    public int compare(String s1, String s2) {
        return s1.toLowerCase().compareTo(s2.toLowerCase());
    }

}
