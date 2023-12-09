package org.example.annotations;

public class AnnotationDetected {

    public String label;
    public float confidence;
    public int type;
    public Vertex[] polygon;

    public AnnotationDetected(String label, float confidence, Type type, Vertex[] polygon) {
        this.label = label;
        this.confidence = confidence;
        this.polygon = polygon;
        this.type = type.value;
    }

    public Type getType() {
        return Type.fromValue(type);
    }

    public enum Type {
        TEXT(0),
        OBJECT(1);

        public final int value;

        Type(int value) {
            this.value = value;
        }

        public static Type fromValue(int value) {
            for (Type type : Type.values()) {
                if (type.value == value) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid Type value: " + value);
        }
    }
}
