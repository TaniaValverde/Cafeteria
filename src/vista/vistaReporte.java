package vista;

import javax.swing.*;
import java.awt.*;

public class vistaReporte extends JFrame {

    private JComboBox<String> comboTipoReporte;
    private JTextField txtMesa;
    private JTextArea areaReporte;

    // Constructor vacío (IMPORTANTE para MenuPrincipal)
    public vistaReporte() {
        setTitle("Reportes de Ventas - Cafetería UCR");
        setSize(650, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        inicializarEncabezado();
        inicializarFiltros();
        inicializarAreaReporte();
        inicializarBotones();
    }

    // ================= ENCABEZADO =================
    private void inicializarEncabezado() {
        JPanel panelHeader = new JPanel();
        panelHeader.setLayout(new BoxLayout(panelHeader, BoxLayout.Y_AXIS));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JLabel lblTitulo = new JLabel("Reportes de Ventas - Cafetería UCR");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));

        JLabel lblSub = new JLabel("Sistema de Gestión Universitaria - Sede del Sur");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 12));

        panelHeader.add(lblTitulo);
        panelHeader.add(lblSub);

        add(panelHeader, BorderLayout.NORTH);
    }

    // ================= FILTROS =================
    private void inicializarFiltros() {
        JPanel panelFiltros = new JPanel(new GridLayout(2, 2, 10, 10));
        panelFiltros.setBorder(
                BorderFactory.createTitledBorder("Filtros de Reporte")
        );

        panelFiltros.add(new JLabel("Tipo de Reporte:"));
        comboTipoReporte = new JComboBox<>(new String[]{
            "Historial de Ventas",
            "Ventas por Mesa"
        });
        panelFiltros.add(comboTipoReporte);

        panelFiltros.add(new JLabel("Número de Mesa:"));
        txtMesa = new JTextField();
        panelFiltros.add(txtMesa);

        add(panelFiltros, BorderLayout.WEST);
    }

    // ================= ÁREA DE REPORTE =================
    private void inicializarAreaReporte() {
        areaReporte = new JTextArea();
        areaReporte.setEditable(false);
        areaReporte.setFont(new Font("Monospaced", Font.PLAIN, 12));

        areaReporte.setText(
                "================================================================\n"
                + "           HISTORIAL DE VENTAS - CAFETERÍA UCR           \n"
                + "                     SEDE DEL SUR                       \n"
                + "================================================================\n"
                + "FECHA REPORTE: --/--/----                ESTADO: --\n"
                + "----------------------------------------------------------------\n"
                + "ID VENTA  FECHA       HORA   MESA   MONTO TOTAL    MÉTODO PAGO\n"
                + "----------------------------------------------------------------\n"
                + "\n"
                + "----------------------------------------------------------------\n"
                + "RESUMEN DEL DÍA:\n"
                + "TOTAL VENTAS:    0\n"
                + "TOTAL RECAUDADO: ₡ 0.00\n"
                + "----------------------------------------------------------------\n"
        );

        JScrollPane scroll = new JScrollPane(areaReporte);
        add(scroll, BorderLayout.CENTER);
    }

    // ================= BOTONES =================
    private void inicializarBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnMenu = new JButton("Menú Principal");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnGenerar = new JButton("Generar Reporte");

        // Cerrar ventana
        btnMenu.addActionListener(e -> volverAlMenu());

        // Limpiar filtros y área
        btnLimpiar.addActionListener(e -> {
            txtMesa.setText("");
            areaReporte.setText("");
        });

        // Generar reporte (placeholder compatible con controlador)
        btnGenerar.addActionListener(e -> generarReporte());

        panelBotones.add(btnMenu);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnGenerar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    // ================= ACCIÓN GENERAR =================
    private void generarReporte() {
        String tipo = comboTipoReporte.getSelectedItem().toString();
        String mesa = txtMesa.getText().trim();

        areaReporte.setText(
                "================================================================\n"
                + "           REPORTE GENERADO - CAFETERÍA UCR           \n"
                + "================================================================\n"
                + "Tipo de Reporte: " + tipo + "\n"
                + "Mesa: " + (mesa.isEmpty() ? "Todas" : mesa) + "\n"
                + "----------------------------------------------------------------\n"
                + "Aquí se mostrará la información real obtenida desde\n"
                + "ReporteController.\n"
                + "----------------------------------------------------------------\n"
        );
    }
    private void volverAlMenu() {
    dispose();

    SwingUtilities.invokeLater(() -> {
        for (java.awt.Frame f : java.awt.Frame.getFrames()) {
            if (f instanceof JFrame && f.isVisible()
                    && f.getTitle() != null
                    && f.getTitle().contains("Cafetería UCR")) {
                f.toFront();
                f.requestFocus();
                break;
            }
        }
    });
}

}
