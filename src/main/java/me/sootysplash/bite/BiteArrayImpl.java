package me.sootysplash.bite;

import java.io.*;
import java.util.*;

class BiteArrayImpl implements BiteArray {
    private final ArrayList<TypeObject> elements = new ArrayList<>();
    private byte[] cache = null;

    BiteArrayImpl() {
    }

    @Override
    public Iterator<TypeObject> iterator() {
        return elements.iterator();
    }

    public List<TypeObject> asList() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream writeTo = new ByteArrayOutputStream(128);
        DataOutputStream wrappedOut = new DataOutputStream(writeTo);
        for (TypeObject typeObject : elements) {
            try {
                typeObject.write(wrappedOut);
            } catch (IOException ignored) {
//                e.printStackTrace(System.err);
            }
        }
        byte[] toArray = writeTo.toByteArray();
        cache = new byte[toArray.length];
        System.arraycopy(toArray, 0, cache, 0, toArray.length);
        return toArray;
    }

    @Override
    public byte[] getCacheBytes() {
        if (cache == null) {
            getBytes();
        }
        return cache;
    }

    @Override
    public void add(TypeObject typeObject) {
        elements.add(typeObject);
    }

    @Override
    public TypeObject get(int index) {
        return elements.get(index);
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public boolean contains(TypeObject typeObject) {
        return elements.contains(typeObject);
    }

    @Override
    public TypeObject remove(int index) {
        return elements.remove(index);
    }

    @Override
    public boolean remove(TypeObject typeObject) {
        return elements.remove(typeObject);
    }

    @Override
    public TypeObject set(int index, TypeObject typeObject) {
        return elements.set(index, typeObject);
    }

    @Override
    public void addAll(BiteArray biteArray) {
        elements.addAll(((BiteArrayImpl)(biteArray)).elements);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof BiteArrayImpl)) return false;
        BiteArrayImpl array = (BiteArrayImpl) object;
        return Objects.equals(elements, array.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(elements);
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}
