package io.coderose.ccc2020.challenges;

import java.util.Objects;

public class FlightKey {

    public FlightKey(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    String source;

    String destination;

    @Override
    public String toString() {
        return "FlightKey{" +
                "source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightKey flightKey = (FlightKey) o;
        return Objects.equals(source, flightKey.source) &&
                Objects.equals(destination, flightKey.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination);
    }
}
