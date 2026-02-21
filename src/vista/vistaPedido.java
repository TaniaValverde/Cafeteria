package vista;

import Controlador.MesaController;
import Controlador.PedidoController;
import Controlador.ProductoController;
import Controlador.VentaController;
import Model.Mesa;
import Model.Pedido;
import Model.Producto;
import Model.Venta;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class vistaPedido extends JFrame {

    // ===== Controllers =====
    private final Pedido pedido;
    private final PedidoController pedidoCtrl;          // (no lo uso mucho aquí, pero lo mantengo)
    private final ProductoController productoCtrl;
    private final VentaController ventaCtrl;
    private final MesaController mesaCtrl;
    private final MenuPrincipal menuPrincipalRef;

    // ===== Palette =====
    private static final Color BG = new Color(0xF5, 0xF7, 0xFA);
    private static final Color CARD = Color.WHITE;
    private static final Color BORDER = new Color(0xE5, 0xE7, 0xEB);
    private static final Color TEXT = new Color(0x0F, 0x17, 0x2A);
    private static final Color TEXT_MID = new Color(0x64, 0x74, 0x8B);

    private static final Color PRIMARY = new Color(0xEE, 0x9D, 0x2B);
    private static final Color NAVY = new Color(0x0B, 0x12, 0x22);
    private static final Color DANGER = new Color(0xEF, 0x44, 0x44);

    // ===== UI state =====
    private JTextField txtBuscar;
    private DefaultListModel<Producto> productosModel;
    private JList<Producto> listaProductos;

    private Producto productoSeleccionado;
    private int cantidadSeleccionada = 1;

    private JLabel lblProdNombre;
    private JLabel lblProdPrecio;
    private JLabel lblProdDesc;
    private JLabel lblCantidad;

    private JTextArea txtNotas; // (visual, no se guarda en Pedido)

    // resumen
    private JPanel resumenItemsPanel;
    private JLabel lblTotalValor;

    public vistaPedido(Pedido pedido,
                       PedidoController pedidoCtrl,
                       ProductoController productoCtrl,
                       VentaController ventaCtrl,
                       MesaController mesaCtrl,
                       MenuPrincipal menuPrincipalRef) {

        this.pedido = pedido;
        this.pedidoCtrl = pedidoCtrl;
        this.productoCtrl = productoCtrl;
        this.ventaCtrl = ventaCtrl;
        this.mesaCtrl = mesaCtrl;
        this.menuPrincipalRef = menuPrincipalRef;

        // ✅ CLAVE: sincroniza el pedido con VentaController
        // Así agregar productos y finalizar venta usarán ESTE pedido.
        this.ventaCtrl.setPedidoActual(pedido);

        initUI();
        cargarProductosInicial();
        refrescarResumen();
    }

    private void initUI() {
        setTitle("Orden #" + pedido.getCodigoPedido());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

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
        shell.add(buildContent(), BorderLayout.CENTER);

        root.add(shell, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ================= TOP BAR =================
    private JComponent buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setBackground(CARD);
        top.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xF1, 0xF5, 0xF9)),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));

        JLabel order = new JLabel("ORDEN #" + pedido.getCodigoPedido());
        order.setFont(new Font("SansSerif", Font.BOLD, 18));
        order.setForeground(TEXT);

        left.add(order);
        left.add(Box.createRigidArea(new Dimension(12, 0)));

        String pillTxt = pedido.getTipoPedido().equals(Pedido.MESA)
                ? ("MESA " + pedido.getNumeroMesa())
                : "PARA LLEVAR";

        left.add(pill(pillTxt));

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));

        JButton btnMenu = ghostButton("▦  MENÚ PRINCIPAL");
        btnMenu.addActionListener(e -> volverAtras());
        right.add(btnMenu);

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private JPanel pill(String text) {
        JPanel p = new JPanel();
        p.setBackground(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 35));
        p.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 10), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(new Color(0xB4, 0x6A, 0x07));
        p.add(l);
        return p;
    }

    // ================= MAIN CONTENT =================
    private JComponent buildContent() {
        JPanel content = new JPanel(new BorderLayout(14, 14));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(14, 14, 14, 14));

        content.add(buildLeftColumn(), BorderLayout.CENTER);
        content.add(buildRightSummary(), BorderLayout.EAST);

        return content;
    }

    // ================= LEFT =================
    private JComponent buildLeftColumn() {
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        left.add(buildSearchBar());
        left.add(Box.createRigidArea(new Dimension(0, 10)));
        left.add(buildProductsList());
        left.add(Box.createRigidArea(new Dimension(0, 12)));
        left.add(buildProductCard());
        left.add(Box.createRigidArea(new Dimension(0, 12)));
        left.add(buildQuantityRow());
        left.add(Box.createRigidArea(new Dimension(0, 12)));
        left.add(buildNotesBox());
        left.add(Box.createRigidArea(new Dimension(0, 14)));

        JButton add = solidButton("🛒  AGREGAR PRODUCTO", PRIMARY, Color.WHITE, 16, 14);
        add.setAlignmentX(Component.LEFT_ALIGNMENT);
        add.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        add.addActionListener(e -> onAgregarProducto());

        left.add(add);
        return left;
    }

    private JComponent buildSearchBar() {
        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtBuscar.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
        txtBuscar.setToolTipText("Buscar producto (Hamburguesa, bebida...)");

        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarProductos(txtBuscar.getText()); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarProductos(txtBuscar.getText()); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrarProductos(txtBuscar.getText()); }
        });

        txtBuscar.setAlignmentX(Component.LEFT_ALIGNMENT);
        return txtBuscar;
    }

    private JComponent buildProductsList() {
        productosModel = new DefaultListModel<>();
        listaProductos = new JList<>(productosModel);
        listaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaProductos.setFont(new Font("SansSerif", Font.PLAIN, 14));
        listaProductos.setFixedCellHeight(32);

        // renderer: Nombre - Precio
        listaProductos.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            String nombre = value.getNombre();
            String precio = String.format("$%.2f", value.getPrecio());

            JLabel l = new JLabel(nombre + "   —   " + precio);
            l.setOpaque(true);
            l.setBorder(new EmptyBorder(6, 10, 6, 10));
            l.setFont(new Font("SansSerif", Font.BOLD, 13));

            if (isSelected) {
                l.setBackground(new Color(0xF1, 0xF5, 0xF9));
                l.setForeground(TEXT);
            } else {
                l.setBackground(Color.WHITE);
                l.setForeground(TEXT_MID);
            }
            return l;
        });

        listaProductos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                productoSeleccionado = listaProductos.getSelectedValue();
                actualizarCardProducto(productoSeleccionado);
            }
        });

        JScrollPane sp = new JScrollPane(listaProductos);
        sp.setBorder(new LineBorder(BORDER, 1, true));
        sp.setPreferredSize(new Dimension(0, 180));
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        return sp;
    }

    private JComponent buildProductCard() {
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(14, 14, 14, 14)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblProdNombre = new JLabel("Selecciona un producto…");
        lblProdNombre.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblProdNombre.setForeground(TEXT);

        lblProdPrecio = new JLabel("$0.00", SwingConstants.RIGHT);
        lblProdPrecio.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblProdPrecio.setForeground(PRIMARY);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblProdNombre, BorderLayout.WEST);
        top.add(lblProdPrecio, BorderLayout.EAST);

        lblProdDesc = new JLabel("<html><span style='color:#64748b;'>—</span></html>");
        lblProdDesc.setBorder(new EmptyBorder(6, 0, 0, 0));

        card.add(top, BorderLayout.NORTH);
        card.add(lblProdDesc, BorderLayout.CENTER);
        return card;
    }

    private JComponent buildQuantityRow() {
        JPanel row = new JPanel(new BorderLayout(10, 10));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("CANTIDAD");
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(TEXT_MID);

        JButton minus = outlineButton("−", new Color(0,0,0,15), TEXT_MID, 18, 14);
        JButton plus  = solidButton("+", PRIMARY, Color.WHITE, 18, 14);

        lblCantidad = new JLabel(String.valueOf(cantidadSeleccionada), SwingConstants.CENTER);
        lblCantidad.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblCantidad.setOpaque(true);
        lblCantidad.setBackground(new Color(0xF8, 0xFA, 0xFC));
        lblCantidad.setBorder(new LineBorder(BORDER, 1, true));
        lblCantidad.setPreferredSize(new Dimension(80, 44));

        minus.addActionListener(e -> {
            if (cantidadSeleccionada > 1) {
                cantidadSeleccionada--;
                lblCantidad.setText(String.valueOf(cantidadSeleccionada));
            }
        });

        plus.addActionListener(e -> {
            cantidadSeleccionada++;
            lblCantidad.setText(String.valueOf(cantidadSeleccionada));
        });

        JPanel controls = new JPanel(new BorderLayout(10, 0));
        controls.setOpaque(false);
        controls.add(minus, BorderLayout.WEST);
        controls.add(lblCantidad, BorderLayout.CENTER);
        controls.add(plus, BorderLayout.EAST);

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(label);
        box.add(Box.createRigidArea(new Dimension(0, 6)));
        box.add(controls);

        row.add(box, BorderLayout.CENTER);
        return row;
    }

    private JComponent buildNotesBox() {
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("NOTAS ESPECIALES (visual)");
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(TEXT_MID);

        txtNotas = new JTextArea(4, 20);
        txtNotas.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtNotas.setLineWrap(true);
        txtNotas.setWrapStyleWord(true);
        txtNotas.setBorder(new EmptyBorder(10, 12, 10, 12));

        JScrollPane sp = new JScrollPane(txtNotas);
        sp.setBorder(new LineBorder(BORDER, 1, true));
        sp.setBackground(Color.WHITE);

        box.add(label);
        box.add(Box.createRigidArea(new Dimension(0, 6)));
        box.add(sp);
        return box;
    }

    // ================= RIGHT SUMMARY =================
    private JComponent buildRightSummary() {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(420, 0));
        card.setBackground(CARD);
        card.setBorder(new LineBorder(BORDER, 1, true));

        JLabel title = new JLabel("  🧾  RESUMEN DE PEDIDO");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(TEXT);
        title.setBorder(new EmptyBorder(14, 14, 14, 14));
        card.add(title, BorderLayout.NORTH);

        resumenItemsPanel = new JPanel();
        resumenItemsPanel.setBackground(CARD);
        resumenItemsPanel.setLayout(new BoxLayout(resumenItemsPanel, BoxLayout.Y_AXIS));
        resumenItemsPanel.setBorder(new EmptyBorder(10, 14, 10, 14));

        // ✅ wrapper para que el BoxLayout NO estire filas cuando hay pocos items
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(CARD);
        wrap.add(resumenItemsPanel, BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(wrap);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        card.add(sp, BorderLayout.CENTER);

        // bottom
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(NAVY);
        bottom.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);

        JLabel totalLbl = new JLabel("TOTAL");
        totalLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        totalLbl.setForeground(new Color(0x9C, 0xA3, 0xAF));

        lblTotalValor = new JLabel("$0.00", SwingConstants.RIGHT);
        lblTotalValor.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTotalValor.setForeground(PRIMARY);

        totalRow.add(totalLbl, BorderLayout.WEST);
        totalRow.add(lblTotalValor, BorderLayout.EAST);

        bottom.add(totalRow);
        bottom.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton finish = solidButton("✅  FINALIZAR PEDIDO", PRIMARY, Color.WHITE, 14, 12);
        finish.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        finish.addActionListener(e -> onFinalizar());

        JButton cancel = outlineButton("✖  CANCELAR PEDIDO", DANGER, DANGER, 14, 12);
        cancel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        cancel.addActionListener(e -> onCancelar());

        bottom.add(finish);
        bottom.add(Box.createRigidArea(new Dimension(0, 10)));
        bottom.add(cancel);

        JPanel bottomWrap = new JPanel(new BorderLayout());
        bottomWrap.setBackground(NAVY);
        bottomWrap.add(bottom, BorderLayout.CENTER);

        card.add(bottomWrap, BorderLayout.SOUTH);
        return card;
    }

    private JComponent summaryItem(String qty, String name, String sub, String price) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(new Color(0xF8, 0xFA, 0xFC));
        row.setBorder(new CompoundBorder(
                new LineBorder(new Color(0,0,0,10), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel q = new JLabel(qty);
        q.setFont(new Font("SansSerif", Font.BOLD, 12));
        q.setForeground(PRIMARY);

        JPanel mid = new JPanel();
        mid.setOpaque(false);
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));

        JLabel n = new JLabel(name);
        n.setFont(new Font("SansSerif", Font.BOLD, 13));
        n.setForeground(TEXT);

        JLabel s = new JLabel(sub == null ? "" : sub);
        s.setFont(new Font("SansSerif", Font.PLAIN, 11));
        s.setForeground(TEXT_MID);

        mid.add(n);
        if (sub != null && !sub.isBlank()) {
            mid.add(Box.createRigidArea(new Dimension(0, 2)));
            mid.add(s);
        }

        JLabel p = new JLabel(price, SwingConstants.RIGHT);
        p.setFont(new Font("SansSerif", Font.BOLD, 12));
        p.setForeground(TEXT);

        row.add(q, BorderLayout.WEST);
        row.add(mid, BorderLayout.CENTER);
        row.add(p, BorderLayout.EAST);

        return row;
    }

    // ✅ NUEVO: botón mini para cantidad (MISMO estilo para + y -)
    private JButton qtyMiniButton(String text) {
    JButton b = new JButton(text);

    b.setFont(new Font("SansSerif", Font.BOLD, 16));
    b.setForeground(TEXT_MID);
    b.setBackground(Color.WHITE);

    // 🔥 CLAVE: sin márgenes (si no, el "+" se recorta y desaparece)
    b.setMargin(new Insets(0, 0, 0, 0));

    // centrado total
    b.setHorizontalAlignment(SwingConstants.CENTER);
    b.setVerticalAlignment(SwingConstants.CENTER);
    b.setHorizontalTextPosition(SwingConstants.CENTER);
    b.setVerticalTextPosition(SwingConstants.CENTER);
    b.setIconTextGap(0);

    b.setFocusPainted(false);
    b.setFocusable(false);

    // pintura simple, LAF-proof
    b.setContentAreaFilled(true);
    b.setOpaque(true);

    b.setBorder(new LineBorder(BORDER, 1, true));

    b.setPreferredSize(new Dimension(36, 36));
    b.setMinimumSize(new Dimension(36, 36));
    b.setMaximumSize(new Dimension(36, 36));

    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    return b;
}

    // ✅ NUEVO: item editable ( + / - / eliminar )
    private JComponent summaryItemEditable(Producto p, int cant) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(new Color(0xF8, 0xFA, 0xFC));
        row.setBorder(new CompoundBorder(
                new LineBorder(new Color(0,0,0,10), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // ✅ BOTONES COMPACTOS (ahora + igual que -)
        JButton minus = qtyMiniButton("−");
        JButton plus  = qtyMiniButton("+");

        JLabel qty = new JLabel(cant + "x", SwingConstants.CENTER);
        qty.setFont(new Font("SansSerif", Font.BOLD, 12));
        qty.setOpaque(true);
        qty.setBackground(Color.WHITE);
        qty.setBorder(new LineBorder(BORDER, 1, true));
        qty.setPreferredSize(new Dimension(44, 36));

        JPanel qtyBox = new JPanel(new BorderLayout(6, 0));
        qtyBox.setOpaque(false);
        qtyBox.add(minus, BorderLayout.WEST);
        qtyBox.add(qty, BorderLayout.CENTER);
        qtyBox.add(plus, BorderLayout.EAST);

        JPanel mid = new JPanel();
        mid.setOpaque(false);
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));

        JLabel n = new JLabel(p.getNombre());
        n.setFont(new Font("SansSerif", Font.BOLD, 13));
        n.setForeground(TEXT);

        mid.add(n);

        double sub = p.getPrecio() * cant;

        JLabel subtotal = new JLabel(String.format("$%.2f", sub), SwingConstants.RIGHT);
        subtotal.setFont(new Font("SansSerif", Font.BOLD, 12));
        subtotal.setForeground(TEXT);

        JButton trash = outlineButton("🗑", new Color(0,0,0,12), DANGER, 14, 8);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));
        right.add(subtotal);
        right.add(Box.createRigidArea(new Dimension(10, 0)));
        right.add(trash);

        // acciones
        plus.addActionListener(e -> {
            try {
                ventaCtrl.agregarProductoAlPedido(p, 1);
                refrescarResumen();
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Stock / Pedido", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        minus.addActionListener(e -> {
            try {
                cambiarCantidadProductoEnPedido(p, -1);
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Pedido", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        trash.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar " + p.getNombre() + " del pedido?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                try {
                    eliminarProductoDelPedido(p);
                    refrescarResumen();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        row.add(qtyBox, BorderLayout.WEST);
        row.add(mid, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);

        return row;
    }

    // ✅ NUEVO: helpers para restar/eliminar (usa tu controller para que mantenga reglas/stock)
    private void cambiarCantidadProductoEnPedido(Producto p, int delta) throws Exception {
        int actual = pedido.getCantidadDeProducto(p);
        int nuevo = actual + delta;

        if (nuevo <= 0) {
            eliminarProductoDelPedido(p);
            refrescarResumen();
            return;
        }

        if (delta > 0) {
            ventaCtrl.agregarProductoAlPedido(p, delta);
        } else {
            // Requiere método en VentaController (inverso a agregar)
            ventaCtrl.quitarProductoDelPedido(p, Math.abs(delta));
        }

        refrescarResumen();
    }

    private void eliminarProductoDelPedido(Producto p) throws Exception {
        int cant = pedido.getCantidadDeProducto(p);
        if (cant <= 0) return;

        // Requiere método en VentaController (quita y devuelve stock)
        ventaCtrl.quitarProductoDelPedido(p, cant);
    }

    // ================= LOGIC =================

    private void cargarProductosInicial() {
        productosModel.clear();
        List<Producto> lista = productoCtrl.listar();
        for (Producto p : lista) productosModel.addElement(p);

        // selecciona el primero si existe
        if (!lista.isEmpty()) {
            listaProductos.setSelectedIndex(0);
        }
    }

    private void filtrarProductos(String q) {
        String query = (q == null) ? "" : q.trim().toLowerCase();
        productosModel.clear();

        for (Producto p : productoCtrl.listar()) {
            String nombre = (p.getNombre() == null) ? "" : p.getNombre().toLowerCase();
            if (nombre.contains(query)) {
                productosModel.addElement(p);
            }
        }

        if (productosModel.size() > 0) {
            listaProductos.setSelectedIndex(0);
        } else {
            productoSeleccionado = null;
            actualizarCardProducto(null);
        }
    }

    private void actualizarCardProducto(Producto p) {
        if (p == null) {
            lblProdNombre.setText("Selecciona un producto…");
            lblProdPrecio.setText("$0.00");
            lblProdDesc.setText("<html><span style='color:#64748b;'>—</span></html>");
            return;
        }

        lblProdNombre.setText(p.getNombre());
        lblProdPrecio.setText(String.format("$%.2f", p.getPrecio()));
        lblProdDesc.setText("<html><span style='color:#64748b;'>Disponible en catálogo</span></html>");
    }

    private void onAgregarProducto() {
        if (productoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto.");
            return;
        }

        try {
            // ✅ usa VentaController para que haga validar stock + descontar stock + agregar al pedidoActual
            ventaCtrl.agregarProductoAlPedido(productoSeleccionado, cantidadSeleccionada);

            // reset UI
            cantidadSeleccionada = 1;
            lblCantidad.setText("1");
            txtNotas.setText("");

            refrescarResumen();

        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Stock / Pedido", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error agregando producto:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescarResumen() {
        resumenItemsPanel.removeAll();

        double total = 0.0;

        for (Producto p : pedido.getProductos()) {
            int cant = pedido.getCantidadDeProducto(p);
            double sub = p.getPrecio() * cant;
            total += sub;

            // ✅ ahora es editable
            resumenItemsPanel.add(summaryItemEditable(p, cant));
            resumenItemsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        lblTotalValor.setText(String.format("$%.2f", total));

        resumenItemsPanel.revalidate();
        resumenItemsPanel.repaint();
    }

    private void onFinalizar() {
        try {
            boolean paraLlevar = pedido.getTipoPedido().equals(Pedido.PARA_LLEVAR);

            Mesa mesa = null;
            if (!paraLlevar) {
                mesa = mesaCtrl.obtenerMesa(pedido.getNumeroMesa());
            }

            Venta v = ventaCtrl.finalizarVenta(null, mesa, paraLlevar);

            JOptionPane.showMessageDialog(this, "Pedido finalizado. Pase a facturación.");

            menuPrincipalRef.setVisible(true);
            dispose();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar la venta:\n" + ex.getMessage());
        }
    }

    private void onCancelar() {
        try {
            // ✅ BORRA la venta pendiente asociada a este pedido
            ventaCtrl.eliminarPendientePorCodigoPedido(pedido.getCodigoPedido());

            boolean paraLlevar = pedido.getTipoPedido().equals(Pedido.PARA_LLEVAR);

            if (!paraLlevar) {
                mesaCtrl.liberarMesa(pedido.getNumeroMesa());
            }

            JOptionPane.showMessageDialog(this, "Pedido cancelado.");

            menuPrincipalRef.setVisible(true);
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cancelar:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void volverAtras() {
        // regresar sin finalizar: para mesa vuelve a mapa de mesas
        if (pedido.getTipoPedido().equals(Pedido.MESA)) {
            vistaMesas vm = new vistaMesas(pedidoCtrl, productoCtrl, ventaCtrl, mesaCtrl, menuPrincipalRef);
            vm.setVisible(true);
        } else {
            menuPrincipalRef.setVisible(true);
        }
        dispose();
    }

    // ================= BUTTONS (LAF-proof) =================
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
                new LineBorder(new Color(0,0,0,18), 1, true),
                new EmptyBorder(pad, 16, pad, 16)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton outlineButton(String text, Color borderColor, Color fg, int fontSize, int pad) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        b.setForeground(fg);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorder(new CompoundBorder(
                new LineBorder(borderColor, 2, true),
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
                new LineBorder(new Color(0,0,0,12), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}