package vista;

import javax.swing.*;
import java.awt.*;

public class vistaReporte extends JFrame {

    private JComboBox<String> comboTipoReporte;
    private JTextField txtMesa;
    private JTextArea areaReporte;

    public vistaReporte() {
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

        filtros.add(new JLabel("Tipo de Reporte:"), gbc);

        gbc.gridy++;
        comboTipoReporte = new JComboBox<>(new String[]{
                "Historial de Ventas",
                "Ventas por Mesa"
        });
        filtros.add(comboTipoReporte, gbc);

        gbc.gridy++;
        filtros.add(new JLabel("Número de Mesa:"), gbc);

        gbc.gridy++;
        txtMesa = new JTextField();
        filtros.add(txtMesa, gbc);

        return filtros;
    }

    private JScrollPane crearPanelReporte() {
        areaReporte = new JTextArea();
        areaReporte.setEditable(false);
        areaReporte.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaReporte.setMargin(new Insets(10, 10, 10, 10));

        areaReporte.setText(
                "============================================================\n" +
                "        HISTORIAL DE VENTAS - CAFETERÍA UCR\n" +
                "                  SEDE DEL SUR\n" +
                "============================================================\n\n" +
                "FECHA REPORTE: --/--/----\n\n" +
                "ID VENTA   FECHA     HORA   MESA   MONTO TOTAL\n" +
                "------------------------------------------------------------\n\n" +
                "RESUMEN DEL DÍA:\n" +
                "TOTAL VENTAS:    0\n" +
                "TOTAL RECAUDADO: ₡ 0.00\n"
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
        });

        btnGenerar.addActionListener(e -> generarReporte());

        botones.add(btnCerrar);
        botones.add(btnLimpiar);
        botones.add(btnGenerar);

        add(botones, BorderLayout.SOUTH);
    }

    private void generarReporte() {
        String tipo = comboTipoReporte.getSelectedItem().toString();
        String mesa = txtMesa.getText().isEmpty() ? "Todas" : txtMesa.getText();

        areaReporte.setText(
                "============================================================\n" +
                "        REPORTE GENERADO - CAFETERÍA UCR\n" +
                "============================================================\n" +
                "Tipo de Reporte: " + tipo + "\n" +
                "Mesa: " + mesa + "\n\n" +
                "Aquí se mostrará la información real desde el controlador\n"
        );
    }
}
