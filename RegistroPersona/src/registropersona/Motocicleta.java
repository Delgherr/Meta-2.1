package registropersona;

import javafx.beans.property.SimpleStringProperty;

class Motocicleta extends Vehiculo {
    private final SimpleStringProperty año;

    public Motocicleta(String marca, String modelo, String año) {
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
