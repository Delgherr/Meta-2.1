package registropersona;

public abstract class Vehiculo {
    private String tipo;
    public Vehiculo(String tipo) {
        this.tipo = tipo;
    }
    public String getTipo() {
        return tipo;
    }
}