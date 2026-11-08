package backend;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class PanelGestionGrafo extends JPanel {

    private final GrafoPonderado grafo;
    private final PanelGrafo panelGrafo = new PanelGrafo();

    private final JTextArea txtSalida = new JTextArea(10, 50);

    private final JTextField txtId = new JTextField(8);
    private final JTextField txtNombre = new JTextField(10);
    private final JComboBox<String> cboTipo = new JComboBox<>(new String[]{
            "Terminal", "Almacén", "Centro de Distribución", "Cliente"
    });
    private final JTextField txtEstado = new JTextField(8);
    private final JTextField txtCapacidad = new JTextField(6);

    private final JTextField txtOrigen = new JTextField(6);
    private final JTextField txtDestino = new JTextField(6);
    private final JTextField txtPeso = new JTextField(6);
    private final JTextField txtInicioRecorrido = new JTextField(6);

    private final JButton btnCrear = new JButton("Crear grafo");
    private final JButton btnAgregarVertice = new JButton("Agregar vértice");
    private final JButton btnActualizarVertice = new JButton("Actualizar vértice");
    private final JButton btnBuscarVertice = new JButton("Buscar vértice");
    private final JButton btnEliminarVertice = new JButton("Eliminar vértice");
    private final JButton btnLimpiarCampos = new JButton("Limpiar");

    private final JButton btnAgregarArista = new JButton("Agregar arista");
    private final JButton btnEliminarArista = new JButton("Eliminar arista");
    private final JButton btnBFS = new JButton("BFS");
    private final JButton btnDFS = new JButton("DFS");

    private final JButton btnMatriz = new JButton("Matriz");
    private final JButton btnConectividad = new JButton("Conectividad");
    private final JButton btnDestruir = new JButton("Destruir");
    private final JButton btnLimpiarSalida = new JButton("Limpiar salida");

    private Runnable onGraphChanged;

    public PanelGestionGrafo(GrafoPonderado grafo) {
        this.grafo = grafo;
        initComponents();
        refrescar();
    }

    public void setOnGraphChanged(Runnable r) {
        this.onGraphChanged = r;
    }

    private void initComponents() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        cboTipo.setEditable(true);

        JPanel norte = new JPanel(new GridLayout(1, 3, 8, 0));
        norte.add(crearPanelVertices());
        norte.add(crearPanelAristas());
        norte.add(crearPanelOperaciones());

        add(norte, BorderLayout.NORTH);

        txtSalida.setEditable(false);
        txtSalida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollSalida = new JScrollPane(txtSalida);
        scrollSalida.setBorder(BorderFactory.createTitledBorder("Salida del sistema"));

        panelGrafo.setBackground(Color.WHITE);
        panelGrafo.setBorder(BorderFactory.createTitledBorder("Representación gráfica"));
        panelGrafo.setPreferredSize(new Dimension(500, 300));

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                scrollSalida,
                panelGrafo
        );

        split.setResizeWeight(0.4);

        add(split, BorderLayout.CENTER);

        registrarEventos();
    }

    private JPanel crearPanelVertices() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Vértices"));

        GridBagConstraints c = baseConstraints();
        int row = 0;

        addCampo(p, c, row++, "ID", txtId);
        addCampo(p, c, row++, "Nombre", txtNombre);
        addCampo(p, c, row++, "Tipo", cboTipo);
        addCampo(p, c, row++, "Estado", txtEstado);
        addCampo(p, c, row++, "Capacidad", txtCapacidad);

        addBotones(p, c, row++, btnCrear, btnDestruir);
        addBotones(p, c, row++, btnAgregarVertice, btnActualizarVertice);
        addBotones(p, c, row++, btnBuscarVertice, btnEliminarVertice);
        addBotones(p, c, row++, btnLimpiarCampos);

        addRelleno(p, c, row);

        return p;
    }

    private JPanel crearPanelAristas() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Aristas / Recorridos"));

        GridBagConstraints c = baseConstraints();
        int row = 0;

        addCampo(p, c, row++, "Origen", txtOrigen);
        addCampo(p, c, row++, "Destino", txtDestino);
        addCampo(p, c, row++, "Peso", txtPeso);
        addCampo(p, c, row++, "Inicio BFS/DFS", txtInicioRecorrido);

        addBotones(p, c, row++, btnAgregarArista, btnEliminarArista);
        addBotones(p, c, row++, btnBFS, btnDFS);

        addRelleno(p, c, row);

        return p;
    }

    private JPanel crearPanelOperaciones() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Operaciones"));

        GridBagConstraints c = baseConstraints();
        int row = 0;

        addBotones(p, c, row++, btnMatriz, btnConectividad);
        addBotones(p, c, row++, btnLimpiarSalida);

        addRelleno(p, c, row);

        return p;
    }

    private GridBagConstraints baseConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 3, 0, 3);
        c.anchor = GridBagConstraints.WEST;
        return c;
    }

    private void addCampo(JPanel panel, GridBagConstraints c, int row, String etiqueta, JComponent campo) {
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;

        panel.add(new JLabel(etiqueta), c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        panel.add(campo, c);
    }

    private void addBotones(JPanel panel, GridBagConstraints c, int row, JComponent... botones) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        for (JComponent boton : botones) {
            fila.add(boton);
        }

        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        panel.add(fila, c);

        c.gridwidth = 1;
    }

    private void addRelleno(JPanel panel, GridBagConstraints c, int row) {
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 2;
        c.weighty = 1;
        c.fill = GridBagConstraints.VERTICAL;

        panel.add(Box.createVerticalGlue(), c);

        c.weighty = 0;
        c.gridwidth = 1;
    }

    private void registrarEventos() {
        btnCrear.addActionListener(e -> accionCrearGrafo());
        btnAgregarVertice.addActionListener(e -> accionAgregarVertice());
        btnActualizarVertice.addActionListener(e -> accionActualizarVertice());
        btnBuscarVertice.addActionListener(e -> accionBuscarVertice());
        btnEliminarVertice.addActionListener(e -> accionEliminarVertice());
        btnLimpiarCampos.addActionListener(e -> limpiarCampos());

        btnAgregarArista.addActionListener(e -> accionAgregarArista());
        btnEliminarArista.addActionListener(e -> accionEliminarArista());

        btnBFS.addActionListener(e -> accionBFS());
        btnDFS.addActionListener(e -> accionDFS());

        btnMatriz.addActionListener(e -> accionMatriz());
        btnConectividad.addActionListener(e -> accionConectividad());
        btnDestruir.addActionListener(e -> accionDestruir());
        btnLimpiarSalida.addActionListener(e -> txtSalida.setText(""));
    }

    private void accionCrearGrafo() {
        if (!grafo.estaVacio()) {
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "Al crear el grafo se eliminarán los datos actuales.\n¿Desea continuar?",
                    "Crear grafo",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }

        grafo.crearGrafo();
        limpiarCampos();
        informar("Grafo creado e inicializado.");
        refrescar();
    }

    private void accionAgregarVertice() {
        try {
            String id = requerirTexto(txtId, "ID");
            String nombre = requerirTexto(txtNombre, "Nombre");
            String tipo = requerirCombo(cboTipo, "Tipo");
            String estado = requerirTexto(txtEstado, "Estado");
            double capacidad = leerDouble(txtCapacidad, "Capacidad");

            Vertice vertice = new Vertice(id, nombre, tipo, estado, capacidad);

            grafo.agregarVertice(vertice);

            informar("Vértice '" + id + "' agregado correctamente.");
            refrescar();

        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void accionActualizarVertice() {
        try {
            String id = requerirTexto(txtId, "ID");
            String nombre = requerirTexto(txtNombre, "Nombre");
            String tipo = requerirCombo(cboTipo, "Tipo");
            String estado = requerirTexto(txtEstado, "Estado");
            double capacidad = leerDouble(txtCapacidad, "Capacidad");

            grafo.actualizarVertice(id, nombre, tipo, estado, capacidad);

            informar("Vértice '" + id + "' actualizado correctamente.");
            refrescar();

        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void accionBuscarVertice() {
        try {
            String id = pedirIdSiVacio(txtId, "Buscar vértice");

            Vertice vertice = grafo.buscarVertice(id);

            if (vertice == null) {
                error("El vértice '" + id + "' no existe.");
            } else {
                informar("Búsqueda de vértice:\n" + detalleVertice(vertice));
            }

        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void accionEliminarVertice() {
        try {
            String id = pedirIdSiVacio(txtId, "Eliminar vértice");

            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Eliminar el vértice '" + id + "' y todas sus aristas?",
                    "Eliminar vértice",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }

            grafo.eliminarVertice(id);

            informar("Vértice '" + id + "' eliminado correctamente.");
            refrescar();

        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void accionAgregarArista() {
        try {
            String origen = pedirIdSiVacio(txtOrigen, "Origen de la arista");
            String destino = pedirIdSiVacio(txtDestino, "Destino de la arista");
            double peso = leerDouble(txtPeso, "Peso");

            boolean creada = grafo.agregarArista(origen, destino, peso);

            if (creada) {
                informar("Arista " + origen + " -> " + destino + " creada con peso " + peso + ".");
            } else {
                informar("Arista " + origen + " -> " + destino + " actualizada con peso " + peso + ".");
            }

            refrescar();

        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void accionEliminarArista() {
        try {
            String origen = pedirIdSiVacio(txtOrigen, "Origen de la arista");
            String destino = pedirIdSiVacio(txtDestino, "Destino de la arista");

            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Eliminar la arista " + origen + " -> " + destino + "?",
                    "Eliminar arista",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }

            grafo.eliminarArista(origen, destino);

            informar("Arista " + origen + " -> " + destino + " eliminada correctamente.");
            refrescar();

        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void accionBFS() {
        try {
            String inicio = pedirIdSiVacio(txtInicioRecorrido, "Nodo inicial para BFS");

            List<String> orden = grafo.bfs(inicio);

            informar("BFS desde '" + inicio + "':\n" + secuencia(orden));

        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void accionDFS() {
        try {
            String inicio = pedirIdSiVacio(txtInicioRecorrido, "Nodo inicial para DFS");

            List<String> orden = grafo.dfs(inicio);

            informar("DFS desde '" + inicio + "':\n" + secuencia(orden));

        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void accionMatriz() {
        try {
            informar(grafo.formatearMatriz());
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void accionConectividad() {
        try {
            informar("Evaluación de conectividad: " + grafo.evaluarConectividad());
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void accionDestruir() {
        if (grafo.estaVacio()) {
            informar("El grafo ya está vacío.");
            refrescar();
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Destruir el grafo?\nSe eliminarán todos los vértices y aristas.",
                "Destruir grafo",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        grafo.destruirGrafo();
        limpiarCampos();

        informar("Grafo destruido. La estructura quedó vacía.");
        refrescar();
    }

    private String detalleVertice(Vertice v) {
        return "ID: " + v.getId()
                + "\nNombre: " + v.getNombre()
                + "\nTipo: " + v.getTipo()
                + "\nEstado/Región: " + v.getEstado()
                + "\nCapacidad: " + v.getCapacidad();
    }

    private String secuencia(List<String> lista) {
        if (lista == null || lista.isEmpty()) {
            return "(sin vértices alcanzables)";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) {
                sb.append(" -> ");
            }
            sb.append(lista.get(i));
        }

        return sb.toString();
    }

    private String requerirTexto(JTextField campo, String nombreCampo) throws Exception {
        String valor = campo.getText() == null ? "" : campo.getText().trim();

        if (valor.isEmpty()) {
            throw new Exception("El campo '" + nombreCampo + "' no puede estar vacío.");
        }

        return valor;
    }

    private String requerirCombo(JComboBox<String> combo, String nombreCampo) throws Exception {
        Object item = combo.getSelectedItem();
        String valor = item == null ? "" : item.toString().trim();

        if (valor.isEmpty()) {
            throw new Exception("El campo '" + nombreCampo + "' no puede estar vacío.");
        }

        return valor;
    }

    private double leerDouble(JTextField campo, String nombreCampo) throws Exception {
        String texto = campo.getText() == null ? "" : campo.getText().trim();

        if (texto.isEmpty()) {
            throw new Exception("El campo '" + nombreCampo + "' es obligatorio.");
        }

        try {
            return Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new Exception("El campo '" + nombreCampo + "' debe ser numérico.");
        }
    }

    private String pedirIdSiVacio(JTextField campo, String titulo) throws Exception {
        String valor = campo.getText() == null ? "" : campo.getText().trim();

        if (!valor.isEmpty()) {
            return valor;
        }

        String entrada = JOptionPane.showInputDialog(
                this,
                "Ingrese el id del vértice:",
                titulo,
                JOptionPane.QUESTION_MESSAGE
        );

        if (entrada == null || entrada.trim().isEmpty()) {
            throw new Exception("No se ingresó el id del vértice.");
        }

        campo.setText(entrada.trim());

        return entrada.trim();
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        cboTipo.setSelectedIndex(0);
        txtEstado.setText("");
        txtCapacidad.setText("");

        txtOrigen.setText("");
        txtDestino.setText("");
        txtPeso.setText("");
        txtInicioRecorrido.setText("");

        txtId.requestFocusInWindow();
    }

    private void informar(String mensaje) {
        if (mensaje == null) {
            mensaje = "";
        }

        txtSalida.append(mensaje + System.lineSeparator());
        txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
    }

    private void error(String mensaje) {
        if (mensaje == null) {
            mensaje = "Error desconocido.";
        }

        txtSalida.append("[ERROR] " + mensaje + System.lineSeparator());
        txtSalida.setCaretPosition(txtSalida.getDocument().getLength());
    }

    private void refrescar() {
        panelGrafo.setGrafo(grafo);

        if (onGraphChanged != null) {
            onGraphChanged.run();
        }
    }
}