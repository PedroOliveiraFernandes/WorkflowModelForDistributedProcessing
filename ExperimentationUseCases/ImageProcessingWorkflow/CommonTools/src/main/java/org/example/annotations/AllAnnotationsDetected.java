package org.example.annotations;

public class AllAnnotationsDetected {
    public final AnnotationDetected[] annotationsDetecteed;

    public AllAnnotationsDetected() {
        this.annotationsDetecteed = new AnnotationDetected[]{};
    }

    public AllAnnotationsDetected(AnnotationDetected[] annotationsDetecteed) {
        this.annotationsDetecteed = annotationsDetecteed;
    }
}
