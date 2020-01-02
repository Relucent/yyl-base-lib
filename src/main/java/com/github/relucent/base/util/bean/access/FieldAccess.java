package com.github.relucent.base.util.bean.access;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

public class FieldAccess {

    // ==============================Fields===========================================
    private final String[] fieldNames;
    private final Class<?>[] fieldTypes;
    private final Field[] fields;
    private final int fieldCount;

    // ==============================Constructor======================================
    protected FieldAccess(Class<?> type) {
        ArrayList<Field> fields = new ArrayList<Field>();
        Class<?> nextClass = type;
        while (nextClass != Object.class) {
            Field[] declaredFields = nextClass.getDeclaredFields();
            for (int i = 0, n = declaredFields.length; i < n; i++) {
                Field field = declaredFields[i];
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers)) {
                    continue;
                }
                if (Modifier.isPrivate(modifiers)) {
                    continue;
                }
                fields.add(field);
            }
            nextClass = nextClass.getSuperclass();
        }
        int fieldCount = fields.size();
        String[] fieldNames = new String[fieldCount];
        Class<?>[] fieldTypes = new Class[fieldCount];
        for (int i = 0, n = fieldNames.length; i < n; i++) {
            fieldNames[i] = fields.get(i).getName();
            fieldTypes[i] = fields.get(i).getType();
        }
        this.fieldNames = fieldNames;
        this.fieldTypes = fieldTypes;
        this.fields = fields.toArray(new Field[fieldCount]);
        this.fieldCount = fieldCount;
    }

    public static FieldAccess create(Class<?> type) {
        return new FieldAccess(type);
    }

    // ==============================Methods==========================================
    public int getFieldCount() {
        return fieldCount;
    }

    public String[] getFieldNames() {
        return Arrays.copyOf(fieldNames, fieldCount);
    }

    public Class<?>[] getFieldTypes() {
        return Arrays.copyOf(fieldTypes, fieldCount);
    }

    public Field[] getFields() {
        return Arrays.copyOf(fields, fieldCount);
    }

    public void set(Object instance, String fieldName, Object value) {
        int index = getIndex(fieldName);
        try {
            fields[index].set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("field set error: " + fieldName, e);
        }
    }

    public Object get(Object instance, String fieldName) {
        int index = getIndex(fieldName);
        try {
            return fields[index].get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("field get error: " + fieldName, e);
        }
    }

    private int getIndex(String fieldName) {
        for (int i = 0, n = fieldNames.length; i < n; i++) {
            if (fieldNames[i].equals(fieldName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unable to find non-private field: " + fieldName);
    }
}
