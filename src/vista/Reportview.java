package vista;

import javax.swing.*;
import java.awt.*;

/**
 * Reporting view for generating and displaying sales reports.
 *
 * <p>
 * This class contains only UI code (Swing components and event handlers) and
 * delegates business logic to the corresponding controllers.</p>
 */
public class Reportview extends JFrame {

    private JComboBox<String> comboTipoVenta;
    private JTextField txtMesa;
    private JTextArea areaReporte;

    /**
     * Creates the view and initializes its Swing components.
     */

    public Reportview() {
        setTitle("Reportes de Ventas - Cafetería UCR");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        inicializarEncabezado();
        inicializarContenidoCentral();
        inicializarBotones();
    }

    private void inicializarEncabezado() {
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));
        header.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("Reportes de Ventas - Cafetería UCR");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel subtitulo = new JLabel("Sistema de Gestión Universitaria - Sede del Sur");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 12));

        header.add(titulo);
        header.add(subtitulo);

        add(header, BorderLayout.NORTH);
    }

    private void inicializarContenidoCentral() {
        JPanel centro = new JPanel(new BorderLayout(15, 15));
        centro.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        centro.setBackground(Color.WHITE);

        centro.add(crearPanelFiltros(), BorderLayout.WEST);
        centro.add(crearPanelReporte(), BorderLayout.CENTER);

        add(centro, BorderLayout.CENTER);
    }

    private JPanel crearPanelFiltros() {
        JPanel filtros = new JPanel(new GridBagLayout());
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros de Reporte"));
        filtros.setPreferredSize(new Dimension(260, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0;
        filtros.add(new JLabel("Tipo de Venta:"), gbc);

        gbc.gridy++;
        comboTipoVenta = new JComboBox<>(new String[]{
            "Todas",
            "En mesa",
            "Para llevar"
        });
        filtros.add(comboTipoVenta, gbc);

        gbc.gridy++;
        filtros.add(new JLabel("Número de Mesa:"), gbc);

        gbc.gridy++;
        txtMesa = new JTextField();
        filtros.add(txtMesa, gbc);

        comboTipoVenta.addActionListener(e -> {
            String tipoVenta = comboTipoVenta.getSelectedItem().toString();
            boolean habilitarMesa = !tipoVenta.equals("Para llevar");
            txtMesa.setEnabled(habilitarMesa);
            if (!habilitarMesa) {
                txtMesa.setText("");
            }
        });

        return filtros;
    }

    private JScrollPane crearPanelReporte() {
        areaReporte = new JTextArea();
        areaReporte.setEditable(false);
        areaReporte.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaReporte.setMargin(new Insets(10, 10, 10, 10));

        areaReporte.setText(
                "============================================================\n"
                + "        HISTORIAL DE VENTAS - CAFETERÍA UCR\n"
                + "                  SEDE DEL SUR\n"
                + "============================================================\n\n"
                + "FECHA REPORTE: --/--/----\n\n"
                + "ID VENTA   FECHA     HORA   TIPO       MESA   MONTO TOTAL\n"
                + "------------------------------------------------------------\n\n"
                + "RESUMEN DEL DÍA:\n"
                + "TOTAL VENTAS:    0\n"
                + "TOTAL RECAUDADO: ₡ 0.00\n"
        );

        JScrollPane scroll = new JScrollPane(areaReporte);
        scroll.setBorder(BorderFactory.createTitledBorder("Reporte"));

        return scroll;
    }

    private void inicializarBotones() {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        botones.setBorder(BorderFactory.createEmptyBorder(5, 20, 15, 20));
        botones.setBackground(Color.WHITE);

        JButton btnCerrar = new JButton("Cerrar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnGenerar = new JButton("Generar Reporte");

        btnCerrar.addActionListener(e -> dispose());

        btnLimpiar.addActionListener(e -> {
            txtMesa.setText("");
            areaReporte.setText("");
            comboTipoVenta.setSelectedIndex(0);
            txtMesa.setEnabled(true);
        });

        btnGenerar.addActionListener(e -> generarReporte());

        botones.add(btnCerrar);
        botones.add(btnLimpiar);
        botones.add(btnGenerar);

        add(botones, BorderLayout.SOUTH);
    }

    private void generarReporte() {
        try {
            Persistencia.VentaDAO ventaDAO = new Persistencia.VentaDAO();
            java.util.List<Model.Venta> ventas = ventaDAO.cargar();

            String tipoVenta = comboTipoVenta.getSelectedItem().toString();
            String mesaTxt = txtMesa.getText().trim();

            Integer mesaFiltro = null;
            if (!mesaTxt.isEmpty()) {
                try {
                    mesaFiltro = Integer.parseInt(mesaTxt);
                    if (mesaFiltro < 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException ex) {
                    areaReporte.setText("Número de mesa inválido. Debe ser un número entero >= 0.");
                    return;
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("============================================================\n");
            sb.append("        REPORTE DE VENTAS - CAFETERÍA UCR\n");
            sb.append("                  SEDE DEL SUR\n");
            sb.append("============================================================\n\n");
            sb.append("Tipo de Venta:   ").append(tipoVenta).append("\n");
            sb.append("Mesa:            ").append(mesaFiltro == null ? "Todas" : mesaFiltro).append("\n\n");

            sb.append(String.format("%-10s %-19s %-10s %-6s %-10s %-10s%n",
                    "ID", "FECHA/HORA", "TIPO", "MESA", "ESTADO", "TOTAL"));
            sb.append("------------------------------------------------------------\n");

            int totalVentas = 0;
            double totalRecaudado = 0;

            int totalMesa = 0;
            double recMesa = 0;

            int totalLlevar = 0;
            double recLlevar = 0;

            ventas.sort(java.util.Comparator.comparing(Model.Venta::getFechaHora));

            for (Model.Venta v : ventas) {
                boolean esLlevar = v.esParaLlevar();
                String tipo = esLlevar ? "LLEVAR" : "MESA";

                if (tipoVenta.equals("En mesa") && esLlevar) {
                    continue;
                }
                if (tipoVenta.equals("Para llevar") && !esLlevar) {
                    continue;
                }

                if (mesaFiltro != null) {
                    if (esLlevar) {
                        continue;
                    }
                    if (v.getMesaNumero() != mesaFiltro) {
                        continue;
                    }
                }

                totalVentas++;
                totalRecaudado += v.getTotal();

                if (esLlevar) {
                    totalLlevar++;
                    recLlevar += v.getTotal();
                } else {
                    totalMesa++;
                    recMesa += v.getTotal();
                }

                String fechaHora = formatearFechaHora(v.getFechaHora());
                String mesaCol = esLlevar ? "-" : String.valueOf(v.getMesaNumero());
                String estado = (v.getEstado() == null || v.getEstado().isBlank()) ? "N/A" : v.getEstado();

                sb.append(String.format("%-10s %-19s %-10s %-6s %-10s ₡%-10.2f%n",
                        v.getId(),
                        fechaHora,
                        tipo,
                        mesaCol,
                        estado.toUpperCase(),
                        v.getTotal()));

                if (v.getMetodoPago() != null && !v.getMetodoPago().isBlank()) {
                    sb.append(String.format("   Método de pago: %s%n", v.getMetodoPago()));
                }
            }

            sb.append("\nRESUMEN:\n");
            sb.append("TOTAL VENTAS:    ").append(totalVentas).append("\n");
            sb.append(String.format("TOTAL RECAUDADO: ₡ %.2f%n", totalRecaudado));

            sb.append("\nDESGLOSE:\n");
            sb.append("EN MESA:         ").append(totalMesa).append(" ventas");
            sb.append(String.format(" | ₡ %.2f%n", recMesa));
            sb.append("PARA LLEVAR:     ").append(totalLlevar).append(" ventas");
            sb.append(String.format(" | ₡ %.2f%n", recLlevar));

            if (totalVentas == 0) {
                sb.append("\n(No hay ventas que coincidan con los filtros.)\n");
            }

            areaReporte.setText(sb.toString());

        } catch (Exception e) {
            areaReporte.setText("Error generando reporte:\n" + e.getMessage());
        }
    }

    private String formatearFechaHora(java.time.LocalDateTime dt) {
        if (dt == null) {
            return "N/A";
        }
        java.time.format.DateTimeFormatter f = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dt.format(f);
    }
}
