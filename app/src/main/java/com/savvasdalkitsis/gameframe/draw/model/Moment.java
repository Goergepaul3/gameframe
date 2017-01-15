package com.savvasdalkitsis.gameframe.draw.model;

public interface Moment<T> {

    T replicateMoment();

    boolean isIdenticalTo(T moment);
}
