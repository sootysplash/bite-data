package me.sootysplash.bite;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

public interface BiteArray extends Iterable<TypeObject>, Nestable {
    default void add(char c) {
        add((byte) c);
    }

    default void add(boolean b) {
        add(TypeObject.of(Type.Boolean, b));
    }

    default void add(Number n) {
        add(TypeObject.of(n));
    }

    default void add(CharSequence cs) {
        add(TypeObject.of(Type.CharSequence, cs));
    }

    default void add(BiteArray array) {
        if (this == array) {
            throw new IllegalArgumentException("Cannot add self to self!");
        }
        add(TypeObject.of(Type.Array, array));
    }

    default void add(BiteMap map) {
        add(TypeObject.of(Type.Nest, map));
    }

    void add(TypeObject typeObject);

    TypeObject get(int index);

    int size();

    boolean contains(TypeObject typeObject);

    TypeObject remove(int index);

    boolean remove(TypeObject typeObject);

    TypeObject set(int index, TypeObject typeObject);

    void addAll(BiteArray biteArray);

    List<TypeObject> asList();

    static BiteArray newInstance() {
        return new BiteArrayImpl();
    }

    static BiteArray fromBytes(byte[] data) {
        byte[] buffer = new byte[data.length];
        System.arraycopy(data, 0, buffer, 0, data.length);
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(buffer));
        BiteArray biteArray = new BiteArrayImpl();
        try {
            while (is.available() > 0) {
                biteArray.add(TypeObject.read(is));
            }
        } catch (IOException ignored) {
        }
        return biteArray;
    }
}
