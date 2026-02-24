package vista;

import Controlador.TableController;
import Controlador.SaleController;
import Model.Sale;
import vista.InvoiceImpressionview;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * View that builds and displays an invoice for a completed sale.
 *
 * <p>
 * This class contains only UI code (Swing components and event handlers) and
 * delegates business logic to the corresponding controllers.</p>
 */
public class Invoiceview extends JFrame {

    private final SaleController ventaCtrl;
    private final TableController mesaCtrl;

    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextArea areaFactura;

    private JLabel lblTotal;
    private JComboBox<String> cmbMetodoPago;

    private Sale ventaSeleccionada;

    private static final Color BG = new Color(0xF5, 0xF7, 0xFA);
    private static final Color CARD = Color.WHITE;
    private static final Color BORDER = new Color(0xE5, 0xE7, 0xEB);
    private static final Color TEXT = new Color(0x0F, 0x17, 0x2A);
    private static final Color TEXT_MID = new Color(0x64, 0x74, 0x8B);

    private static final Color PRIMARY = new Color(0xEE, 0x9D, 0x2B);
    private static final Color NAVY = new Color(0x0B, 0x12, 0x22);

    private static final Color ROW_ALT = new Color(0xF8, 0xFA, 0xFC);
    private static final Color ROW_SEL = new Color(0xE0, 0xF2, 0xFE);
    private static final Color ROW_SEL_BORDER = new Color(0x38BDF8);

    private static final DateTimeFormatter FECHA_CORTA
            = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Creates the view and initializes its Swing components.
     */

