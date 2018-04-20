package org.ulitzky.flights.model;

import javax.validation.constraints.NotNull;

/**
 * Created by lulitzky on 19.04.18.
 */
public class Aircraft  {

    @NotNull
    private final String registration;

    private final String model;

    private String manufacturer;


    public Aircraft(final String registration,  final String model) {
        this.registration = registration;
        this.model = model;

        if (model.startsWith("A")) {
            this.manufacturer = "AIRBUS";
        } else {
            this.manufacturer = "BOING";
        }
    }

    public Aircraft(final String registration,  final String model, final String manufacturer) {
        this.registration = registration;
        this.manufacturer = manufacturer;
        this.model = model;
    }



    public String getRegistration() {
        return registration;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }

    @Override
    public String toString()  {
        return model;
    }


    @Override
    public boolean equals(final Object o) {
        return (o instanceof  Aircraft) && registration.equalsIgnoreCase(((Aircraft) o).registration);
    }

    @Override
    public int hashCode() {
        return registration.hashCode();
    }
}
