package org.ulitzky.flights.model;

import javax.validation.constraints.NotNull;

/**
 * Created by lulitzky on 19.04.18.
 */
public class Airport {

    @NotNull
    private final String code;
    private String description;

    public Airport(final String code) {
        this.code = code;
    }

    public Airport(final String code, final String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return code;
    }


    @Override
    public boolean equals(final Object o) {
        return (o instanceof  Airport) && code.equalsIgnoreCase(((Airport) o).code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

}
