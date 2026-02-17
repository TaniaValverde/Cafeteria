package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * VistaFactura Muestra visualmente la factura generada por el sistema. No
 * contiene lógica de negocio (MVC).
 */
public class vistaFactura extends JFrame {

    private JTextArea areaFactura;
    private JButton btnImprimir;
    private JButton btnCerrar;

    public vistaFactura() {
        setTitle("Factura - Cafetería UCR Sede del Sur");
        setSize(600, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        crearHeader();
        crearCuerpoFactura();
        crearBotones();
    }

    private void crearHeader() {
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(230, 128, 25));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Factura - Cafetería UCR");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Sede del Sur - Sistema de Gestión");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(Color.WHITE);

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        textos.add(lblTitulo);
        textos.add(lblSubtitulo);

        panelHeader.add(textos, BorderLayout.WEST);
        add(panelHeader, BorderLayout.NORTH);
    }

    private void crearCuerpoFactura() {
        areaFactura = new JTextArea();
        areaFactura.setEditable(false);
        areaFactura.setFont(new Font("Courier New", Font.PLAIN, 13));
        areaFactura.setBackground(Color.WHITE);
        areaFactura.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Texto de ejemplo (luego el controlador lo reemplaza)
        areaFactura.setText(
                "CAFETERÍA UCR - SEDE DEL SUR\n"
                + "----------------------------------\n"
                + "Factura generada correctamente\n\n"
                + "FECHA: 24/05/2024\n"
                + "HORA: 14:30\n"
                + "ORDEN: Mesa #5\n\n"
                + "PRODUCTO        CANT    SUBT\n"
                + "----------------------------------\n"
                + "Café Latte       2     ₡3000\n"
                + "Empanada         1     ₡1200\n\n"
                + "----------------------------------\n"
                + "TOTAL:                 ₡4200\n\n"
                + "¡Gracias por su visita!"
        );

        JScrollPane scroll = new JScrollPane(areaFactura);
        add(scroll, BorderLayout.CENTER);
    }

    private void crearBotones() {
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 15, 0));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        btnImprimir = new JButton("Imprimir Factura");
        btnImprimir.setFont(new Font("Arial", Font.BOLD, 16));
        btnImprimir.setBackground(new Color(46, 125, 50));
        btnImprimir.setForeground(Color.WHITE);

        btnCerrar = new JButton("Cerrar / Volver");
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 16));

        panelBotones.add(btnImprimir);
        panelBotones.add(btnCerrar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    /* =====================
       MÉTODOS PARA EL CONTROLADOR
       ===================== */
    // Muestra la factura generada (ej: generarImpresion())
    public void mostrarFactura(String textoFactura) {
        areaFactura.setText(textoFactura);
    }

    public void agregarListenerImprimir(ActionListener listener) {
        btnImprimir.addActionListener(listener);
    }

    public void agregarListenerCerrar(ActionListener listener) {
        btnCerrar.addActionListener(listener);
    }
}
