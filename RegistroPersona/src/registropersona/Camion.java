package registropersona;

import javafx.beans.property.SimpleStringProperty;

class Camion extends Vehiculo {
    private final SimpleStringProperty año;

    public Camion(String marca, String modelo, String año) {
        super(marca, modelo);
        this.año = new SimpleStringProperty(año);
    }

    public String getAño() {
        return año.get();
    }

    public SimpleStringProperty añoProperty() {
        return año;
    }
}
