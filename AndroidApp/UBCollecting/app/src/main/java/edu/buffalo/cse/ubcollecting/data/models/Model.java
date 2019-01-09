package edu.buffalo.cse.ubcollecting.data.models;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstraction of an SQLite database entry.
 */
public abstract class Model implements Serializable {

    public String id;

    public Model() {
        id = generateID();
    }

    /**
     * Returns an ID unique to the Model instance.
     *
     * @return Unique ID
     */
    public String getId() {
        return id;
    }

    /**
     * Set model's ID.
     *
     * @param id String to set ID to.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Return human-readable model identifier.
     *
     * @return human-readable identification
     */
    public abstract String getIdentifier();

    /**
     * Return a {@link List} of the Model instance's get {@link Method}s.
     *
     * @return {@link List} of get methods
     */
    public List<Method> getGetters() {

        Method[] m = this.getClass().getDeclaredMethods();
        ArrayList<Method> getters = new ArrayList<>();

        for (int i = 0; i < m.length; i++) {
            if (m[i].getName().startsWith("get") && !m[i].getName().equals("getIdentifier")) {
                getters.add(m[i]);
            }
        }

        m = this.getClass().getMethods();

        for (int i = 0; i < m.length; i++) {
            if (m[i].getName().equals("getId")) {
                getters.add(m[i]);
                break;
            }
        }

        return getters;
    }

    /**
     * Return a {@link List} of the Model instance's set {@link Method}s.
     *
     * @return {@link List} of set methods
     */
    public ArrayList<Method> getSetters() {

        Method[] m = this.getClass().getDeclaredMethods();
        ArrayList<Method> setters = new ArrayList<>();

        for (int i = 0; i < m.length; i++) {
            if (m[i].getName().startsWith("set")) {
                setters.add(m[i]);
            }
        }

        m = this.getClass().getMethods();

        for (int i = 0; i < m.length; i++) {
            if (m[i].getName().equals("setId")) {
                setters.add(m[i]);
                break;
            }
        }

        return setters;
    }

    /**
     * Return the String-value of the current timestamp in milliseconds to be used as the
     * {@link Model} instance's ID
     *
     * @return {@link String} ID
     */
    private String generateID() {
        String s = "";
        s = s + "0";
        while (s.length() < 5) {
            Random r = new Random();
            s = Integer.toString(r.nextInt(10)) + s;
        }
        return "1" + s + String.valueOf(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return getIdentifier();
    }

}
