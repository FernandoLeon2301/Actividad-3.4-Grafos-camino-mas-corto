package backend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class PanelOptimizacion extends JPanel {

    private final GrafoPonderado grafo;

    private final PanelGrafo panelGrafo = new PanelGrafo();

    private final JComboBox<String> cboOrigen = new JComboBox<>();
    private final JComboBox<String> cboDestino = new JComboBox<>();

    private final JComboBox<String> cboAlgoritmo = new JComboBox<>(
            new String[]{
                    "Dijkstra",
                    "Bellman-Ford"
            }
    );

    private final JTextArea txtTraza = new JTextArea(20, 70);
    private final JTextArea txtResultado = new JTextArea(6, 70);

    private final JButton btnEjecutar = new JButton("Ejecutar algoritmo seleccionado");
    private final JButton btnComparar = new JButton("Comparar Dijkstra vs Bellman-Ford");
    private final JButton btnLimpiar = new JButton("Limpiar traza");
    private final JButton btnPruebas = new JButton("Pruebas de rutas");

    private SwingWorker<?, ?> trabajador;

    public PanelOptimizacion(GrafoPonderado grafo) {
        this.grafo = grafo;
        initComponents();
        refrescar();
    }

    private void initComponents() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel pnlControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        pnlControles.setBorder(
                BorderFactory.createTitledBorder("Selección de ruta y algoritmo")
        );

        pnlControles.add(new JLabel("Origen:"));
        pnlControles.add(cboOrigen);

        pnlControles.add(new JLabel("Destino:"));
        pnlControles.add(cboDestino);

        pnlControles.add(new JLabel("Algoritmo:"));
        pnlControles.add(cboAlgoritmo);

        pnlControles.add(btnEjecutar);
        pnlControles.add(btnComparar);
        pnlControles.add(btnLimpiar);
        pnlControles.add(btnPruebas);

        add(pnlControles, BorderLayout.NORTH);

        txtTraza.setEditable(false);
        txtTraza.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        txtResultado.setEditable(false);
        txtResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollTraza = new JScrollPane(txtTraza);
        scrollTraza.setBorder(
                BorderFactory.createTitledBorder("Traza paso a paso del algoritmo")
        );

        JScrollPane scrollResultado = new JScrollPane(txtResultado);
        scrollResultado.setBorder(
                BorderFactory.createTitledBorder("Resultado final")
        );

        JSplitPane splitIzquierdo = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                scrollTraza,
                scrollResultado
        );

        splitIzquierdo.setResizeWeight(0.80);

        panelGrafo.setBackground(Color.WHITE);
        panelGrafo.setBorder(
                BorderFactory.createTitledBorder("Representación gráfica y ruta óptima")
        );
        panelGrafo.setPreferredSize(new Dimension(430, 300));

        JSplitPane splitPrincipal = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                splitIzquierdo,
                panelGrafo
        );

        splitPrincipal.setResizeWeight(0.75);

        add(splitPrincipal, BorderLayout.CENTER);

        btnEjecutar.addActionListener(e -> ejecutarSeleccionado());
        btnComparar.addActionListener(e -> ejecutarComparacion());
        btnLimpiar.addActionListener(e -> limpiarRegistro());
        btnPruebas.addActionListener(e -> ejecutarPruebas());
    }

    public final void refrescar() {
        refrescarCombos();
        panelGrafo.setRutaResaltada(null);
        panelGrafo.setGrafo(grafo);
    }

    private void refrescarCombos() {
        String seleccionadoOrigen =
                cboOrigen.getSelectedItem() == null
                        ? null
                        : cboOrigen.getSelectedItem().toString();

        String seleccionadoDestino =
                cboDestino.getSelectedItem() == null
                        ? null
                        : cboDestino.getSelectedItem().toString();

        cboOrigen.removeAllItems();
        cboDestino.removeAllItems();

        for (String id : grafo.getIds()) {
            cboOrigen.addItem(id);
            cboDestino.addItem(id);
        }

        if (seleccionadoOrigen != null && grafo.existeVertice(seleccionadoOrigen)) {
            cboOrigen.setSelectedItem(seleccionadoOrigen);
        } else if (cboOrigen.getItemCount() > 0) {
            cboOrigen.setSelectedIndex(0);
        }

        if (seleccionadoDestino != null && grafo.existeVertice(seleccionadoDestino)) {
            cboDestino.setSelectedItem(seleccionadoDestino);
        } else if (cboDestino.getItemCount() > 0) {
            cboDestino.setSelectedIndex(cboDestino.getItemCount() - 1);
        }
    }

    private void ejecutarPruebas() {
        try {
            limpiarRegistro();
            txtTraza.append(PruebasRutas.ejecutarSuite());
            txtResultado.setText("Pruebas automáticas del módulo de rutas ejecutadas.");
        } catch (Exception ex) {
            txtTraza.append("[ERROR] " + ex.getMessage() + System.lineSeparator());
        }
    }

    private void ejecutarSeleccionado() {
        String origen = cboOrigen.getSelectedItem() == null
                ? null
                : cboOrigen.getSelectedItem().toString();

        String destino = cboDestino.getSelectedItem() == null
                ? null
                : cboDestino.getSelectedItem().toString();

        String algoritmo = cboAlgoritmo.getSelectedItem() == null
                ? "Dijkstra"
                : cboAlgoritmo.getSelectedItem().toString();

        if (!validarNodos(origen, destino)) {
            return;
        }

        if (!prepararEjecucion()) {
            return;
        }

        limpiarRegistro();
        setControles(false);

        SwingWorker<ResultadoRuta, String> worker =
                new SwingWorker<ResultadoRuta, String>() {

                    @Override
                    protected ResultadoRuta doInBackground() {
                        TraceListener listener = new TraceListener() {
                            @Override
                            public void linea(String linea) {
                                publish(linea);
                            }
                        };

                        if ("Dijkstra".equals(algoritmo)) {
                            return MotorCaminosMasCortos.dijkstra(
                                    grafo,
                                    origen,
                                    destino,
                                    listener
                            );
                        } else {
                            return MotorCaminosMasCortos.bellmanFord(
                                    grafo,
                                    origen,
                                    destino,
                                    listener
                            );
                        }
                    }

                    @Override
                    protected void process(List<String> chunks) {
                        for (String linea : chunks) {
                            txtTraza.append(linea + System.lineSeparator());
                            txtTraza.setCaretPosition(
                                    txtTraza.getDocument().getLength()
                            );
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            ResultadoRuta resultado = get();
                            mostrarResultadoIndividual(resultado);
                        } catch (Exception ex) {
                            txtTraza.append("[ERROR] " + ex.getMessage() + System.lineSeparator());
                        } finally {
                            setControles(true);
                        }
                    }
                };

        trabajador = worker;
        worker.execute();
    }

    private void ejecutarComparacion() {
        String origen = cboOrigen.getSelectedItem() == null
                ? null
                : cboOrigen.getSelectedItem().toString();

        String destino = cboDestino.getSelectedItem() == null
                ? null
                : cboDestino.getSelectedItem().toString();

        if (!validarNodos(origen, destino)) {
            return;
        }

        if (!prepararEjecucion()) {
            return;
        }

        limpiarRegistro();
        setControles(false);

        SwingWorker<List<ResultadoRuta>, String> worker =
                new SwingWorker<List<ResultadoRuta>, String>() {

                    @Override
                    protected List<ResultadoRuta> doInBackground() {
                        TraceListener listener = new TraceListener() {
                            @Override
                            public void linea(String linea) {
                                publish(linea);
                            }
                        };

                        publish("===== EJECUCIÓN COMPARATIVA =====");
                        publish("Origen: " + origen + " | Destino: " + destino);
                        publish("");

                        ResultadoRuta dijkstra =
                                MotorCaminosMasCortos.dijkstra(
                                        grafo,
                                        origen,
                                        destino,
                                        listener
                                );

                        publish("");
                        publish("--------------------------------------------------");
                        publish("");

                        ResultadoRuta bellmanFord =
                                MotorCaminosMasCortos.bellmanFord(
                                        grafo,
                                        origen,
                                        destino,
                                        listener
                                );

                        publish("");
                        publish(construirComparativa(dijkstra, bellmanFord));

                        return Arrays.asList(dijkstra, bellmanFord);
                    }

                    @Override
                    protected void process(List<String> chunks) {
                        for (String linea : chunks) {
                            txtTraza.append(linea + System.lineSeparator());
                            txtTraza.setCaretPosition(
                                    txtTraza.getDocument().getLength()
                            );
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            List<ResultadoRuta> resultados = get();
                            mostrarResultadoComparativo(
                                    resultados.get(0),
                                    resultados.get(1)
                            );
                        } catch (Exception ex) {
                            txtTraza.append("[ERROR] " + ex.getMessage() + System.lineSeparator());
                        } finally {
                            setControles(true);
                        }
                    }
                };

        trabajador = worker;
        worker.execute();
    }

    private boolean prepararEjecucion() {
        if (trabajador != null && !trabajador.isDone()) {
            txtTraza.append("[INFO] Ya hay una ejecución en curso." + System.lineSeparator());
            return false;
        }

        return true;
    }

    private boolean validarNodos(String origen, String destino) {
        StringBuilder errores = new StringBuilder();

        if (grafo == null || grafo.estaVacio()) {
            errores.append("El grafo está vacío.").append(System.lineSeparator());
        } else {
            if (origen == null || origen.trim().isEmpty()) {
                errores.append("Debe seleccionar el nodo origen.").append(System.lineSeparator());
            }

            if (destino == null || destino.trim().isEmpty()) {
                errores.append("Debe seleccionar el nodo destino.").append(System.lineSeparator());
            }

            if (origen != null && !origen.trim().isEmpty()
                    && !grafo.existeVertice(origen)) {
                errores.append("El nodo origen '").append(origen)
                        .append("' no existe.").append(System.lineSeparator());
            }

            if (destino != null && !destino.trim().isEmpty()
                    && !grafo.existeVertice(destino)) {
                errores.append("El nodo destino '").append(destino)
                        .append("' no existe.").append(System.lineSeparator());
            }
        }

        if (errores.length() > 0) {
            txtResultado.setText("");
            txtTraza.append("[ERROR] Validación de entrada:" + System.lineSeparator());
            txtTraza.append(errores.toString());
            txtTraza.setCaretPosition(txtTraza.getDocument().getLength());
            return false;
        }

        return true;
    }

    private void mostrarResultadoIndividual(ResultadoRuta resultado) {
        StringBuilder sb = new StringBuilder();

        sb.append("Algoritmo: ").append(resultado.algoritmo).append('\n');
        sb.append("Origen: ").append(resultado.origen).append('\n');
        sb.append("Destino: ").append(resultado.destino).append('\n');
        sb.append("Estado: ").append(resultado.estado).append('\n');

        if (resultado.estado == ResultadoRuta.Estado.EXITOSO) {
            sb.append("Ruta: ")
                    .append(String.join(" -> ", resultado.camino))
                    .append('\n');

            sb.append("Costo total: ")
                    .append(MotorCaminosMasCortos.fmt(resultado.distancia))
                    .append('\n');

            panelGrafo.setRutaResaltada(resultado.camino);
        } else if (resultado.estado == ResultadoRuta.Estado.INALCANZABLE) {
            sb.append("No existe camino entre origen y destino.").append('\n');
            panelGrafo.setRutaResaltada(null);
        } else if (resultado.estado == ResultadoRuta.Estado.CICLO_NEGATIVO) {
            sb.append("No existe camino mínimo debido a un ciclo negativo.").append('\n');
            panelGrafo.setRutaResaltada(null);
        } else {
            sb.append("Mensaje: ").append(resultado.mensaje).append('\n');
            panelGrafo.setRutaResaltada(null);
        }

        txtResultado.setText(sb.toString());
        txtTraza.setCaretPosition(txtTraza.getDocument().getLength());
    }

    private void mostrarResultadoComparativo(
            ResultadoRuta dijkstra,
            ResultadoRuta bellmanFord
    ) {
        String resumen = construirComparativa(dijkstra, bellmanFord);
        txtResultado.setText(resumen);

        ResultadoRuta principal = null;

        if (dijkstra.ok()) {
            principal = dijkstra;
        } else if (bellmanFord.ok()) {
            principal = bellmanFord;
        }

        if (principal != null) {
            panelGrafo.setRutaResaltada(principal.camino);
        } else {
            panelGrafo.setRutaResaltada(null);
        }

        txtTraza.setCaretPosition(txtTraza.getDocument().getLength());
    }

    private String construirComparativa(
            ResultadoRuta dijkstra,
            ResultadoRuta bellmanFord
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append("===== COMPARATIVA DE ALGORITMOS =====").append('\n');
        sb.append('\n');

        sb.append(resumenParaComparar(dijkstra));
        sb.append('\n');
        sb.append(resumenParaComparar(bellmanFord));
        sb.append('\n');

        if (dijkstra.estado == ResultadoRuta.Estado.EXITOSO
                && bellmanFord.estado == ResultadoRuta.Estado.EXITOSO) {

            double diferencia = Math.abs(dijkstra.distancia - bellmanFord.distancia);

            if (diferencia < 1e-6) {
                sb.append("CONCLUSIÓN: Ambos algoritmos obtuvieron el mismo costo óptimo.")
                        .append('\n');
            } else {
                sb.append("CONCLUSIÓN: Los costos obtenidos difieren.")
                        .append('\n');
            }

        } else if (dijkstra.estado == ResultadoRuta.Estado.INALCANZABLE
                && bellmanFord.estado == ResultadoRuta.Estado.INALCANZABLE) {

            sb.append("CONCLUSIÓN: Ambos algoritmos determinaron que el destino es inaccesible.")
                    .append('\n');

        } else if (dijkstra.estado == ResultadoRuta.Estado.CICLO_NEGATIVO
                || bellmanFord.estado == ResultadoRuta.Estado.CICLO_NEGATIVO) {

            sb.append("CONCLUSIÓN: Se detectó un ciclo negativo que afecta la ruta.")
                    .append('\n');

        } else {
            sb.append("CONCLUSIÓN: Los estados de los algoritmos difieren.")
                    .append('\n');
        }

        return sb.toString();
    }

    private String resumenParaComparar(ResultadoRuta r) {
        StringBuilder sb = new StringBuilder();

        sb.append(r.algoritmo).append(":").append('\n');
        sb.append("  Estado: ").append(r.estado).append('\n');

        if (r.estado == ResultadoRuta.Estado.EXITOSO) {
            sb.append("  Ruta: ")
                    .append(String.join(" -> ", r.camino))
                    .append('\n');

            sb.append("  Costo: ")
                    .append(MotorCaminosMasCortos.fmt(r.distancia))
                    .append('\n');
        } else if (r.mensaje != null && !r.mensaje.trim().isEmpty()) {
            sb.append("  Mensaje: ").append(r.mensaje).append('\n');
        }

        sb.append("  Evaluaciones: ").append(r.evaluaciones).append('\n');
        sb.append("  Aceptaciones: ").append(r.aceptaciones).append('\n');
        sb.append("  Descartes: ").append(r.descartes).append('\n');
        sb.append("  Nodos procesados: ").append(r.nodosProcesados).append('\n');

        sb.append("  Tiempo: ")
                .append(
                        String.format(
                                Locale.US,
                                "%.2f ms",
                                r.tiempoNanos / 1_000_000.0
                        )
                )
                .append('\n');

        return sb.toString();
    }

    private void limpiarRegistro() {
        txtTraza.setText("");
        txtResultado.setText("");
        panelGrafo.setRutaResaltada(null);
    }

    private void setControles(boolean habilitado) {
        cboOrigen.setEnabled(habilitado);
        cboDestino.setEnabled(habilitado);
        cboAlgoritmo.setEnabled(habilitado);

        btnEjecutar.setEnabled(habilitado);
        btnComparar.setEnabled(habilitado);
        btnLimpiar.setEnabled(habilitado);
        btnPruebas.setEnabled(habilitado);
    }
}