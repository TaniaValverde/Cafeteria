package vista;

import Controlador.ProductController;
import Model.Product;

import java.awt.*;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Product management view for creating, editing, deleting, and listing
 * products.
 *
 * <p>
 * This class contains only UI code (Swing components and event handlers) and
 * delegates business logic to the corresponding controllers.</p>
 */
public class Productview extends JFrame {

    private final ProductController productoController;

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JComboBox<String> cmbCategoria;
    private JTextField txtPrecio;
    private JTextField txtStock;

    private JButton btnAgregar;
    private JButton btnMenu;
    private JButton btnModificar;
    private JButton btnEliminar;

    private JTable tabla;
    private DefaultTableModel modelo;

    private JLabel lblRegistros;
    private JLabel lblUltimaMod;
    private JLabel lblEstado;

    private static final Color PRIMARY = new Color(0x3C, 0xE6, 0x19);
    private static final Color BG_LIGHT = new Color(0xF6, 0xF8, 0xF6);
    private static final Color CARD_BG = new Color(0xF8, 0xFA, 0xF8);
    private static final Color BORDER = new Color(0xE5, 0xE7, 0xEB);
    private static final Color TEXT_DARK = new Color(0x0F, 0x17, 0x2A);
    private static final Color TEXT_MID = new Color(0x64, 0x74, 0x8B);
    private static final Color DARK_BTN = new Color(0x1F, 0x29, 0x37);
    private static final Color DANGER = new Color(0xDC, 0x26, 0x26);

    private static final Color ORANGE_PRIMARY = new Color(230, 149, 36);
    private static final Color NAVY_DARK = new Color(10, 25, 47);
    private static final Color RED_CANCEL = new Color(220, 53, 69);
    private static final Color GRAY_SOFT_BG = new Color(240, 240, 240);

    /**
     * Creates the view and initializes its Swing components.
     */

