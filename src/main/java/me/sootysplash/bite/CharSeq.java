package me.sootysplash.bite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class CharSeq implements CharSequence {
    private final char[] chars;

    private CharSeq(char... chars) {
        this.chars = new char[chars.length];
        System.arraycopy(chars, 0, this.chars, 0, chars.length);
    }

    public static CharSeq of(char... chars) {
        return new CharSeq(chars);
    }

    public static CharSeq of(Number number) {
        return of(String.valueOf(number).toCharArray());
    }

    public static CharSeq of(boolean bool) {
        return of(bool ? new char[]{'t', 'r', 'u', 'e'} : new char[]{'f', 'a', 'l', 's', 'e'});
    }

    public static CharSeq of(CharSequence cs) {
        int[] array = cs.chars().toArray();
        int length = array.length;
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = (char) array[i];
        }
        return of(chars);
    }

    @Override
    public int length() {
        return chars.length;
    }

    @Override
    public char charAt(int index) {
        return chars[index];
    }

    @Override
    public CharSeq subSequence(int start, int end) {
        char[] c = new char[end - start];
        if (end - start >= 0) System.arraycopy(chars, start, c, start, end - start);
        return of(c);
    }

    @Override
    public String toString() {
        return new String(this.getChars());
    }

    public CharSeq invert() {
        char[] copy = new char[chars.length];
        for (int i = copy.length - 1; i >= 0; i--) {
            copy[i] = chars[copy.length - 1 - i];
        }
        return new CharSeq(copy);
    }

    public CharSeq concat(CharSeq with) {
        char[] one = this.getChars();
        char[] two = with.getChars();
        char[] c = new char[one.length + two.length];
        System.arraycopy(one, 0, c, 0, one.length);
        System.arraycopy(two, 0, c, one.length, two.length);
        return new CharSeq(c);
    }

    public char[] getChars() {
        char[] chars = new char[this.chars.length];
        System.arraycopy(this.chars, 0, chars, 0, chars.length);
        return chars;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharSeq)) return false;
        return compare(this, (CharSeq) o) == 0;
    }

    public static int compare(CharSeq cs1, CharSeq cs2) {
        if (Objects.requireNonNull(cs1) == Objects.requireNonNull(cs2)) {
            return 0;
        }

        for (int i = 0, len = Math.min(cs1.length(), cs2.length()); i < len; i++) {
            char a = cs1.charAt(i);
            char b = cs2.charAt(i);
            if (a != b) {
                return a - b;
            }
        }

        return cs1.length() - cs2.length();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(chars);
    }

    static CharSeq fromInputStream(DataInputStream is) throws IOException {
        short length = is.readShort();
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = (char) is.readByte();
        }
        return CharSeq.of(chars);
    }

    static void toOutputStream(CharSequence cs, DataOutputStream out) throws IOException {
        short length = (short) cs.length();
        out.writeShort(length);
        for (int i = 0; i < length; i++) {
            out.writeByte(cs.charAt(i));
        }
    }
}
