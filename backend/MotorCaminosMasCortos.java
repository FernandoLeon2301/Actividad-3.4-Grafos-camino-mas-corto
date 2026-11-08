package backend;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class MotorCaminosMasCortos {

    private static final double EPS = 1e-9;
    private static final double INF = Double.POSITIVE_INFINITY;

    public static String fmt(double d) {
        if (Double.isNaN(d)) {
            return "NaN";
        }

        if (Double.isInfinite(d)) {
            return "∞";
        }

        return String.format(Locale.US, "%.2f", d);
    }

    public static ResultadoRuta dijkstra(
            GrafoPonderado grafo,
            String origen,
            String destino,
            TraceListener out
    ) {
        ResultadoRuta resultado = new ResultadoRuta("Dijkstra", origen, destino);
        long inicio = System.nanoTime();

        try {
            validarBase(grafo, origen, destino);

            for (Arista arista : grafo.getAristas()) {
                if (arista.getPeso() < 0) {
                    throw new IllegalArgumentException(
                            "Dijkstra no admite pesos negativos. "
                                    + "Se encontró peso " + fmt(arista.getPeso())
                                    + " en la arista "
                                    + arista.getOrigen() + " -> " + arista.getDestino() + "."
                    );
                }
            }

            out.linea("=== Algoritmo de Dijkstra ===");
            out.linea("Origen: " + origen + " | Destino: " + destino);

            Map<String, Double> dist = inicializarDistancias(grafo, origen);
            Map<String, String> prev = inicializarPredecesores(grafo);

            imprimirInicializacion(out, dist);

            PriorityQueue<NodoPrioridad> cola =
                    new PriorityQueue<>(Comparator.comparingDouble(n -> n.distancia));

            cola.add(new NodoPrioridad(origen, 0.0));

            Set<String> visitados = new HashSet<>();

            while (!cola.isEmpty()) {
                NodoPrioridad actual = cola.poll();

                if (visitados.contains(actual.id)) {
                    continue;
                }

                if (actual.distancia > dist.get(actual.id) + EPS) {
                    out.linea(
                            "[DESCARTADA] Entrada obsoleta en cola para '"
                                    + actual.id + "' con costo " + fmt(actual.distancia)
                    );
                    continue;
                }

                visitados.add(actual.id);
                resultado.nodosProcesados++;

                out.linea(
                        "Paso: se extrae el nodo '" + actual.id
                                + "' con costo acumulado " + fmt(actual.distancia)
                );

                if (actual.id.equals(destino)) {
                    out.linea(
                            "El destino '" + destino
                                    + "' fue extraído de la cola; su distancia ya es óptima."
                    );
                    break;
                }

                Map<String, Double> vecinos = grafo.getAdyacentes(actual.id);

                for (Map.Entry<String, Double> entrada : vecinos.entrySet()) {
                    String vecino = entrada.getKey();
                    double peso = entrada.getValue();

                    resultado.evaluaciones++;

                    double nuevoCosto = actual.distancia + peso;

                    out.linea(
                            "  Evaluando arista "
                                    + actual.id + " -> " + vecino
                                    + " (peso=" + fmt(peso) + ")"
                    );

                    if (nuevoCosto < dist.get(vecino) - EPS) {
                        out.linea(
                                "    [ACEPTADA] dist[" + vecino + "] mejora de "
                                        + fmt(dist.get(vecino)) + " a " + fmt(nuevoCosto)
                        );

                        dist.put(vecino, nuevoCosto);
                        prev.put(vecino, actual.id);
                        resultado.aceptaciones++;

                        cola.add(new NodoPrioridad(vecino, nuevoCosto));
                    } else {
                        out.linea(
                                "    [DESCARTADA] costo propuesto " + fmt(nuevoCosto)
                                        + " no mejora dist[" + vecino + "] = "
                                        + fmt(dist.get(vecino))
                        );

                        resultado.descartes++;
                    }
                }
            }

            finalizarResultado(grafo, origen, destino, dist, prev, resultado, out);

        } catch (Exception ex) {
            resultado.estado = ResultadoRuta.Estado.ERROR;
            resultado.mensaje = ex.getMessage();

            if (out != null) {
                out.linea("[ERROR] " + ex.getMessage());
            }
        }

        resultado.tiempoNanos = System.nanoTime() - inicio;
        return resultado;
    }

    public static ResultadoRuta bellmanFord(
            GrafoPonderado grafo,
            String origen,
            String destino,
            TraceListener out
    ) {
        ResultadoRuta resultado = new ResultadoRuta("Bellman-Ford", origen, destino);
        long inicio = System.nanoTime();

        try {
            validarBase(grafo, origen, destino);

            out.linea("=== Algoritmo de Bellman-Ford ===");
            out.linea("Origen: " + origen + " | Destino: " + destino);

            Map<String, Double> dist = inicializarDistancias(grafo, origen);
            Map<String, String> prev = inicializarPredecesores(grafo);

            imprimirInicializacion(out, dist);

            int V = grafo.cantidadVertices();
            List<Arista> aristas = grafo.getAristas();

            resultado.nodosProcesados = V;

            int iteraciones = Math.max(0, V - 1);

            boolean cambio;

            for (int i = 1; i <= iteraciones; i++) {
                out.linea("Iteración " + i + " de " + iteraciones);

                cambio = false;

                for (Arista arista : aristas) {
                    resultado.evaluaciones++;

                    String u = arista.getOrigen();
                    String v = arista.getDestino();
                    double peso = arista.getPeso();

                    out.linea(
                            "  Evaluando arista "
                                    + u + " -> " + v
                                    + " (peso=" + fmt(peso) + ")"
                    );

                    double distU = dist.get(u);

                    if (Double.isInfinite(distU)) {
                        out.linea(
                                "    [DESCARTADA] dist[" + u
                                        + "] = ∞; la arista no es evaluable desde el origen."
                        );

                        resultado.descartes++;
                        continue;
                    }

                    double nuevoCosto = distU + peso;

                    if (nuevoCosto < dist.get(v) - EPS) {
                        out.linea(
                                "    [ACEPTADA] dist[" + v + "] mejora de "
                                        + fmt(dist.get(v)) + " a " + fmt(nuevoCosto)
                        );

                        dist.put(v, nuevoCosto);
                        prev.put(v, u);
                        resultado.aceptaciones++;
                        cambio = true;
                    } else {
                        out.linea(
                                "    [DESCARTADA] costo propuesto " + fmt(nuevoCosto)
                                        + " no mejora dist[" + v + "] = "
                                        + fmt(dist.get(v))
                        );

                        resultado.descartes++;
                    }
                }

                if (!cambio) {
                    out.linea(
                            "Convergencia temprana: ninguna distancia cambió en la iteración "
                                    + i + "."
                    );
                    break;
                }
            }

            Set<String> nodosCiclo = new HashSet<>();

            for (Arista arista : aristas) {
                String u = arista.getOrigen();
                String v = arista.getDestino();
                double peso = arista.getPeso();

                double distU = dist.get(u);

                if (!Double.isInfinite(distU)
                        && distU + peso < dist.get(v) - EPS) {

                    nodosCiclo.add(v);

                    out.linea(
                            "[CICLO NEGATIVO CANDIDATO] La arista "
                                    + u + " -> " + v
                                    + " todavía puede relajar dist[" + v + "]."
                    );
                }
            }

            if (!nodosCiclo.isEmpty()) {
                out.linea(
                        "Se detectaron relajaciones adicionales que indican "
                                + "la presencia de un ciclo negativo alcanzable."
                );

                if (afectaDestino(nodosCiclo, destino, grafo)) {
                    resultado.estado = ResultadoRuta.Estado.CICLO_NEGATIVO;
                    resultado.mensaje =
                            "Existe un ciclo negativo que puede alcanzar el destino; "
                                    + "no existe camino mínimo bien definido.";

                    out.linea(
                            "El ciclo negativo afecta al destino '" + destino + "'. "
                                    + "No existe camino mínimo bien definido."
                    );

                    resultado.tiempoNanos = System.nanoTime() - inicio;
                    return resultado;
                } else {
                    out.linea(
                            "El ciclo negativo no alcanza al destino solicitado; "
                                    + "se conserva la ruta encontrada."
                    );
                }
            }

            finalizarResultado(grafo, origen, destino, dist, prev, resultado, out);

        } catch (Exception ex) {
            resultado.estado = ResultadoRuta.Estado.ERROR;
            resultado.mensaje = ex.getMessage();

            if (out != null) {
                out.linea("[ERROR] " + ex.getMessage());
            }
        }

        resultado.tiempoNanos = System.nanoTime() - inicio;
        return resultado;
    }

    private static void validarBase(GrafoPonderado grafo, String origen, String destino) {
        if (grafo == null || grafo.estaVacio()) {
            throw new IllegalStateException("El grafo está vacío.");
        }

        if (origen == null || origen.trim().isEmpty()) {
            throw new IllegalArgumentException("El nodo origen es obligatorio.");
        }

        if (destino == null || destino.trim().isEmpty()) {
            throw new IllegalArgumentException("El nodo destino es obligatorio.");
        }

        if (!grafo.existeVertice(origen)) {
            throw new IllegalArgumentException("El nodo origen '" + origen + "' no existe.");
        }

        if (!grafo.existeVertice(destino)) {
            throw new IllegalArgumentException("El nodo destino '" + destino + "' no existe.");
        }
    }

    private static Map<String, Double> inicializarDistancias(
            GrafoPonderado grafo,
            String origen
    ) {
        Map<String, Double> dist = new LinkedHashMap<>();

        for (String id : grafo.getIds()) {
            dist.put(id, INF);
        }

        dist.put(origen, 0.0);

        return dist;
    }

    private static Map<String, String> inicializarPredecesores(GrafoPonderado grafo) {
        Map<String, String> prev = new LinkedHashMap<>();

        for (String id : grafo.getIds()) {
            prev.put(id, null);
        }

        return prev;
    }

    private static void imprimirInicializacion(
            TraceListener out,
            Map<String, Double> dist
    ) {
        out.linea("Fase de inicialización de distancias:");

        for (Map.Entry<String, Double> entrada : dist.entrySet()) {
            out.linea(
                    "  dist[" + entrada.getKey() + "] = "
                            + fmt(entrada.getValue())
            );
        }
    }

    private static void finalizarResultado(
            GrafoPonderado grafo,
            String origen,
            String destino,
            Map<String, Double> dist,
            Map<String, String> prev,
            ResultadoRuta resultado,
            TraceListener out
    ) {
        double distanciaDestino = dist.get(destino);

        if (Double.isInfinite(distanciaDestino)) {
            resultado.estado = ResultadoRuta.Estado.INALCANZABLE;
            resultado.distancia = INF;
            resultado.mensaje =
                    "No existe camino posible entre '" + origen
                            + "' y '" + destino + "'.";

            out.linea("Resultado: " + resultado.mensaje);
        } else {
            resultado.estado = ResultadoRuta.Estado.EXITOSO;
            resultado.distancia = distanciaDestino;
            resultado.camino = reconstruirCamino(prev, origen, destino);

            if (resultado.camino.isEmpty()) {
                resultado.estado = ResultadoRuta.Estado.ERROR;
                resultado.mensaje = "No fue posible reconstruir el camino.";
                out.linea("[ERROR] " + resultado.mensaje);
            } else {
                out.linea(
                        "Resultado final: "
                                + String.join(" -> ", resultado.camino)
                                + " | Costo total: " + fmt(resultado.distancia)
                );
            }
        }
    }

    private static List<String> reconstruirCamino(
            Map<String, String> prev,
            String origen,
            String destino
    ) {
        LinkedList<String> camino = new LinkedList<>();

        String actual = destino;
        int limite = 0;

        while (actual != null && limite <= prev.size() + 5) {
            camino.addFirst(actual);

            if (actual.equals(origen)) {
                break;
            }

            actual = prev.get(actual);
            limite++;
        }

        if (camino.isEmpty() || !camino.getFirst().equals(origen)) {
            return new ArrayList<>();
        }

        return camino;
    }

    private static boolean afectaDestino(
            Set<String> nodosCiclo,
            String destino,
            GrafoPonderado grafo
    ) {
        Set<String> visitados = new HashSet<>();
        Deque<String> cola = new ArrayDeque<>();

        for (String nodo : nodosCiclo) {
            if (nodo.equals(destino)) {
                return true;
            }

            if (visitados.add(nodo)) {
                cola.add(nodo);
            }
        }

        while (!cola.isEmpty()) {
            String actual = cola.poll();

            for (String vecino : grafo.getAdyacentes(actual).keySet()) {
                if (vecino.equals(destino)) {
                    return true;
                }

                if (visitados.add(vecino)) {
                    cola.add(vecino);
                }
            }
        }

        return false;
    }

    private static class NodoPrioridad {
        String id;
        double distancia;

        NodoPrioridad(String id, double distancia) {
            this.id = id;
            this.distancia = distancia;
        }
    }
}