    public Productview(ProductController productoController) {
        this.productoController = productoController;

        setTitle("Gestión de Productos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 850);
        setMinimumSize(new Dimension(900, 720));
        setLocationRelativeTo(null);

        setContentPane(buildRoot());

        applyInputFilters();

        wireEvents();

        recargarTabla();
        actualizarFooter();

        txtStock.setEnabled(true);
    }

    private JComponent buildRoot() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_LIGHT);
        root.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(Color.WHITE);
        shell.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));

        shell.add(buildHeader(), BorderLayout.NORTH);
        shell.add(buildMain(), BorderLayout.CENTER);
        shell.add(buildFooter(), BorderLayout.SOUTH);

        root.add(shell, BorderLayout.CENTER);
        return root;
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xF1, 0xF5, 0xF9)),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));

        JPanel iconBox = new JPanel(new GridBagLayout());
        iconBox.setBackground(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 35));
        iconBox.setBorder(new LineBorder(new Color(0, 0, 0, 0), 1, true));
        iconBox.setPreferredSize(new Dimension(44, 44));
        iconBox.setMaximumSize(new Dimension(44, 44));

        JLabel ico = new JLabel("📦");
        ico.setFont(new Font("SansSerif", Font.PLAIN, 22));
        ico.setForeground(new Color(0x11, 0x11, 0x11));
        iconBox.add(ico);

        left.add(iconBox);
        left.add(Box.createRigidArea(new Dimension(12, 0)));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel h1 = new JLabel("Gestión de Productos");
        h1.setFont(new Font("SansSerif", Font.BOLD, 28));
        h1.setForeground(TEXT_DARK);

        JLabel sub = new JLabel("CAFETERÍA SYSTEM v1.0");
        sub.setFont(new Font("SansSerif", Font.BOLD, 11));
        sub.setForeground(TEXT_MID);
        sub.setBorder(new EmptyBorder(4, 0, 0, 0));

        titles.add(h1);
        titles.add(sub);

        left.add(titles);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);

        JPanel pill = new JPanel();
        pill.setBackground(new Color(0xF1, 0xF5, 0xF9));
        pill.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 12), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        pill.setLayout(new BoxLayout(pill, BoxLayout.X_AXIS));

        JLabel dot = new JLabel("●");
        dot.setForeground(PRIMARY);
        dot.setFont(new Font("SansSerif", Font.BOLD, 12));

        JLabel txt = new JLabel("  SISTEMA ACTIVO");
        txt.setForeground(TEXT_MID);
        txt.setFont(new Font("SansSerif", Font.BOLD, 11));

        pill.add(dot);
        pill.add(txt);

        btnMenu = ghostButton("⬅ Menú Principal");
        right.add(btnMenu);
        right.add(Box.createRigidArea(new Dimension(10, 0)));
        right.add(pill);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JComponent buildMain() {
        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(18, 18, 18, 18));

        main.add(buildFormCard());
        main.add(Box.createRigidArea(new Dimension(0, 14)));
        main.add(buildToolbar());
        main.add(Box.createRigidArea(new Dimension(0, 14)));
        main.add(buildTableCard());

        return main;
    }

    private JComponent buildFormCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xF1, 0xF5, 0xF9), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 0, 12);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;

        c.gridx = 0;
        c.weightx = 0.9;
        grid.add(fieldGroup("Código (txtCodigo)", txtCodigo = input("Solo números. Ej: 1001")), c);

        c.gridx = 1;
        c.weightx = 2.5;
        grid.add(fieldGroup("Nombre (txtNombre)", txtNombre = input("Café Americano Grande")), c);

        c.gridx = 2;
        c.weightx = 1.2;
        cmbCategoria = new JComboBox<>(new String[]{"BEBIDA", "COMIDA"});
        styleCombo(cmbCategoria);
        grid.add(fieldGroup("Categoría (cmbCategoria)", cmbCategoria), c);

        c.gridx = 3;
        c.weightx = 1.8;
        JPanel precioStock = new JPanel(new GridLayout(1, 2, 10, 0));
        precioStock.setOpaque(false);

        txtPrecio = inputCentered("2.50");
        txtStock = inputCentered("50");

        precioStock.add(fieldGroup("Precio", txtPrecio));
        precioStock.add(fieldGroup("Stock (solo al agregar)", txtStock));

        c.insets = new Insets(0, 0, 0, 0);
        grid.add(precioStock, c);

        card.add(grid, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildToolbar() {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        btnAgregar = primaryButton("➕ Agregar");
        btnModificar = darkButton("✏ Modificar");
        btnEliminar = dangerButton("🗑 Eliminar");

        left.add(btnAgregar);
        left.add(btnModificar);
        left.add(btnEliminar);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private JComponent buildTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));

        modelo = new DefaultTableModel(new String[]{"Código", "Nombre del Producto", "Categoría", "Precio", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(34);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.getTableHeader().setForeground(TEXT_MID);
        tabla.getTableHeader().setBackground(new Color(0xF8, 0xFA, 0xFC));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tabla.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        tabla.removeColumn(tabla.getColumnModel().getColumn(4));

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(null);

        card.add(sp, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(980, 520));

        return card;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(0xF8, 0xFA, 0xFC));
        footer.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                new EmptyBorder(10, 18, 10, 18)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        left.setOpaque(false);

        lblEstado = new JLabel("ℹ Listo para operar   |   🗄 Conectado a Base de Datos Local");
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblEstado.setForeground(TEXT_MID);
        left.add(lblEstado);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        right.setOpaque(false);

        lblRegistros = new JLabel("Registros: 0");
        lblRegistros.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblRegistros.setForeground(TEXT_MID);

        lblUltimaMod = new JLabel("Última modificación: --:--:--");
        lblUltimaMod.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblUltimaMod.setForeground(TEXT_MID);

        right.add(lblRegistros);
        right.add(lblUltimaMod);

        footer.add(left, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);
        return footer;
    }

    private void applyInputFilters() {
        ((AbstractDocument) txtCodigo.getDocument())
                .setDocumentFilter(new RegexFilter("\\d*"));

        ((AbstractDocument) txtStock.getDocument())
                .setDocumentFilter(new RegexFilter("\\d*"));

        ((AbstractDocument) txtPrecio.getDocument())
                .setDocumentFilter(new RegexFilter("\\d*([\\.,]\\d{0,2})?"));

        ((AbstractDocument) txtNombre.getDocument())
                .setDocumentFilter(new RegexFilter("[\\p{L} ]*"));
    }

    private void wireEvents() {
        btnMenu.addActionListener(e -> volverAlMenu());

        btnAgregar.addActionListener(e -> onAgregar());
        btnModificar.addActionListener(e -> onModificar());
        btnEliminar.addActionListener(e -> onEliminar());

        tabla.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionEnFormulario();
            }
        });
    }

    private void onAgregar() {
        try {
            DatosForm d = leerFormulario(true);

            Product nuevo = new Product(d.codigo, d.nombre, d.categoria, d.precio, d.stock);
            productoController.agregar(nuevo);

            recargarTabla();
            actualizarFooterConHora();
            limpiarFormulario();

            JOptionPane.showMessageDialog(this,
                    "Producto guardado ✅",
                    "Productos", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el producto:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void onModificar() {
        try {
            DatosForm d = leerFormulario(false);

            int stockActual = productoController.buscarPorCodigo(d.codigo).getStock();

            productoController.modificar(d.codigo, d.categoria, d.precio, stockActual);

            recargarTabla();
            actualizarFooterConHora();

            JOptionPane.showMessageDialog(this,
                    "Producto actualizado ✅",
                    "Productos", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error guardando cambios:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void onEliminar() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla.", "Productos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String codigo = String.valueOf(modelo.getValueAt(row, 0));

        int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el producto " + codigo + "?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (op != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            productoController.eliminar(codigo);

            recargarTabla();
            actualizarFooterConHora();
            limpiarFormulario();

            JOptionPane.showMessageDialog(this, "Producto eliminado ✅", "Productos", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            showError("Error IO al eliminar: " + ex.getMessage());
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void recargarTabla() {
        modelo.setRowCount(0);

        List<Product> lista = productoController.listar();
        for (Product p : lista) {
            modelo.addRow(new Object[]{
                p.getCodigo(),
                p.getNombre(),
                p.getCategoria(),
                p.getPrecio(),
                p.getStock()
            });
        }

        lblRegistros.setText("Registros: " + modelo.getRowCount());
    }

    private void cargarSeleccionEnFormulario() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            return;
        }

        txtCodigo.setText(String.valueOf(modelo.getValueAt(row, 0)));
        txtNombre.setText(String.valueOf(modelo.getValueAt(row, 1)));
        cmbCategoria.setSelectedItem(String.valueOf(modelo.getValueAt(row, 2)));
        txtPrecio.setText(String.valueOf(modelo.getValueAt(row, 3)));

        txtStock.setText("");

        txtCodigo.setEnabled(false);
        txtNombre.setEnabled(false);

        txtStock.setEnabled(false);
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtNombre.setText("");
        cmbCategoria.setSelectedIndex(0);
        txtPrecio.setText("");
        txtStock.setText("");

        txtCodigo.setEnabled(true);
        txtNombre.setEnabled(true);

        txtStock.setEnabled(true);

        tabla.clearSelection();
    }

    private DatosForm leerFormulario(boolean esAgregar) {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String categoria = String.valueOf(cmbCategoria.getSelectedItem()).trim();

        String sPrecio = txtPrecio.getText().trim().replace(',', '.');
        String sStock = txtStock.getText().trim();

        if (codigo.isEmpty()) {
            throw new IllegalArgumentException("El código es obligatorio.");
        }
        if (esAgregar && nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        double precio;
        try {
            precio = Double.parseDouble(sPrecio);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Precio inválido. Ej: 2.50");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }

        int stock = 0;
        if (esAgregar) {
            try {
                stock = Integer.parseInt(sStock);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Stock inválido. Ej: 50");
            }
            if (stock < 0) {
                throw new IllegalArgumentException("El stock no puede ser negativo.");
            }
        }

        return new DatosForm(codigo, nombre, categoria, precio, stock);
    }

    private void actualizarFooter() {
        lblRegistros.setText("Registros: " + modelo.getRowCount());
        lblUltimaMod.setText("Última modificación: --:--:--");
    }

    private void actualizarFooterConHora() {
        lblRegistros.setText("Registros: " + modelo.getRowCount());
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        lblUltimaMod.setText("Última modificación: " + hora);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JTextField input(String placeholder) {
        JTextField t = new JTextField();
        t.setFont(new Font("SansSerif", Font.PLAIN, 14));
        t.setForeground(TEXT_DARK);
        t.setBackground(Color.WHITE);
        t.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        t.setToolTipText(placeholder);
        return t;
    }

    private JTextField inputCentered(String placeholder) {
        JTextField t = input(placeholder);
        t.setHorizontalAlignment(SwingConstants.CENTER);
        return t;
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cb.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        cb.setBackground(Color.WHITE);
        cb.setForeground(TEXT_DARK);
        cb.setPreferredSize(new Dimension(120, 42));
    }

    private JPanel fieldGroup(String label, JComponent field) {
        JPanel g = new JPanel();
        g.setOpaque(false);
        g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));

        JLabel l = new JLabel(label.toUpperCase());
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(TEXT_MID);

        g.add(l);
        g.add(Box.createRigidArea(new Dimension(0, 6)));
        g.add(field);
        return g;
    }

    private void forceButtonPaint(JButton b) {
        b.setUI(new BasicButtonUI());
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(true);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(ORANGE_PRIMARY);
        b.setForeground(Color.WHITE);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 20), 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));
        forceButtonPaint(b);
        return b;
    }

    private JButton darkButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(NAVY_DARK);
        b.setForeground(Color.WHITE);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 20), 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));
        forceButtonPaint(b);
        return b;
    }

    private JButton dangerButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(NAVY_DARK);
        b.setForeground(RED_CANCEL);
        b.setBorder(new CompoundBorder(
                new LineBorder(RED_CANCEL, 2, true),
                new EmptyBorder(10, 16, 10, 16)
        ));
        forceButtonPaint(b);
        return b;
    }

    private JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(GRAY_SOFT_BG);
        b.setForeground(TEXT_DARK);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 20), 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));
        forceButtonPaint(b);
        return b;
    }

    private static class DatosForm {

        final String codigo;
        final String nombre;
        final String categoria;
        final double precio;
        final int stock;

        DatosForm(String codigo, String nombre, String categoria, double precio, int stock) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.categoria = categoria;
            this.precio = precio;
            this.stock = stock;
        }
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

    private static class RegexFilter extends DocumentFilter {

        private final String allowedRegex;

        RegexFilter(String allowedRegex) {
            this.allowedRegex = allowedRegex;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) {
                return;
            }

            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String next = current.substring(0, offset) + string + current.substring(offset);
            if (next.matches(allowedRegex)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text == null) {
                text = "";
            }

            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String next = current.substring(0, offset) + text + current.substring(offset + length);
            if (next.matches(allowedRegex)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}
