package edu.buffalo.cse.ubcollecting.data.models;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Created by aamel786 on 3/31/18.
 */

public class MethodComparator implements Comparator<Method> {

    @Override
    public int compare(Method m1, Method m2) {
        return m1.getName().toLowerCase().compareTo(m2.getName().toLowerCase());
    }

}
