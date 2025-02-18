package registropersona;

import java.util.List;

class Persona {
    private int id;
    private String nombre;
    private String direccion;
    private String tipoVehiculo;
    private List<String> telefonos;

    public Persona(int id, String nombre, String direccion, String tipoVehiculo, List<String> telefonos) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.tipoVehiculo = tipoVehiculo;
        this.telefonos = telefonos;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public String getTipoVehiculo() { return tipoVehiculo; }

    // Método para obtener los teléfonos como una cadena separada por comas
    public String getTelefonos() {
        return String.join(", ", telefonos);
    }
}
