package backend;

public class Vertice {

    private final String id;
    private String nombre;
    private String tipo;
    private String estado;
    private double capacidad;

    public Vertice(String id, String nombre, String tipo, String estado, double capacidad) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador del vértice es obligatorio.");
        }

        this.id = id.trim();

        setNombre(nombre);
        setTipo(tipo);
        setEstado(estado);
        setCapacidad(capacidad);
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEstado() {
        return estado;
    }

    public double getCapacidad() {
        return capacidad;
    }

    public final void setNombre(String nombre) {
        validarTexto(nombre, "nombre");
        this.nombre = nombre.trim();
    }

    public final void setTipo(String tipo) {
        validarTexto(tipo, "tipo");
        this.tipo = tipo.trim();
    }

    public final void setEstado(String estado) {
        validarTexto(estado, "estado");
        this.estado = estado.trim();
    }

    public final void setCapacidad(double capacidad) {
        if (!esFinito(capacidad) || capacidad < 0) {
            throw new IllegalArgumentException(
                    "La capacidad debe ser un número finito y mayor o igual a cero."
            );
        }
        this.capacidad = capacidad;
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " no puede estar vacío.");
        }
    }

    private static boolean esFinito(double valor) {
        return !Double.isNaN(valor) && !Double.isInfinite(valor);
    }

    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}