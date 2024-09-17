package me.sootysplash.bite;

public enum Type {
    Boolean(1), Short(2), Integer(3), Float(4), Double(5), Byte(6), Long(7), CharSequence(8), Nest(9), Array(10), Null(11)
    ;
    public final byte code;
    private static final Type[] numbers;
    Type(int code) {
        this.code = (byte) code;
    }
    static {
        numbers = new Type[]{Short, Integer, Float, Double, Byte};
    }
    public static boolean isNumber(Type type) {
        for (Type verifiedNumbers : numbers) {
            if (verifiedNumbers.equals(type)) {
                return true;
            }
        }
        return false;
    }
    public static Type fromCode(int code) {
        for (Type t : Type.values()) {
            if (t.code == code) {
                return t;
            }
        }
        throw new IllegalArgumentException(String.format("No type for code: %s found!", code));
    }
}
