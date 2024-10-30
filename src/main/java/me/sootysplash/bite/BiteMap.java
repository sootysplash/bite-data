package me.sootysplash.bite;

import java.io.*;
import java.util.Map;
import java.util.Set;

public interface BiteMap extends Nestable {

    default void add(CharSequence key, BiteMap toNest) {
        if (this.equals(toNest)) {
            throw new IllegalArgumentException("Cannot add self to self!");
        }
        add(key, TypeObject.of(Type.Nest, toNest));
    }

    default void add(CharSequence key, BiteArray toNest) {
        add(key, TypeObject.of(Type.Array, toNest));
    }

    default void add(CharSequence key, boolean bl) {
        add(key, TypeObject.of(Type.Boolean, bl));
    }

    default void add(CharSequence key, char c) {
        add(key, (byte) c);
    }

    default void add(CharSequence key, CharSequence cs) {
        add(key, TypeObject.of(Type.CharSequence, cs));
    }

    default void add(CharSequence key, Number n) {
        add(key, TypeObject.of(n));
    }

    void add(CharSequence key, TypeObject object);

    TypeObject get(CharSequence key);

    TypeObject remove(CharSequence key);

    Set<Map.Entry<CharSeq, TypeObject>> entrySet();

    Set<CharSeq> keySet();

    int size();

    boolean has(CharSequence key);

    static BiteMap newInstance() {
        return new BiteMapImpl();
    }

    static BiteMap fromBytes(byte[] data) {
        byte[] buffer = new byte[data.length];
        System.arraycopy(data, 0, buffer, 0, data.length);
        DataInputStream is = new DataInputStream(new ByteArrayInputStream(buffer));
        BiteMap biteMap = new BiteMapImpl();
        try {
            while (is.available() > 0) {
                CharSeq key = CharSeq.fromInputStream(is);
                biteMap.add(key, TypeObject.read(is));
            }
        } catch (IOException ignored) {
        }
        return biteMap;

    }
}
