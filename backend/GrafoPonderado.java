package backend;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class GrafoPonderado {

    private final Map<String, Vertice> vertices = new LinkedHashMap<>();
    private final Map<String, Map<String, Double>> adyacencia = new LinkedHashMap<>();

    public void crearGrafo() {
        vertices.clear();
        adyacencia.clear();
    }

    public void destruirGrafo() {
        crearGrafo();
    }

    public boolean estaVacio() {
        return vertices.isEmpty();
    }

    public int cantidadVertices() {
        return vertices.size();
    }

    public int cantidadAristas() {
        int total = 0;
        for (Map<String, Double> mapa : adyacencia.values()) {
            total += mapa.size();
        }
        return total;
    }

    public void agregarVertice(Vertice vertice) {
        if (vertice == null) {
            throw new IllegalArgumentException("El vértice no puede ser nulo.");
        }

        String id = validarId(vertice.getId());

        if (vertices.containsKey(id)) {
            throw new IllegalArgumentException("El vértice con id '" + id + "' ya existe.");
        }

        vertices.put(id, vertice);
        adyacencia.put(id, new LinkedHashMap<String, Double>());
    }

    public void actualizarVertice(String id, String nombre, String tipo, String estado, double capacidad) {
        String clave = validarId(id);
        Vertice actual = vertices.get(clave);

        if (actual == null) {
            throw new IllegalArgumentException("El vértice con id '" + clave + "' no existe.");
        }

        Vertice validado = new Vertice(clave, nombre, tipo, estado, capacidad);

        actual.setNombre(validado.getNombre());
        actual.setTipo(validado.getTipo());
        actual.setEstado(validado.getEstado());
        actual.setCapacidad(validado.getCapacidad());
    }

    public boolean agregarArista(String origen, String destino, double peso) {
        String o = validarVerticeExistente(origen);
        String d = validarVerticeExistente(destino);

        validarPeso(peso);

        boolean yaExiste = existeArista(o, d);

        Map<String, Double> mapa = adyacencia.get(o);
        if (mapa == null) {
            mapa = new LinkedHashMap<String, Double>();
            adyacencia.put(o, mapa);
        }

        mapa.put(d, peso);

        return !yaExiste;
    }

    public void actualizarArista(String origen, String destino, double peso) {
        String o = validarVerticeExistente(origen);
        String d = validarVerticeExistente(destino);

        validarPeso(peso);

        Map<String, Double> mapa = adyacencia.get(o);

        if (mapa == null || !mapa.containsKey(d)) {
            throw new IllegalArgumentException("La arista '" + o + " -> " + d + "' no existe.");
        }

        mapa.put(d, peso);
    }

    public void eliminarVertice(String id) {
        String clave = validarId(id);

        if (!vertices.containsKey(clave)) {
            throw new IllegalArgumentException("El vértice '" + clave + "' no existe.");
        }

        vertices.remove(clave);
        adyacencia.remove(clave);

        for (Map<String, Double> mapa : adyacencia.values()) {
            mapa.remove(clave);
        }
    }

    public void eliminarArista(String origen, String destino) {
        String o = validarVerticeExistente(origen);
        String d = validarVerticeExistente(destino);

        Map<String, Double> mapa = adyacencia.get(o);

        if (mapa == null || !mapa.containsKey(d)) {
            throw new IllegalArgumentException("La arista '" + o + " -> " + d + "' no existe.");
        }

        mapa.remove(d);
    }

    public Vertice buscarVertice(String id) {
        if (id == null) {
            return null;
        }
        return vertices.get(id.trim());
    }

    public Vertice getVertice(String id) {
        return buscarVertice(id);
    }

    public boolean existeVertice(String id) {
        return id != null && vertices.containsKey(id.trim());
    }

    public boolean existeArista(String origen, String destino) {
        if (origen == null || destino == null) {
            return false;
        }

        Map<String, Double> mapa = adyacencia.get(origen.trim());
        return mapa != null && mapa.containsKey(destino.trim());
    }

    public List<String> getIds() {
        return new ArrayList<>(vertices.keySet());
    }

    public Map<String, Double> getAdyacentes(String id) {
        if (id == null) {
            return Collections.<String, Double>emptyMap();
        }

        Map<String, Double> mapa = adyacencia.get(id.trim());

        if (mapa == null) {
            return Collections.<String, Double>emptyMap();
        }

        return Collections.unmodifiableMap(mapa);
    }

    public List<Arista> getAristas() {
        List<Arista> aristas = new ArrayList<>();

        for (Map.Entry<String, Map<String, Double>> entrada : adyacencia.entrySet()) {
            for (Map.Entry<String, Double> arista : entrada.getValue().entrySet()) {
                aristas.add(new Arista(
                        entrada.getKey(),
                        arista.getKey(),
                        arista.getValue()
                ));
            }
        }

        return aristas;
    }

    public List<String> bfs(String origen) {
        String inicio = validarVerticeExistente(origen);

        LinkedHashSet<String> visitados = new LinkedHashSet<>();
        ArrayDeque<String> cola = new ArrayDeque<>();

        visitados.add(inicio);
        cola.add(inicio);

        while (!cola.isEmpty()) {
            String actual = cola.poll();

            Map<String, Double> vecinos = adyacencia.get(actual);
            if (vecinos == null) {
                continue;
            }

            for (String vecino : vecinos.keySet()) {
                if (visitados.add(vecino)) {
                    cola.add(vecino);
                }
            }
        }

        return new ArrayList<>(visitados);
    }

    public List<String> dfs(String origen) {
        String inicio = validarVerticeExistente(origen);

        LinkedHashSet<String> visitados = new LinkedHashSet<>();
        List<String> orden = new ArrayList<>();

        dfsRecursivo(inicio, visitados, orden);

        return orden;
    }

    private void dfsRecursivo(String actual, Set<String> visitados, List<String> orden) {
        visitados.add(actual);
        orden.add(actual);

        Map<String, Double> vecinos = adyacencia.get(actual);
        if (vecinos == null) {
            return;
        }

        for (String vecino : vecinos.keySet()) {
            if (!visitados.contains(vecino)) {
                dfsRecursivo(vecino, visitados, orden);
            }
        }
    }

    public double[][] getMatrizAdyacencia() {
        List<String> ids = getIds();
        int n = ids.size();

        double[][] matriz = new double[n][n];

        for (int i = 0; i < n; i++) {
            Arrays.fill(matriz[i], Double.POSITIVE_INFINITY);
            matriz[i][i] = 0.0;
        }

        Map<String, Integer> indice = new HashMap<>();

        for (int i = 0; i < n; i++) {
            indice.put(ids.get(i), i);
        }

        for (Map.Entry<String, Map<String, Double>> entrada : adyacencia.entrySet()) {
            Integer i = indice.get(entrada.getKey());

            if (i == null) {
                continue;
            }

            for (Map.Entry<String, Double> arista : entrada.getValue().entrySet()) {
                Integer j = indice.get(arista.getKey());

                if (j != null) {
                    matriz[i][j] = arista.getValue();
                }
            }
        }

        return matriz;
    }

    public String formatearMatriz() {
        if (estaVacio()) {
            return "El grafo está vacío. No hay matriz de adyacencia.";
        }

        List<String> ids = getIds();
        double[][] matriz = getMatrizAdyacencia();

        StringBuilder sb = new StringBuilder();

        sb.append("Matriz de adyacencia (∞ = sin arista):\n");

        sb.append(String.format(Locale.US, "%8s", "ID"));

        for (String id : ids) {
            sb.append(String.format(Locale.US, "%8s", id));
        }

        sb.append('\n');

        for (int i = 0; i < ids.size(); i++) {
            sb.append(String.format(Locale.US, "%8s", ids.get(i)));

            for (int j = 0; j < ids.size(); j++) {
                if (Double.isInfinite(matriz[i][j])) {
                    sb.append(String.format(Locale.US, "%8s", "∞"));
                } else {
                    sb.append(String.format(Locale.US, "%8.2f", matriz[i][j]));
                }
            }

            sb.append('\n');
        }

        return sb.toString();
    }

    public String evaluarConectividad() {
        if (estaVacio()) {
            return "Desconectado (grafo vacío)";
        }

        if (vertices.size() == 1) {
            return "Fuertemente conectado (grafo trivial)";
        }

        boolean fuerte = true;

        for (String id : vertices.keySet()) {
            if (bfs(id).size() != vertices.size()) {
                fuerte = false;
                break;
            }
        }

        if (fuerte) {
            return "Fuertemente conectado";
        }

        if (esDebilmenteConectado()) {
            return "Débilmente conectado";
        }

        return "Desconectado";
    }

    private boolean esDebilmenteConectado() {
        if (estaVacio()) {
            return false;
        }

        Map<String, Set<String>> base = new HashMap<>();

        for (String id : vertices.keySet()) {
            base.put(id, new HashSet<String>());
        }

        for (Map.Entry<String, Map<String, Double>> entrada : adyacencia.entrySet()) {
            String origen = entrada.getKey();

            for (String destino : entrada.getValue().keySet()) {
                base.get(origen).add(destino);
                base.get(destino).add(origen);
            }
        }

        String inicio = vertices.keySet().iterator().next();

        Set<String> visitados = new HashSet<>();
        Deque<String> cola = new ArrayDeque<>();

        visitados.add(inicio);
        cola.add(inicio);

        while (!cola.isEmpty()) {
            String actual = cola.poll();

            Set<String> vecinos = base.get(actual);
            if (vecinos == null) {
                continue;
            }

            for (String vecino : vecinos) {
                if (visitados.add(vecino)) {
                    cola.add(vecino);
                }
            }
        }

        return visitados.size() == vertices.size();
    }

    public String resumen() {
        return "Vértices: " + cantidadVertices()
                + " | Aristas: " + cantidadAristas()
                + " | Conectividad: " + evaluarConectividad();
    }

    private String validarId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador es obligatorio.");
        }
        return id.trim();
    }

    private void validarPeso(double peso) {
        if (!esFinito(peso)) {
            throw new IllegalArgumentException("El peso debe ser numérico y finito.");
        }
    }

    private static boolean esFinito(double valor) {
        return !Double.isNaN(valor) && !Double.isInfinite(valor);
    }

    private String validarVerticeExistente(String id) {
        String clave = validarId(id);

        if (!vertices.containsKey(clave)) {
            throw new IllegalArgumentException("El vértice '" + clave + "' no existe.");
        }

        return clave;
    }

}