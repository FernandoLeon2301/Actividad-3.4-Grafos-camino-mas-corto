package backend;

public class Arista {

    private final String origen;
    private final String destino;
    private double peso;

    public Arista(String origen, String destino, double peso) {
        if (origen == null || origen.trim().isEmpty()) {
            throw new IllegalArgumentException("El origen de la arista es obligatorio.");
        }

        if (destino == null || destino.trim().isEmpty()) {
            throw new IllegalArgumentException("El destino de la arista es obligatorio.");
        }

        this.origen = origen.trim();
        this.destino = destino.trim();

        setPeso(peso);
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public double getPeso() {
        return peso;
    }

    public final void setPeso(double peso) {
        if (!esFinito(peso)) {
            throw new IllegalArgumentException(
                    "El peso de la arista debe ser numérico y finito."
            );
        }
        this.peso = peso;
    }

    private static boolean esFinito(double valor) {
        return !Double.isNaN(valor) && !Double.isInfinite(valor);
    }

    @Override
    public String toString() {
        return origen + " -> " + destino + " (" + peso + ")";
    }
}