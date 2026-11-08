package frontend;

import backend.GrafoPonderado;
import backend.PanelGestionGrafo;
import backend.PanelOptimizacion;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SistemaGrafoGUI extends JFrame {

    private final GrafoPonderado grafo = new GrafoPonderado();

    private PanelGestionGrafo panelGestion;
    private PanelOptimizacion panelOptimizacion;

    public SistemaGrafoGUI() {
        super("Sistema de Gestión de Redes - Actividad 3.4");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1350, 850);
        setMinimumSize(new java.awt.Dimension(1150, 720));
        setLocationRelativeTo(null);

        panelGestion = new PanelGestionGrafo(grafo);
        panelOptimizacion = new PanelOptimizacion(grafo);

        panelGestion.setOnGraphChanged(() -> panelOptimizacion.refrescar());

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Gestión de red", panelGestion);
        tabs.addTab("Optimización de rutas", panelOptimizacion);

        add(tabs);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }

            new SistemaGrafoGUI().setVisible(true);
        });
    }
}