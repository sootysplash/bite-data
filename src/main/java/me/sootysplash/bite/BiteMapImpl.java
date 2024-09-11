package me.sootysplash.bite;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

class BiteMapImpl implements BiteMap {
    private final LinkedHashMap<CharSeq, TypeObject> members = new LinkedHashMap<>();
    private byte[] cache = null;

    BiteMapImpl() {
    }

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream writeTo = new ByteArrayOutputStream(128);
        DataOutputStream wrappedOut = new DataOutputStream(writeTo);
        for (Map.Entry<CharSeq, TypeObject> keyEntryPair : members.entrySet()) {
            try {
                CharSeq.toOutputStream(keyEntryPair.getKey(), wrappedOut);
                keyEntryPair.getValue().write(wrappedOut);
            } catch (IOException ignored) {
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
    public void add(CharSequence key, TypeObject object) {
        members.put(CharSeq.of(key), object);
    }

    @Override
    public TypeObject get(CharSequence key) {
        return members.get(CharSeq.of(key));
    }

    @Override
    public TypeObject remove(CharSequence key) {
        return members.remove(CharSeq.of(key));
    }

    @Override
    public Set<Map.Entry<CharSeq, TypeObject>> entrySet() {
        return members.entrySet();
    }

    @Override
    public Set<CharSeq> keySet() {
        return members.keySet();
    }

    @Override
    public int size() {
        return members.size();
    }

    @Override
    public boolean has(CharSequence key) {
        return members.containsKey(CharSeq.of(key));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof BiteMapImpl)) return false;
        BiteMapImpl biteMap = (BiteMapImpl) object;
        return Objects.equals(members, biteMap.members);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(members);
    }

    @Override
    public String toString() {
        return members.toString();
    }
}
