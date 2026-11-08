package backend;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

public class PanelGrafo extends JPanel {

    private GrafoPonderado grafo;

    private List<String> rutaResaltada = new ArrayList<>();

    private final DecimalFormat formatoPeso = new DecimalFormat("0.##");
    private static final int RADIO_NODO = 26;

    public void setGrafo(GrafoPonderado grafo) {
        this.grafo = grafo;
        repaint();
    }

    public void setRutaResaltada(List<String> ruta) {
        this.rutaResaltada = ruta == null
                ? new ArrayList<String>()
                : new ArrayList<String>(ruta);

        repaint();
    }

    public void limpiarRuta() {
        setRutaResaltada(null);
    }

    private boolean nodoEnRuta(String id) {
        return rutaResaltada != null && rutaResaltada.contains(id);
    }

    private boolean aristaEnRuta(String origen, String destino) {
        if (rutaResaltada == null || rutaResaltada.size() < 2) {
            return false;
        }

        for (int i = 0; i < rutaResaltada.size() - 1; i++) {
            String actual = rutaResaltada.get(i);
            String siguiente = rutaResaltada.get(i + 1);

            if (actual.equals(origen) && siguiente.equals(destino)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        if (grafo == null || grafo.estaVacio()) {
            g2.setColor(new Color(100, 100, 100));

            String mensaje =
                    "El grafo está vacío. Use 'Crear grafo' y agregue vértices/aristas.";

            FontMetrics fm = g2.getFontMetrics();

            g2.drawString(
                    mensaje,
                    (getWidth() - fm.stringWidth(mensaje)) / 2,
                    getHeight() / 2
            );

            return;
        }

        int ancho = getWidth();
        int alto = getHeight();

        List<String> ids = grafo.getIds();
        int n = ids.size();

        Map<String, Point2D.Double> posiciones = new HashMap<>();

        double cx = ancho / 2.0;
        double cy = alto / 2.0;

        double radio = Math.max(30, Math.min(ancho, alto) / 2.0 - 95);

        for (int i = 0; i < n; i++) {
            double angulo = (2 * Math.PI * i / n) - Math.PI / 2;

            double x;
            double y;

            if (n == 1) {
                x = cx;
                y = cy;
            } else {
                x = cx + radio * Math.cos(angulo);
                y = cy + radio * Math.sin(angulo);
            }

            posiciones.put(ids.get(i), new Point2D.Double(x, y));
        }

        for (String origen : ids) {
            Map<String, Double> vecinos = grafo.getAdyacentes(origen);

            for (Map.Entry<String, Double> entrada : vecinos.entrySet()) {
                String destino = entrada.getKey();
                double peso = entrada.getValue();

                Point2D.Double p1 = posiciones.get(origen);
                Point2D.Double p2 = posiciones.get(destino);

                boolean self = origen.equals(destino);
                boolean doble = !self && grafo.existeArista(destino, origen);
                boolean resaltada = aristaEnRuta(origen, destino);

                dibujarArista(g2, p1, p2, peso, self, doble, resaltada);
            }
        }

        for (String id : ids) {
            boolean enRuta = nodoEnRuta(id);
            dibujarNodo(g2, posiciones.get(id), grafo.getVertice(id), enRuta);
        }
    }

    private void dibujarArista(
            Graphics2D g2,
            Point2D.Double p1,
            Point2D.Double p2,
            double peso,
            boolean selfLoop,
            boolean dobleSentido,
            boolean resaltada
    ) {
        if (p1 == null || p2 == null) {
            return;
        }

        Color colorLinea = resaltada
                ? new Color(0, 140, 70)
                : new Color(90, 90, 90);

        float grosor = resaltada ? 3.0f : 1.6f;

        if (selfLoop) {
            int diametro = 44;

            int x = (int) Math.round(p1.x - diametro / 2.0);
            int y = (int) Math.round(p1.y - RADIO_NODO - diametro);

            g2.setColor(colorLinea);
            g2.setStroke(new BasicStroke(grosor));
            g2.drawOval(x, y, diametro, diametro);

            g2.setColor(
                    resaltada
                            ? new Color(0, 110, 60)
                            : new Color(170, 40, 40)
            );

            g2.drawString(
                    formatoPeso.format(peso),
                    x + diametro / 2 - 8,
                    y - 3
            );

            return;
        }

        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double distancia = Math.hypot(dx, dy);

        if (distancia < 1) {
            return;
        }

        double ux = dx / distancia;
        double uy = dy / distancia;

        double desplazamientoX = 0;
        double desplazamientoY = 0;

        if (dobleSentido) {
            double perpendicularX = -uy;
            double perpendicularY = ux;
            double separacion = 8;

            desplazamientoX = perpendicularX * separacion;
            desplazamientoY = perpendicularY * separacion;
        }

        Point2D.Double inicio = new Point2D.Double(
                p1.x + ux * (RADIO_NODO + 2) + desplazamientoX,
                p1.y + uy * (RADIO_NODO + 2) + desplazamientoY
        );

        Point2D.Double fin = new Point2D.Double(
                p2.x - ux * (RADIO_NODO + 10) + desplazamientoX,
                p2.y - uy * (RADIO_NODO + 10) + desplazamientoY
        );

        g2.setColor(colorLinea);
        g2.setStroke(new BasicStroke(grosor));
        g2.draw(new Line2D.Double(inicio, fin));

        dibujarFlecha(g2, fin, ux, uy);

        double mx = (inicio.x + fin.x) / 2.0;
        double my = (inicio.y + fin.y) / 2.0;

        g2.setColor(
                resaltada
                        ? new Color(0, 110, 60)
                        : new Color(20, 70, 160)
        );

        g2.drawString(
                formatoPeso.format(peso),
                (float) mx + 4,
                (float) my - 4
        );
    }

    private void dibujarFlecha(
            Graphics2D g2,
            Point2D.Double punta,
            double ux,
            double uy
    ) {
        double largoFlecha = 10;
        double anchoFlecha = 4.5;

        double baseX = punta.x - ux * largoFlecha;
        double baseY = punta.y - uy * largoFlecha;

        double perpX = -uy;
        double perpY = ux;

        GeneralPath flecha = new GeneralPath();

        flecha.moveTo(punta.x, punta.y);
        flecha.lineTo(
                baseX + perpX * anchoFlecha,
                baseY + perpY * anchoFlecha
        );

        flecha.lineTo(
                baseX - perpX * anchoFlecha,
                baseY - perpY * anchoFlecha
        );

        flecha.closePath();

        g2.fill(flecha);
    }

    private void dibujarNodo(
            Graphics2D g2,
            Point2D.Double centro,
            Vertice vertice,
            boolean enRuta
    ) {
        if (centro == null || vertice == null) {
            return;
        }

        Ellipse2D.Double nodo = new Ellipse2D.Double(
                centro.x - RADIO_NODO,
                centro.y - RADIO_NODO,
                RADIO_NODO * 2,
                RADIO_NODO * 2
        );

        g2.setColor(
                enRuta
                        ? new Color(204, 255, 204)
                        : new Color(226, 240, 255)
        );

        g2.fill(nodo);

        g2.setColor(
                enRuta
                        ? new Color(0, 130, 70)
                        : new Color(35, 95, 190)
        );

        g2.setStroke(new BasicStroke(2f));
        g2.draw(nodo);

        FontMetrics fm = g2.getFontMetrics();

        String id = vertice.getId();
        String nombre = vertice.getNombre();

        if (nombre == null) {
            nombre = "";
        }

        if (nombre.length() > 18) {
            nombre = nombre.substring(0, 17) + "…";
        }

        g2.setColor(Color.BLACK);
        g2.drawString(
                id,
                (int) centro.x - fm.stringWidth(id) / 2,
                (int) centro.y + fm.getAscent() / 2 - 2
        );

        g2.setColor(new Color(70, 70, 70));
        g2.drawString(
                nombre,
                (int) centro.x - fm.stringWidth(nombre) / 2,
                (int) centro.y + RADIO_NODO + 15
        );
    }
}