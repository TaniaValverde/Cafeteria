package vista;

import Controlador.ProductoController;
import Model.Producto;

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

public class vistaProducto extends JFrame {

    // ===== Controller =====
    private final ProductoController productoController;

    // ===== UI Fields =====
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JComboBox<String> cmbCategoria;
    private JTextField txtPrecio;
    private JTextField txtStock;

    private JButton btnAgregar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnRecargar;

    private JTable tabla;
    private DefaultTableModel modelo;

    // Footer labels
    private JLabel lblRegistros;
    private JLabel lblUltimaMod;
    private JLabel lblEstado;

    // ===== Palette (from HTML) =====
    private static final Color PRIMARY = new Color(0x3C, 0xE6, 0x19);      // #3ce619
    private static final Color BG_LIGHT = new Color(0xF6, 0xF8, 0xF6);     // #f6f8f6
    private static final Color CARD_BG = new Color(0xF8, 0xFA, 0xF8);
    private static final Color BORDER = new Color(0xE5, 0xE7, 0xEB);
    private static final Color TEXT_DARK = new Color(0x0F, 0x17, 0x2A);    // slate-900-ish
    private static final Color TEXT_MID = new Color(0x64, 0x74, 0x8B);     // slate-500-ish
    private static final Color DARK_BTN = new Color(0x1F, 0x29, 0x37);     // slate-800-ish
    private static final Color DANGER = new Color(0xDC, 0x26, 0x26);       // red-600

    public vistaProducto(ProductoController productoController) {
        this.productoController = productoController;

        setTitle("Gestión de Productos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 850);
        setMinimumSize(new Dimension(900, 720));
        setLocationRelativeTo(null);

        setContentPane(buildRoot());
        wireEvents();

        recargarTabla();
        actualizarFooter();
    }

    // =========================
    // ===== BUILD ROOT UI =====
    // =========================
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

        // Left: icon + titles
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

        // Right: status pill
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

        // Código
        c.gridx = 0;
        c.weightx = 0.9;
        grid.add(fieldGroup("Código (txtCodigo)", txtCodigo = input("P001")), c);

        // Nombre (colspan 2)
        c.gridx = 1;
        c.weightx = 2.5;
        grid.add(fieldGroup("Nombre (txtNombre)", txtNombre = input("Café Americano Grande")), c);

        // Categoría
        c.gridx = 2;
        c.weightx = 1.2;
        cmbCategoria = new JComboBox<>(new String[]{"BEBIDA", "COMIDA"});
        styleCombo(cmbCategoria);
        grid.add(fieldGroup("Categoría (cmbCategoria)", cmbCategoria), c);

        // Precio + Stock en panel
        c.gridx = 3;
        c.weightx = 1.8;
        JPanel precioStock = new JPanel(new GridLayout(1, 2, 10, 0));
        precioStock.setOpaque(false);

        txtPrecio = inputCentered("2.50");
        txtStock = inputCentered("50");

        precioStock.add(fieldGroup("Precio", txtPrecio));
        precioStock.add(fieldGroup("Stock", txtStock));

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

        btnLimpiar = ghostButton("🧹 Limpiar");
        btnRecargar = ghostButton("🔄 Recargar");

        right.add(btnLimpiar);
        right.add(btnRecargar);

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

        // Model + table
        modelo = new DefaultTableModel(new String[]{"Código", "Nombre del Producto", "Categoría", "Precio", "Stock"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(34);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.getTableHeader().setForeground(TEXT_MID);
        tabla.getTableHeader().setBackground(new Color(0xF8, 0xFA, 0xFC));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Align numeric to right
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tabla.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tabla.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

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

    // =========================
    // ====== WIRE EVENTS ======
    // =========================
    private void wireEvents() {
        btnRecargar.addActionListener(e -> {
            recargarTabla();
            actualizarFooter();
        });

        btnLimpiar.addActionListener(e -> limpiarFormulario());

        btnAgregar.addActionListener(e -> onAgregar());

        btnModificar.addActionListener(e -> onModificar());

        btnEliminar.addActionListener(e -> onEliminar());

        tabla.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                cargarSeleccionEnFormulario();
            }
        });
    }

    // =========================
    // ===== CRUD ACTIONS ======
    // =========================
    private void onAgregar() {
        try {
            DatosForm d = leerFormulario(true);

            Producto nuevo = new Producto(d.codigo, d.nombre, d.categoria, d.precio, d.stock);
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
            // Para modificar, el código y nombre ya vienen de la fila seleccionada (nombre está deshabilitado)
            DatosForm d = leerFormulario(false);

            productoController.modificar(d.codigo, d.categoria, d.precio, d.stock);

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

        if (op != JOptionPane.YES_OPTION) return;

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

    // =========================
    // ===== TABLE HELPERS =====
    // =========================
    private void recargarTabla() {
        modelo.setRowCount(0);

        List<Producto> lista = productoController.listar();
        for (Producto p : lista) {
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
        if (row < 0) return;

        txtCodigo.setText(String.valueOf(modelo.getValueAt(row, 0)));
        txtNombre.setText(String.valueOf(modelo.getValueAt(row, 1)));
        cmbCategoria.setSelectedItem(String.valueOf(modelo.getValueAt(row, 2)));
        txtPrecio.setText(String.valueOf(modelo.getValueAt(row, 3)));
        txtStock.setText(String.valueOf(modelo.getValueAt(row, 4)));

        // En tu controller NO se modifica nombre: lo dejamos readonly cuando ya existe
        txtCodigo.setEnabled(false);
        txtNombre.setEnabled(false);
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtNombre.setText("");
        cmbCategoria.setSelectedIndex(0);
        txtPrecio.setText("");
        txtStock.setText("");

        txtCodigo.setEnabled(true);
        txtNombre.setEnabled(true);
        tabla.clearSelection();
    }

    // =========================
    // ===== FORM PARSING ======
    // =========================
    private DatosForm leerFormulario(boolean esAgregar) {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String categoria = String.valueOf(cmbCategoria.getSelectedItem()).trim();
        String sPrecio = txtPrecio.getText().trim();
        String sStock = txtStock.getText().trim();

        if (codigo.isEmpty()) throw new IllegalArgumentException("El código es obligatorio.");
        if (esAgregar && nombre.isEmpty()) throw new IllegalArgumentException("El nombre es obligatorio.");
        if (!esAgregar && nombre.isEmpty()) {
            // al modificar está deshabilitado, pero por seguridad
            nombre = txtNombre.getText().trim();
        }

        double precio;
        int stock;

        try {
            precio = Double.parseDouble(sPrecio);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Precio inválido. Ej: 2.50");
        }

        try {
            stock = Integer.parseInt(sStock);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Stock inválido. Ej: 50");
        }

        if (precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
        if (stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");

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

    // =========================
    // ===== SMALL UI UTILS =====
    // =========================
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

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(PRIMARY);
        b.setForeground(TEXT_DARK);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 10), 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton darkButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(DARK_BTN);
        b.setForeground(Color.WHITE);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 10), 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton dangerButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(Color.WHITE);
        b.setForeground(DANGER);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xFE, 0xCACA), 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(new Color(0xF1, 0xF5, 0xF9));
        b.setForeground(TEXT_MID);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 12), 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ===== small record holder =====
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
}
