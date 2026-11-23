package backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PruebasRutas {

    public static String ejecutarSuite() {
        StringBuilder sb = new StringBuilder();
        int[] cont = new int[2];

        sb.append("===== PRUEBAS DEL MÓDULO DE CAMINOS MÁS CORTOS - ACTIVIDAD 3.4 =====")
                .append('\n');

        try {

            GrafoPonderado g1 = new GrafoPonderado();

            agregarVertices(
                    g1,
                    "1", "2", "3", "4", "5", "6", "7", "8"
            );

            g1.agregarArista("1", "2", 3);
            g1.agregarArista("2", "3", 1);
            g1.agregarArista("3", "4", 1);
            g1.agregarArista("4", "5", 1);
            g1.agregarArista("5", "6", 1);
            g1.agregarArista("6", "7", 1);
            g1.agregarArista("7", "8", 1);
            g1.agregarArista("1", "8", 20);

            ResultadoRuta d1 = MotorCaminosMasCortos.dijkstra(
                    g1,
                    "1",
                    "2",
                    linea -> {
                    }
            );

            ResultadoRuta b1 = MotorCaminosMasCortos.bellmanFord(
                    g1,
                    "1",
                    "2",
                    linea -> {
                    }
            );

            registrar(
                    sb,
                    cont,
                    "Ruta óptima entre nodos vecinos directos (8 nodos)",
                    d1.ok()
                            && casiIgual(d1.distancia, 3.0)
                            && d1.camino.equals(Arrays.asList("1", "2"))
                            && b1.ok()
                            && casiIgual(b1.distancia, 3.0)
            );

            GrafoPonderado g2 = new GrafoPonderado();

            agregarVertices(
                    g2,
                    "1", "2", "3", "4", "5", "6", "7", "8"
            );

            g2.agregarArista("1", "8", 100);
            g2.agregarArista("1", "2", 2);
            g2.agregarArista("2", "3", 2);
            g2.agregarArista("2", "8", 50);
            g2.agregarArista("3", "4", 2);
            g2.agregarArista("3", "8", 60);
            g2.agregarArista("4", "5", 2);
            g2.agregarArista("5", "6", 2);
            g2.agregarArista("6", "7", 2);
            g2.agregarArista("7", "8", 2);

            List<String> traza = new ArrayList<>();

            ResultadoRuta d2 = MotorCaminosMasCortos.dijkstra(
                    g2,
                    "1",
                    "8",
                    linea -> traza.add(linea)
            );

            boolean contieneAceptada = contiene(traza, "[ACEPTADA]");
            boolean contieneDescartada = contiene(traza, "[DESCARTADA]");

            registrar(
                    sb,
                    cont,
                    "Ruta distante + traza muestra aceptación/descarte (8 nodos)",
                    d2.ok()
                            && casiIgual(d2.distancia, 14.0)
                            && d2.camino.equals(
                            Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8")
                    )
                            && contieneAceptada
                            && contieneDescartada
            );

            GrafoPonderado g3 = new GrafoPonderado();

            agregarVertices(
                    g3,
                    "1", "2", "3", "4", "5", "6", "7", "8"
            );

            g3.agregarArista("1", "2", 1);
            g3.agregarArista("2", "3", 1);
            g3.agregarArista("3", "4", 1);
            g3.agregarArista("4", "5", 1);
            g3.agregarArista("5", "6", 1);
            g3.agregarArista("6", "7", 1);

            ResultadoRuta d3 = MotorCaminosMasCortos.dijkstra(
                    g3,
                    "1",
                    "8",
                    linea -> {
                    }
            );

            ResultadoRuta b3 = MotorCaminosMasCortos.bellmanFord(
                    g3,
                    "1",
                    "8",
                    linea -> {
                    }
            );

            registrar(
                    sb,
                    cont,
                    "Ruta entre nodos no conectados (8 nodos)",
                    d3.estado == ResultadoRuta.Estado.INALCANZABLE
                            && b3.estado == ResultadoRuta.Estado.INALCANZABLE
            );

            ResultadoRuta d4 = MotorCaminosMasCortos.dijkstra(
                    g2,
                    "1",
                    "8",
                    linea -> {
                    }
            );

            ResultadoRuta b4 = MotorCaminosMasCortos.bellmanFord(
                    g2,
                    "1",
                    "8",
                    linea -> {
                    }
            );

            registrar(
                    sb,
                    cont,
                    "Comparativa: mismo costo óptimo en ambos algoritmos (8 nodos)",
                    d4.ok()
                            && b4.ok()
                            && casiIgual(d4.distancia, b4.distancia)
                            && casiIgual(d4.distancia, 14.0)
            );

            GrafoPonderado g5 = new GrafoPonderado();

            agregarVertices(
                    g5,
                    "1", "2", "3", "4", "5", "6", "7", "8"
            );

            g5.agregarArista("1", "2", 1);
            g5.agregarArista("2", "3", -1);
            g5.agregarArista("3", "4", 1);
            g5.agregarArista("4", "5", 1);
            g5.agregarArista("5", "6", 1);
            g5.agregarArista("6", "7", 1);
            g5.agregarArista("7", "8", 1);

            ResultadoRuta d5 = MotorCaminosMasCortos.dijkstra(
                    g5,
                    "1",
                    "8",
                    linea -> {
                    }
            );

            registrar(
                    sb,
                    cont,
                    "Dijkstra rechaza pesos negativos (8 nodos)",
                    d5.estado == ResultadoRuta.Estado.ERROR
            );

            GrafoPonderado g6 = new GrafoPonderado();

            agregarVertices(
                    g6,
                    "1", "2", "3", "4", "5", "6", "7", "8"
            );

            g6.agregarArista("1", "2", 5);
            g6.agregarArista("1", "3", 6);
            g6.agregarArista("1", "8", 20);
            g6.agregarArista("3", "2", -2);
            g6.agregarArista("2", "4", 1);
            g6.agregarArista("4", "5", 1);
            g6.agregarArista("5", "6", 1);
            g6.agregarArista("6", "7", 1);
            g6.agregarArista("7", "8", 1);

            ResultadoRuta b6 = MotorCaminosMasCortos.bellmanFord(
                    g6,
                    "1",
                    "8",
                    linea -> {
                    }
            );

            registrar(
                    sb,
                    cont,
                    "Bellman-Ford resuelve peso negativo sin ciclo negativo (8 nodos)",
                    b6.ok()
                            && casiIgual(b6.distancia, 9.0)
                            && b6.camino.equals(
                            Arrays.asList("1", "3", "2", "4", "5", "6", "7", "8")
                    )
            );

            GrafoPonderado g7 = new GrafoPonderado();

            agregarVertices(
                    g7,
                    "1", "2", "3", "4", "5", "6", "7", "8"
            );

            g7.agregarArista("1", "2", 1);
            g7.agregarArista("2", "3", 1);
            g7.agregarArista("3", "4", -1);
            g7.agregarArista("4", "2", -1);
            g7.agregarArista("4", "5", 1);
            g7.agregarArista("5", "6", 1);
            g7.agregarArista("6", "7", 1);
            g7.agregarArista("7", "8", 1);

            ResultadoRuta b7 = MotorCaminosMasCortos.bellmanFord(
                    g7,
                    "1",
                    "8",
                    linea -> {
                    }
            );

            registrar(
                    sb,
                    cont,
                    "Bellman-Ford detecta ciclo negativo que afecta al destino (8 nodos)",
                    b7.estado == ResultadoRuta.Estado.CICLO_NEGATIVO
            );

            boolean consultasContinuas = true;

            try {
                for (int i = 0; i < 5; i++) {
                    MotorCaminosMasCortos.dijkstra(
                            g2,
                            "1",
                            "8",
                            linea -> {
                            }
                    );

                    MotorCaminosMasCortos.bellmanFord(
                            g2,
                            "1",
                            "8",
                            linea -> {
                            }
                    );
                }
            } catch (Exception ex) {
                consultasContinuas = false;
            }

            registrar(
                    sb,
                    cont,
                    "Consultas continuas sin reiniciar (8 nodos)",
                    consultasContinuas
            );

        } catch (Exception ex) {
            sb.append("[ERROR] Error ejecutando pruebas: ")
                    .append(ex.getMessage())
                    .append('\n');

            sb.append("Verifique que se haya permitido el registro de pesos negativos ")
                    .append("para probar Bellman-Ford.")
                    .append('\n');
        }

        sb.append('\n');
        sb.append("Resumen: ")
                .append(cont[0])
                .append(" pruebas pasaron, ")
                .append(cont[1])
                .append(" fallaron.")
                .append('\n');

        return sb.toString();
    }

    private static void agregarVertices(GrafoPonderado g, String... ids) {
        for (String id : ids) {
            g.agregarVertice(
                    new Vertice(
                            id,
                            "Nodo " + id,
                            "Terminal",
                            "Zona " + id,
                            10.0
                    )
            );
        }
    }

    private static boolean contiene(List<String> lista, String token) {
        for (String linea : lista) {
            if (linea != null && linea.contains(token)) {
                return true;
            }
        }

        return false;
    }

    private static boolean casiIgual(double a, double b) {
        return Math.abs(a - b) < 1e-6;
    }

    private static void registrar(
            StringBuilder sb,
            int[] cont,
            String nombre,
            boolean ok
    ) {
        sb.append(ok ? "[PASS] " : "[FAIL] ")
                .append(nombre)
                .append('\n');

        if (ok) {
            cont[0]++;
        } else {
            cont[1]++;
        }
    }
}
