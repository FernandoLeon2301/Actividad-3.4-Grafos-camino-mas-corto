package backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PruebasRutas {

    public static String ejecutarSuite() {
        StringBuilder sb = new StringBuilder();
        int[] cont = new int[2];

        sb.append("===== PRUEBAS DEL MÓDULO DE CAMINOS MÁS CORTOS =====").append('\n');

        try {

            GrafoPonderado g1 = new GrafoPonderado();
            agregarVertices(g1, "A", "B");
            g1.agregarArista("A", "B", 3);

            ResultadoRuta d1 = MotorCaminosMasCortos.dijkstra(
                    g1,
                    "A",
                    "B",
                    new TraceListener() {
                        @Override
                        public void linea(String linea) {
                        }
                    }
            );

            ResultadoRuta b1 = MotorCaminosMasCortos.bellmanFord(
                    g1,
                    "A",
                    "B",
                    new TraceListener() {
                        @Override
                        public void linea(String linea) {
                        }
                    }
            );

            registrar(
                    sb,
                    cont,
                    "Ruta óptima entre nodos vecinos directos",
                    d1.ok()
                            && casiIgual(d1.distancia, 3.0)
                            && d1.camino.equals(Arrays.asList("A", "B"))
                            && b1.ok()
                            && casiIgual(b1.distancia, 3.0)
            );

            GrafoPonderado g2 = new GrafoPonderado();
            agregarVertices(g2, "A", "B", "C", "D");

            g2.agregarArista("A", "B", 10);
            g2.agregarArista("A", "C", 2);
            g2.agregarArista("C", "D", 1);
            g2.agregarArista("D", "B", 1);
            g2.agregarArista("C", "B", 20);

            List<String> traza = new ArrayList<>();

            ResultadoRuta d2 = MotorCaminosMasCortos.dijkstra(
                    g2,
                    "A",
                    "B",
                    new TraceListener() {
                        @Override
                        public void linea(String linea) {
                            traza.add(linea);
                        }
                    }
            );

            boolean contieneAceptada = contiene(traza, "[ACEPTADA]");
            boolean contieneDescartada = contiene(traza, "[DESCARTADA]");

            registrar(
                    sb,
                    cont,
                    "Ruta distante + traza muestra aceptación/descarte",
                    d2.ok()
                            && casiIgual(d2.distancia, 4.0)
                            && d2.camino.equals(Arrays.asList("A", "C", "D", "B"))
                            && contieneAceptada
                            && contieneDescartada
            );

            GrafoPonderado g3 = new GrafoPonderado();
            agregarVertices(g3, "A", "B", "X");
            g3.agregarArista("A", "B", 1);

            ResultadoRuta d3 = MotorCaminosMasCortos.dijkstra(
                    g3,
                    "A",
                    "X",
                    new TraceListener() {
                        @Override
                        public void linea(String linea) {
                        }
                    }
            );

            ResultadoRuta b3 = MotorCaminosMasCortos.bellmanFord(
                    g3,
                    "A",
                    "X",
                    new TraceListener() {
                        @Override
                        public void linea(String linea) {
                        }
                    }
            );

            registrar(
                    sb,
                    cont,
                    "Ruta entre nodos no conectados",
                    d3.estado == ResultadoRuta.Estado.INALCANZABLE
                            && b3.estado == ResultadoRuta.Estado.INALCANZABLE
            );

            GrafoPonderado g4 = new GrafoPonderado();
            agregarVertices(g4, "1", "2", "3", "4");

            g4.agregarArista("1", "2", 4);
            g4.agregarArista("1", "3", 1);
            g4.agregarArista("3", "2", 2);
            g4.agregarArista("2", "4", 1);
            g4.agregarArista("1", "4", 10);

            ResultadoRuta d4 = MotorCaminosMasCortos.dijkstra(
                    g4,
                    "1",
                    "4",
                    new TraceListener() {
                        @Override
                        public void linea(String linea) {
                        }
                    }
            );

            ResultadoRuta b4 = MotorCaminosMasCortos.bellmanFord(
                    g4,
                    "1",
                    "4",
                    new TraceListener() {
                        @Override
                        public void linea(String linea) {
                        }
                    }
            );

            registrar(
                    sb,
                    cont,
                    "Comparativa: mismo costo óptimo en ambos algoritmos",
                    d4.ok()
                            && b4.ok()
                            && casiIgual(d4.distancia, b4.distancia)
                            && casiIgual(d4.distancia, 4.0)
            );

            GrafoPonderado g5 = new GrafoPonderado();
            agregarVertices(g5, "A", "B");
            g5.agregarArista("A", "B", -1);

            ResultadoRuta d5 = MotorCaminosMasCortos.dijkstra(
                    g5,
                    "A",
                    "B",
                    new TraceListener() {
                        @Override
                        public void linea(String linea) {
                        }
                    }
            );

            registrar(
                    sb,
                    cont,
                    "Dijkstra rechaza pesos negativos",
                    d5.estado == ResultadoRuta.Estado.ERROR
            );

            GrafoPonderado g6 = new GrafoPonderado();
            agregarVertices(g6, "A", "B", "C");

            g6.agregarArista("A", "B", 4);
            g6.agregarArista("A", "C", 5);
            g6.agregarArista("C", "B", -2);

            ResultadoRuta b6 = MotorCaminosMasCortos.bellmanFord(
                    g6,
                    "A",
                    "B",
                    new TraceListener() {
                        @Override
                        public void linea(String linea) {
                        }
                    }
            );

            registrar(
                    sb,
                    cont,
                    "Bellman-Ford resuelve peso negativo sin ciclo negativo",
                    b6.ok()
                            && casiIgual(b6.distancia, 3.0)
                            && b6.camino.equals(Arrays.asList("A", "C", "B"))
            );

            GrafoPonderado g7 = new GrafoPonderado();
            agregarVertices(g7, "1", "2", "3");

            g7.agregarArista("1", "2", 1);
            g7.agregarArista("2", "3", -1);
            g7.agregarArista("3", "1", -1);

            ResultadoRuta b7 = MotorCaminosMasCortos.bellmanFord(
                    g7,
                    "1",
                    "3",
                    new TraceListener() {
                        @Override
                        public void linea(String linea) {
                        }
                    }
            );

            registrar(
                    sb,
                    cont,
                    "Bellman-Ford detecta ciclo negativo que afecta al destino",
                    b7.estado == ResultadoRuta.Estado.CICLO_NEGATIVO
            );

            boolean consultasContinuas = true;

            try {
                for (int i = 0; i < 5; i++) {
                    MotorCaminosMasCortos.dijkstra(
                            g4,
                            "1",
                            "4",
                            new TraceListener() {
                                @Override
                                public void linea(String linea) {
                                }
                            }
                    );

                    MotorCaminosMasCortos.bellmanFord(
                            g4,
                            "1",
                            "4",
                            new TraceListener() {
                                @Override
                                public void linea(String linea) {
                                }
                            }
                    );
                }
            } catch (Exception ex) {
                consultasContinuas = false;
            }

            registrar(
                    sb,
                    cont,
                    "Consultas continuas sin reiniciar",
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