    public Invoiceview(SaleController ventaCtrl, TableController mesaCtrl) {
        this.ventaCtrl = ventaCtrl;
        this.mesaCtrl = mesaCtrl;

        setTitle("Facturas Pendientes - Cafetería UCR");
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        cargarPendientes();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(CARD);
        shell.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));

        shell.add(buildTopBar(), BorderLayout.NORTH);
        shell.add(buildBody(), BorderLayout.CENTER);

        root.add(shell, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JComponent buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setBackground(CARD);
        top.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xF1, 0xF5, 0xF9)),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel title = new JLabel("FACTURAS PENDIENTES");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(TEXT);

        JButton btnMenu = ghostButton("▦  MENÚ PRINCIPAL");
        btnMenu.addActionListener(e -> dispose());

        top.add(title, BorderLayout.WEST);
        top.add(btnMenu, BorderLayout.EAST);

        return top;
    }

    private JComponent buildBody() {
        JPanel body = new JPanel(new BorderLayout(14, 14));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(14, 14, 14, 14));

        body.add(buildLeftTable(), BorderLayout.WEST);
        body.add(buildRightPanel(), BorderLayout.CENTER);

        return body;
    }

    private JComponent buildLeftTable() {
        modelo = new DefaultTableModel(new String[]{"ID", "Fecha", "Mesa", "Total"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(32);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setShowGrid(true);
        tabla.setGridColor(new Color(0, 0, 0, 20));

        aplicarHeaderNaranjaPlano();
        aplicarEstiloFilas();

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarVenta();
            }
        });

        JScrollPane sp = new JScrollPane(tabla);
        sp.setPreferredSize(new Dimension(470, 0));
        sp.setBorder(new LineBorder(BORDER, 1, true));

        ajustarAnchosTabla();

        return sp;
    }

    private void ajustarAnchosTabla() {
        SwingUtilities.invokeLater(() -> {
            if (tabla == null || tabla.getColumnModel().getColumnCount() < 4) {
                return;
            }

            TableColumn colId = tabla.getColumnModel().getColumn(0);
            TableColumn colFecha = tabla.getColumnModel().getColumn(1);
            TableColumn colMesa = tabla.getColumnModel().getColumn(2);
            TableColumn colTotal = tabla.getColumnModel().getColumn(3);

            colId.setPreferredWidth(70);
            colFecha.setPreferredWidth(160);
            colMesa.setPreferredWidth(90);
            colTotal.setPreferredWidth(90);
        });
    }

    private void aplicarHeaderNaranjaPlano() {
        JTableHeader header = tabla.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(true);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col
            ) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col
                );

                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setOpaque(true);
                l.setBackground(PRIMARY);
                l.setForeground(Color.WHITE);
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(0, 0, 0, 35)));

                return l;
            }
        });

        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 0, 0, 35)));
    }

    private void aplicarEstiloFilas() {
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col
            ) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col
                );

                c.setOpaque(true);
                c.setFont(new Font("SansSerif", Font.PLAIN, 13));
                c.setForeground(TEXT);

                if (col == 3) {
                    c.setHorizontalAlignment(SwingConstants.RIGHT);
                    c.setBorder(new EmptyBorder(0, 0, 0, 10));
                } else {
                    c.setHorizontalAlignment(SwingConstants.LEFT);
                    c.setBorder(new EmptyBorder(0, 10, 0, 0));
                }

                if (isSelected) {
                    c.setBackground(ROW_SEL);
                    c.setBorder(new CompoundBorder(
                            BorderFactory.createMatteBorder(1, 1, 1, 1, ROW_SEL_BORDER),
                            c.getBorder()
                    ));
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ROW_ALT);
                }

                return c;
            }
        };

        for (int i = 0; i < tabla.getColumnModel().getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }

    private JComponent buildRightPanel() {
        JPanel right = new JPanel(new BorderLayout(12, 12));
        right.setOpaque(false);

        areaFactura = new JTextArea();
        areaFactura.setEditable(false);
        areaFactura.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaFactura.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane sp = new JScrollPane(areaFactura);
        sp.setBorder(new LineBorder(BORDER, 1, true));
        right.add(sp, BorderLayout.CENTER);

        right.add(buildFooter(), BorderLayout.SOUTH);

        return right;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new GridBagLayout());
        footer.setBackground(NAVY);
        footer.setBorder(new EmptyBorder(14, 14, 14, 14));

        lblTotal = new JLabel("TOTAL: ₡0.00");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTotal.setForeground(PRIMARY);

        cmbMetodoPago = new JComboBox<>(new String[]{"EFECTIVO", "TARJETA", "SINPE"});
        cmbMetodoPago.setFont(new Font("SansSerif", Font.BOLD, 14));
        cmbMetodoPago.setPreferredSize(new Dimension(165, 42));
        cmbMetodoPago.setMaximumSize(new Dimension(165, 42));

        JButton btnCobrar = solidButton("💰  COBRAR", PRIMARY, Color.WHITE, 14, 12);
        btnCobrar.setPreferredSize(new Dimension(180, 46));
        btnCobrar.addActionListener(e -> cobrarVenta());

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.insets = new Insets(0, 0, 0, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0;
        gc.weightx = 1.0;
        footer.add(lblTotal, gc);

        gc.gridx = 1;
        gc.weightx = 0.0;
        footer.add(cmbMetodoPago, gc);

        gc.gridx = 2;
        gc.insets = new Insets(0, 0, 0, 0);
        footer.add(btnCobrar, gc);

        return footer;
    }

    private void cargarPendientes() {
        try {
            modelo.setRowCount(0);
            List<Sale> pendientes = ventaCtrl.obtenerPendientes();

            for (Sale v : pendientes) {
                String idCorto = v.getId();
                if (idCorto.length() > 6) {
                    idCorto = idCorto.substring(idCorto.length() - 6);
                }

                String fechaBonita = v.getFechaHora().format(FECHA_CORTA);

                modelo.addRow(new Object[]{
                    idCorto,
                    fechaBonita,
                    v.esParaLlevar() ? "PARA LLEVAR" : "Mesa " + v.getMesaNumero(),
                    String.format("₡%.2f", v.getTotal())
                });
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error cargando ventas pendientes",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarVenta() {
        int row = tabla.getSelectedRow();
        if (row == -1) {
            return;
        }

        String idCorto = modelo.getValueAt(row, 0).toString();

        try {
            ventaSeleccionada = null;

            for (Sale v : ventaCtrl.obtenerPendientes()) {
                String id = v.getId();
                if (id != null && id.endsWith(idCorto)) {
                    ventaSeleccionada = v;
                    break;
                }
            }

            if (ventaSeleccionada != null) {
                areaFactura.setText(ventaCtrl.generarTextoFactura(ventaSeleccionada));
                lblTotal.setText("TOTAL: ₡" + String.format("%.2f", ventaSeleccionada.getTotal()));
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error seleccionando venta",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cobrarVenta() {
        if (ventaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta primero.");
            return;
        }

        String metodo = (String) cmbMetodoPago.getSelectedItem();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmar cobro de ₡" + String.format("%.2f", ventaSeleccionada.getTotal())
                + " por " + metodo + "?",
                "Confirmar Cobro",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String textoFactura = ventaCtrl.generarTextoFactura(ventaSeleccionada, metodo);
            areaFactura.setText(textoFactura);

            InvoiceImpressionview dlg = new InvoiceImpressionview(this, textoFactura);
            dlg.setVisible(true);

            if (!dlg.isImpresionConfirmada()) {
                return;
            }

            ventaCtrl.marcarComoPagada(ventaSeleccionada, metodo);

            if (!ventaSeleccionada.esParaLlevar()) {
                int n = ventaSeleccionada.getMesaNumero();
                mesaCtrl.liberarMesa(n);
            }

            JOptionPane.showMessageDialog(this, "Venta cobrada e impresa correctamente ✅");

            ventaSeleccionada = null;
            areaFactura.setText("");
            lblTotal.setText("TOTAL: ₡0.00");
            cargarPendientes();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cobrar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton solidButton(String text, Color bg, Color fg, int fontSize, int pad) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        b.setForeground(fg);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 18), 1, true),
                new EmptyBorder(pad, 16, pad, 16)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(TEXT_MID);
        b.setBackground(new Color(0xF1, 0xF5, 0xF9));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 12), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}