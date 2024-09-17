package me.sootysplash.bite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;


public class TypeObject {
    private final Type type;
    private final Object object;

    private TypeObject(Type type, Object object) {
        Objects.requireNonNull(type);
        this.type = type;
        this.object = object;
    }

    public static TypeObject of(Number number) {
        if (number == null) {
            return of(Type.Null, null);
        }
        if (Math.floor(number.doubleValue()) == number.doubleValue()) {
            if (number.byteValue() == number.longValue()) {
                return of(Type.Byte, number.byteValue());
            } else if (number.shortValue() == number.longValue()) {
                return of(Type.Short, number.shortValue());
            } else if (number.intValue() == number.longValue()) {
                return of(Type.Integer, number.intValue());
            } else {
                return of(Type.Long, number.longValue());
            }
        } else if (number.floatValue() == number.doubleValue()) {
            return of(Type.Float, number.floatValue());
        } else {
            return of(Type.Double, number.doubleValue());
        }
    }

    public static TypeObject of(Type type, Object object) {
        if (object == null) {
            type = Type.Null;
        }
        return new TypeObject(type, object);
    }

    public Type getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }

    void write(DataOutputStream dos) throws IOException {
        byte code = (byte) (type.code << 3);
        byte byteLen = 0;
        switch (type) {
            case Boolean:
                code += (byte) (getBoolean() ? 1 : 0);
                break;
            case Nest:
            case Array:
                byte[] amountOfBytes = ((Nestable) getObject()).getBytes();
                int len = amountOfBytes.length;
                switchLabel:
                switch ((byte) (len >>> 24)) {
                    case 0:
                        switch ((byte) (len >>> 16)) {
                            case 0:
                                switch ((byte) (len >>> 8)) {
                                    case 0:
                                        byteLen = 1;
                                        break switchLabel;
                                    default:
                                        byteLen = 2;
                                        break switchLabel;
                                }
                            default:
                                byteLen = 3;
                                break switchLabel;
                        }
                    default:
                        byteLen = 4;
                }
                code += byteLen;
        }
        dos.write(code);
        switch (type) {
            case Byte:
                dos.writeByte(getByte());
                break;
            case Short:
                dos.writeShort(getShort());
                break;
            case Integer:
                dos.writeInt(getInteger());
                break;
            case Float:
                dos.writeFloat(getFloat());
                break;
            case Double:
                dos.writeDouble(getDouble());
                break;
            case Long:
                dos.writeLong(getLong());
                break;
            case CharSequence:
                CharSeq.toOutputStream(getCharSequence(), dos);
                break;
            case Nest:
            case Array:
                byte[] nb = ((Nestable) getObject()).getCacheBytes();
                int len = nb.length;
                switch (byteLen) {
                    case 4:
                        dos.writeByte((byte)(len >>> 24));
                    case 3:
                        dos.writeByte((byte)(len >>> 16));
                    case 2:
                        dos.writeByte((byte)(len >>> 8));
                }
                dos.writeByte((byte)(len));
                dos.write(nb);
                break;
        }
    }

    static TypeObject read(DataInputStream dis) throws IOException {
        byte impureCode = dis.readByte();
        int convert = Math.abs((byte) (impureCode >>> 3));
        Type type = Type.fromCode(convert);
        byte strippedCode = (byte) ((byte) (impureCode << 5) >>> 5);
        Object object = null;
        switch (type) {
            case Boolean:
                object = strippedCode == 1;
                break;
            case Byte:
                object = dis.readByte();
                break;
            case Short:
                object = dis.readShort();
                break;
            case Integer:
                object = dis.readInt();
                break;
            case Float:
                object = dis.readFloat();
                break;
            case Double:
                object = dis.readDouble();
                break;
            case Long:
                object = dis.readLong();
                break;
            case CharSequence:
                object = CharSeq.fromInputStream(dis);
                break;
            case Nest:
                byte[] nestData = new byte[readCompressedInt(strippedCode, dis)];
                dis.read(nestData);
                object = BiteMap.fromBytes(nestData);
                break;
            case Array:
                byte[] arrayData = new byte[readCompressedInt(strippedCode, dis)];
                dis.read(arrayData);
                object = BiteArray.fromBytes(arrayData);
                break;
        }
        return new TypeObject(type, object);
    }

    private static int readCompressedInt(byte strippedCode, DataInputStream dis) throws IOException {
        int second = 0, third = 0, fourth = 0;
        switch (strippedCode) {
            case 4:
                fourth = dis.readUnsignedByte();
            case 3:
                third = dis.readUnsignedByte();
            case 2:
                second = dis.readUnsignedByte();
        }
        return dis.readUnsignedByte() + (second << 8) + (third << 16) + (fourth << 24);
    }

    public boolean isBoolean() {
        return getType() == Type.Boolean && getObject() instanceof Boolean;
    }

    public boolean getBoolean() {
        return (boolean) getObject();
    }

    public boolean isCharSequence() {
        return getType() == Type.CharSequence && getObject() instanceof CharSequence;
    }

    public CharSequence getCharSequence() {
        return (CharSequence) getObject();
    }

    public boolean isNest() {
        return getType() == Type.Nest && getObject() instanceof BiteMapImpl;
    }

    public BiteMap getNest() {
        return (BiteMapImpl) getObject();
    }

    public boolean isArray() {
        return getType() == Type.Array && getObject() instanceof BiteArrayImpl;
    }

    public BiteArray getArray() {
        return (BiteArrayImpl) getObject();
    }

    public boolean isNumber() {
        return Type.isNumber(getType()) && getObject() instanceof Number;
    }

    public boolean isNull() {
        return getType() == Type.Null && getObject() == null;
    }

    public byte getByte() {
        return ((Number) getObject()).byteValue();
    }

    public short getShort() {
        return ((Number) getObject()).shortValue();
    }

    public int getInteger() {
        return ((Number) getObject()).intValue();
    }

    public float getFloat() {
        return ((Number) getObject()).floatValue();
    }

    public double getDouble() {
        return ((Number) getObject()).doubleValue();
    }

    public long  getLong() {
        return ((Number) getObject()).longValue();
    }

    public char getCharacter() {
        return (char) getByte();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeObject)) return false;
        TypeObject that = (TypeObject) o;
        return getType() == that.getType() && Objects.equals(getObject(), that.getObject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getObject());
    }

    @Override
    public String toString() {
        return "TypeObject{" +
                "type=" + getType() +
                ", object=" + getObject() +
                '}';
    }
}
