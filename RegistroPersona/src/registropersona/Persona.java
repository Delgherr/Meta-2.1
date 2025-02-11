package registropersona;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Persona {
    private final StringProperty nombre;
    private final StringProperty direccion;
    private final StringProperty telefono;
    private final StringProperty vehiculo;

    public Persona(String nombre, String direccion, String telefono, String vehiculo) {
        this.nombre = new SimpleStringProperty(nombre);
        this.direccion = new SimpleStringProperty(direccion);
        this.telefono = new SimpleStringProperty(telefono);
        this.vehiculo = new SimpleStringProperty(vehiculo);
    }

    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public String getDireccion() {
        return direccion.get();
    }

    public StringProperty direccionProperty() {
        return direccion;
    }

    public String getTelefono() {
        return telefono.get();
    }

    public StringProperty telefonoProperty() {
        return telefono;
    }

    public String getVehiculo() {
        return vehiculo.get();
    }

    public StringProperty vehiculoProperty() {
        return vehiculo;
    }
    
    @Override
    public String toString() {
        return "Persona{" +
               "nombre='" + nombre.get() + '\'' +
               ", direccion='" + direccion.get() + '\'' +
               ", telefono='" + telefono.get() + '\'' +
               ", vehiculo='" + vehiculo.get() + '\'' +
               '}';
    }

 }
