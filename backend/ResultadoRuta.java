package backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResultadoRuta {

    public enum Estado {
        EXITOSO,
        INALCANZABLE,
        CICLO_NEGATIVO,
        ERROR
    }

    public final String algoritmo;
    public final String origen;
    public final String destino;

    public Estado estado;
    public double distancia;
    public List<String> camino;

    public int evaluaciones;
    public int aceptaciones;
    public int descartes;
    public int nodosProcesados;

    public long tiempoNanos;
    public String mensaje;

    public ResultadoRuta(String algoritmo, String origen, String destino) {
        this.algoritmo = algoritmo;
        this.origen = origen;
        this.destino = destino;
        this.estado = Estado.ERROR;
        this.distancia = Double.POSITIVE_INFINITY;
        this.camino = new ArrayList<>();
        this.mensaje = "";
    }

    public boolean ok() {
        return estado == Estado.EXITOSO;
    }

    public String formatoDistancia() {
        if (Double.isNaN(distancia)) {
            return "NaN";
        }

        if (Double.isInfinite(distancia)) {
            return "∞";
        }

        return String.format(Locale.US, "%.2f", distancia);
    }
}