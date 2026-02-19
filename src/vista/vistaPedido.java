package vista;

import Controlador.MesaController;
import Controlador.PedidoController;
import Controlador.ProductoController;
import Controlador.VentaController;
import Model.Pedido;
import Model.Producto;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class vistaPedido extends JFrame {

    private Pedido pedido;

    private final PedidoController pedidoController;
    private final ProductoController productoController;
    private final VentaController ventaController;
    private final MesaController mesaController;

    private JLabel lblInfo;

    // ===== Autocomplete UI con JComboBox =====
    private JComboBox<Producto> cbProductos;
    private JTextField txtBuscarEditor; // editor del combo
    private List<Producto> productosCache = new ArrayList<>();
    private Producto productoSeleccionado = null;

    private JTextField txtCantidad;

    private JButton btnAgregar;
    private JButton btnFinalizar;
    private JButton btnCancelar;

    // Estado mesa
    private boolean mesaAsignada = false;
    private boolean cierreControlado = false;

    // ===== Teclados flotantes =====
    private AlphaKeyboard alphaKeyboard;
    private NumericKeyboard numericKeyboard;

    // ✅ Flags anti-loop / anti-eventos fantasma
    private boolean actualizandoCombo = false;
    private boolean actualizandoEditor = false;

    public vistaPedido(Pedido pedido,
                       PedidoController pedidoController,
                       ProductoController productoController,
                       VentaController ventaController,
                       MesaController mesaController) {

        this.pedidoController = pedidoController;
        this.productoController = productoController;
        this.ventaController = ventaController;
        this.mesaController = mesaController;

        // Asegurar que el pedido exista en el controller
        Pedido existente = pedidoController.buscarPedido(pedido.getCodigoPedido());
        if (existente != null) {
            this.pedido = existente;
        } else {
            this.pedido = pedidoController.crearPedido(
                    pedido.getCodigoPedido(),
                    pedido.getTipoPedido(),
                    pedido.getNumeroMesa()
            );
        }

        setTitle("Pedido");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildUI();
        cargarProductos();
        cargarComboInicialVacio(); // ✅ no muestra nada al inicio

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                hideAllKeyboards();
                if (!cierreControlado) liberarMesaSiCorresponde();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                hideAllKeyboards();
                if (!cierreControlado) liberarMesaSiCorresponde();
            }
        });
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.setBackground(new Color(0xF6F7F9));
        setContentPane(root);

        lblInfo = new JLabel(
                "Pedido: " + pedido.getCodigoPedido()
                        + " | Tipo: " + pedido.getTipoPedido()
                        + (pedido.getNumeroMesa() != null ? (" | Mesa: " + pedido.getNumeroMesa()) : "")
        );
        lblInfo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblInfo.setBorder(new EmptyBorder(0, 0, 12, 0));
        root.add(lblInfo, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(18, 18));
        content.setOpaque(false);
        root.add(content, BorderLayout.CENTER);

        content.add(buildBuscadorConCombo(), BorderLayout.CENTER);
        content.add(buildAcciones(), BorderLayout.EAST);

        alphaKeyboard = new AlphaKeyboard(this);
        numericKeyboard = new NumericKeyboard(this);
    }

    private void hideAllKeyboards() {
        if (alphaKeyboard != null) alphaKeyboard.hideKb();
        if (numericKeyboard != null) numericKeyboard.hideKb();
    }

    // ============================
    //  BUSCADOR CON JComboBox (autocomplete)
    // ============================
    private JComponent buildBuscadorConCombo() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xE2E8F0), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel titulo = new JLabel("Buscar producto");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createVerticalStrut(10));

        JPanel searchBox = new JPanel(new BorderLayout(10, 0));
        searchBox.setBackground(Color.WHITE);
        searchBox.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xCBD5E1), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        searchBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        Dimension halfWidth = new Dimension(520, 52);
        searchBox.setPreferredSize(halfWidth);
        searchBox.setMaximumSize(halfWidth);

        JLabel icon = new JLabel("🔎");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchBox.add(icon, BorderLayout.WEST);

        cbProductos = new JComboBox<>();
        cbProductos.setEditable(true);
        cbProductos.setBorder(null);
        cbProductos.setFont(new Font("SansSerif", Font.PLAIN, 18));
        cbProductos.setMaximumRowCount(8);

        // Render: solo nombre
        cbProductos.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel l = new JLabel(value == null ? "" : value.getNombre());
            l.setOpaque(true);
            l.setBorder(new EmptyBorder(8, 12, 8, 12));
            if (isSelected) {
                l.setBackground(new Color(0xE0F2FE));
                l.setForeground(new Color(0x0F172A));
            } else {
                l.setBackground(Color.WHITE);
                l.setForeground(new Color(0x111827));
            }
            return l;
        });

        // Editor del combo
        Component editorComp = cbProductos.getEditor().getEditorComponent();
        txtBuscarEditor = (JTextField) editorComp;
        txtBuscarEditor.setBorder(null);
        txtBuscarEditor.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtBuscarEditor.setToolTipText("Buscar producto por nombre...");

        ((AbstractDocument) txtBuscarEditor.getDocument()).setDocumentFilter(new OnlyLettersFilter());

        JLabel lblSel = new JLabel("Seleccionado: (ninguno)");
        lblSel.setForeground(new Color(0x64748B));
        lblSel.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblSel.setBorder(new EmptyBorder(12, 2, 0, 0));
        lblSel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ✅ Filtrado en vivo (SIN reescribir el editor)
        txtBuscarEditor.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(lblSel); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(lblSel); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(lblSel); }
        });

        // ✅ Selección real del dropdown
        cbProductos.addActionListener(e -> {
            if (actualizandoCombo) return;

            Object sel = cbProductos.getSelectedItem();
            if (sel instanceof Producto) {
                Producto p = (Producto) sel;
                productoSeleccionado = p;

                // aquí sí reescribimos porque ya es selección final
                try {
                    actualizandoEditor = true;
                    txtBuscarEditor.setText(p.getNombre());
                    txtBuscarEditor.setCaretPosition(p.getNombre().length());
                } finally {
                    actualizandoEditor = false;
                }

                lblSel.setText("Seleccionado: " + p.getNombre());
                cbProductos.hidePopup();
            }
        });

        // ✅ teclado SOLO letras al tocar el buscador
        txtBuscarEditor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (numericKeyboard != null) numericKeyboard.hideKb();
                if (alphaKeyboard != null) {
                    alphaKeyboard.attach(txtBuscarEditor);
                    alphaKeyboard.showCenteredInOwner();
                }
            }
        });

        searchBox.add(cbProductos, BorderLayout.CENTER);
        card.add(searchBox);
        card.add(lblSel);

        card.add(Box.createVerticalStrut(18));
        JLabel tip = new JLabel("Tip: escribe y selecciona de la lista. Enter o doble click agrega.");
        tip.setForeground(new Color(0x64748B));
        tip.setFont(new Font("SansSerif", Font.BOLD, 13));
        tip.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(tip);

        return card;
    }

    // ✅ Filtrado sin sobreescribir el editor
    private void filtrar(JLabel lblSel) {
        if (actualizandoEditor) return;

        productoSeleccionado = null;
        lblSel.setText("Seleccionado: (ninguno)");

        String t = txtBuscarEditor.getText();
        int caret = txtBuscarEditor.getCaretPosition();

        if (t == null || t.trim().isEmpty()) {
            actualizandoCombo = true;
            cbProductos.setModel(new DefaultComboBoxModel<>());
            actualizandoCombo = false;
            cbProductos.hidePopup();
            return;
        }

        List<Producto> matches = filtrarProductos(t);

        actualizandoCombo = true;
        DefaultComboBoxModel<Producto> model = new DefaultComboBoxModel<>();
        for (Producto p : matches) model.addElement(p);
        cbProductos.setModel(model);
        actualizandoCombo = false;

        // NO tocar el texto; solo restaurar caret si el Look&Feel lo mueve
        SwingUtilities.invokeLater(() -> {
            try {
                actualizandoEditor = true;
                int safe = Math.min(caret, txtBuscarEditor.getText().length());
                txtBuscarEditor.setCaretPosition(safe);
            } finally {
                actualizandoEditor = false;
            }

            if (!matches.isEmpty()) cbProductos.showPopup();
            else cbProductos.hidePopup();
        });
    }

    private void cargarComboInicialVacio() {
        actualizandoCombo = true;
        cbProductos.setModel(new DefaultComboBoxModel<>());
        actualizandoCombo = false;
        // ✅ NO limpiar editor aquí (evita "sobreponer")
    }

    private List<Producto> filtrarProductos(String texto) {
        String f = texto.trim().toLowerCase();
        List<Producto> res = new ArrayList<>();
        for (Producto p : productosCache) {
            String nombre = p.getNombre() == null ? "" : p.getNombre();
            if (nombre.toLowerCase().contains(f)) res.add(p);
        }
        return res;
    }

    // ============================
    //  ACCIONES
    // ============================
    private JComponent buildAcciones() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(360, 0));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xE2E8F0), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel lblCant = new JLabel("Cantidad");
        lblCant.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblCant.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtCantidad = new JTextField("1");
        txtCantidad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        txtCantidad.setFont(new Font("SansSerif", Font.BOLD, 20));
        txtCantidad.setBorder(new CompoundBorder(
                new LineBorder(new Color(0xCBD5E1), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        ((AbstractDocument) txtCantidad.getDocument()).setDocumentFilter(new OnlyDigitsFilter());

        txtCantidad.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (alphaKeyboard != null) alphaKeyboard.hideKb();
                if (numericKeyboard != null) {
                    numericKeyboard.attach(txtCantidad);
                    numericKeyboard.showCenteredInOwner();
                }
            }
        });

        btnAgregar = new JButton("Agregar Producto");
        btnAgregar.setFont(new Font("SansSerif", Font.BOLD, 20));
        btnAgregar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        btnAgregar.addActionListener(e -> agregarProducto());

        btnFinalizar = new JButton("Finalizar Pedido");
        btnFinalizar.setFont(new Font("SansSerif", Font.BOLD, 20));
        btnFinalizar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        btnFinalizar.addActionListener(e -> finalizarPedido());

        btnCancelar = new JButton("Cancelar Pedido");
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 20));
        btnCancelar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        btnCancelar.addActionListener(e -> cancelarPedido());

        JButton btnMenu = new JButton("Menú Principal");
        btnMenu.setFont(new Font("SansSerif", Font.BOLD, 20));
        btnMenu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        btnMenu.addActionListener(e -> volverAlMenu());

        card.add(lblCant);
        card.add(Box.createVerticalStrut(8));
        card.add(txtCantidad);
        card.add(Box.createVerticalStrut(18));
        card.add(btnAgregar);
        card.add(Box.createVerticalStrut(12));
        card.add(btnFinalizar);
        card.add(Box.createVerticalStrut(12));
        card.add(btnCancelar);
        card.add(Box.createVerticalStrut(12));
        card.add(btnMenu);

        return card;
    }

    private void cargarProductos() {
        productosCache = new ArrayList<>(productoController.listar());
        if (productosCache.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos cargados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            btnAgregar.setEnabled(false);
            cbProductos.setEnabled(false);
        }
    }

    private void agregarProducto() {
        try {
            hideAllKeyboards();

            Producto producto = productoSeleccionado;
            if (producto == null) {
                String typed = txtBuscarEditor.getText().trim().toLowerCase();
                for (Producto p : productosCache) {
                    if (p.getNombre() != null && p.getNombre().trim().toLowerCase().equals(typed)) {
                        producto = p;
                        break;
                    }
                }
            }

            if (producto == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un producto de la lista.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            pedidoController.agregarProductoAPedido(pedido.getCodigoPedido(), producto, cantidad);

            if (pedido.getTipoPedido().equals(Pedido.MESA) && !mesaAsignada) {
                Integer numMesa = pedido.getNumeroMesa();
                if (numMesa != null && mesaController.estaLibre(numMesa)) {
                    mesaController.asignarPedido(numMesa, pedido);
                    mesaAsignada = true;
                }
            }

            JOptionPane.showMessageDialog(this, "Producto agregado ✅");

            txtCantidad.setText("1");
            productoSeleccionado = null;

            // ✅ aquí sí limpiamos el editor a propósito (no dentro de filtrar())
            actualizandoEditor = true;
            txtBuscarEditor.setText("");
            actualizandoEditor = false;

            cargarComboInicialVacio();
            txtBuscarEditor.requestFocusInWindow();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Cantidad debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finalizarPedido() {
        hideAllKeyboards();
        JOptionPane.showMessageDialog(this, "FinalizarPedido(): integra tu lógica real aquí.");
    }

    private void cancelarPedido() {
        hideAllKeyboards();
        cierreControlado = true;
        liberarMesaSiCorresponde();
        dispose();
    }

    private void liberarMesaSiCorresponde() {
        try {
            if (pedido.getTipoPedido().equals(Pedido.MESA)) {
                Integer numMesa = pedido.getNumeroMesa();
                if (numMesa != null && !mesaController.estaLibre(numMesa)) {
                    mesaController.liberarMesa(numMesa);
                }
                mesaAsignada = false;
            }
        } catch (Exception ignored) {}
    }

    private void volverAlMenu() {
        hideAllKeyboards();
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

    // ============================
    //  FILTROS
    // ============================
    private static class OnlyLettersFilter extends DocumentFilter {
        private boolean ok(String s) { return s != null && s.matches("[\\p{L} ]*"); }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            if (ok(string)) super.insertString(fb, offset, string, attr);
            else Toolkit.getDefaultToolkit().beep();
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            if (ok(text)) super.replace(fb, offset, length, text, attrs);
            else Toolkit.getDefaultToolkit().beep();
        }
    }

    private static class OnlyDigitsFilter extends DocumentFilter {
        private boolean ok(String s) { return s != null && s.matches("[0-9]*"); }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            if (ok(string)) super.insertString(fb, offset, string, attr);
            else Toolkit.getDefaultToolkit().beep();
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) return;
            if (ok(text)) super.replace(fb, offset, length, text, attrs);
            else Toolkit.getDefaultToolkit().beep();
        }
    }

    // ============================
    //  TECLADO LETRAS (Document API)
    // ============================
    private static class AlphaKeyboard extends JDialog {
        private JTextField target;

        AlphaKeyboard(Window owner) {
            super(owner);
            setUndecorated(true);
            setAlwaysOnTop(true);
            setFocusableWindowState(false);
            setAutoRequestFocus(false);

            JPanel root = new JPanel(new BorderLayout(8, 8));
            root.setBorder(new CompoundBorder(new LineBorder(new Color(0xCBD5E1), 1, true),
                    new EmptyBorder(10, 10, 10, 10)));
            root.setBackground(new Color(0xF8FAFC));
            setContentPane(root);

            JPanel keys = new JPanel(new GridLayout(3, 10, 6, 6));
            keys.setOpaque(false);

            String[] letters = {
                    "Q","W","E","R","T","Y","U","I","O","P",
                    "A","S","D","F","G","H","J","K","L","Ñ",
                    "Z","X","C","V","B","N","M","Á","É","Í"
            };

            for (String k : letters) keys.add(keyBtn(k, () -> insert(k.toLowerCase())));

            JPanel actions = new JPanel(new GridLayout(1, 4, 6, 6));
            actions.setOpaque(false);
            actions.add(keyBtn("Espacio", () -> insert(" ")));
            actions.add(keyBtn("Borrar", this::backspace));
            actions.add(keyBtn("Limpiar", this::clear));
            actions.add(keyBtn("Cerrar", this::hideKb));

            root.add(keys, BorderLayout.CENTER);
            root.add(actions, BorderLayout.SOUTH);
            pack();
        }

        void attach(JTextField t) { target = t; }

        void showCenteredInOwner() {
            if (getOwner() == null) return;
            Rectangle o = getOwner().getBounds();
            int x = o.x + (o.width - getWidth()) / 2;
            int y = o.y + o.height - getHeight() - 70;
            setLocation(x, y);
            if (!isVisible()) setVisible(true);
        }

        void hideKb() { setVisible(false); }

        private JButton keyBtn(String text, Runnable r) {
            JButton b = new JButton(text);
            b.setFont(new Font("SansSerif", Font.BOLD, 16));
            b.setFocusPainted(false);
            b.addActionListener(e -> r.run());
            return b;
        }

        private void insert(String s) {
            if (target == null) return;
            target.requestFocusInWindow();
            int pos = target.getCaretPosition();
            try {
                Document doc = target.getDocument();
                doc.insertString(pos, s, null);
                target.setCaretPosition(pos + s.length());
            } catch (BadLocationException ex) {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        private void backspace() {
            if (target == null) return;
            target.requestFocusInWindow();
            int pos = target.getCaretPosition();
            if (pos <= 0) return;

            try {
                Document doc = target.getDocument();
                doc.remove(pos - 1, 1);
                target.setCaretPosition(pos - 1);
            } catch (BadLocationException ex) {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        private void clear() {
            if (target == null) return;
            target.requestFocusInWindow();
            try {
                Document doc = target.getDocument();
                doc.remove(0, doc.getLength());
            } catch (BadLocationException ex) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    // ============================
    //  TECLADO NUMÉRICO (Document API)
    // ============================
    private static class NumericKeyboard extends JDialog {
        private JTextField target;

        NumericKeyboard(Window owner) {
            super(owner);
            setUndecorated(true);
            setAlwaysOnTop(true);
            setFocusableWindowState(false);
            setAutoRequestFocus(false);

            JPanel root = new JPanel(new BorderLayout(8, 8));
            root.setBorder(new CompoundBorder(new LineBorder(new Color(0xCBD5E1), 1, true),
                    new EmptyBorder(10, 10, 10, 10)));
            root.setBackground(new Color(0xF8FAFC));
            setContentPane(root);

            JPanel keys = new JPanel(new GridLayout(4, 3, 6, 6));
            keys.setOpaque(false);

            String[] nums = {"7","8","9","4","5","6","1","2","3","0","⌫","Cerrar"};
            for (String n : nums) {
                if ("⌫".equals(n)) keys.add(keyBtn(n, this::backspace));
                else if ("Cerrar".equals(n)) keys.add(keyBtn(n, this::hideKb));
                else keys.add(keyBtn(n, () -> insert(n)));
            }

            JPanel actions = new JPanel(new GridLayout(1, 1, 6, 6));
            actions.setOpaque(false);
            actions.add(keyBtn("Limpiar", this::clear));

            root.add(keys, BorderLayout.CENTER);
            root.add(actions, BorderLayout.SOUTH);
            pack();
        }

        void attach(JTextField t) { target = t; }

        void showCenteredInOwner() {
            if (getOwner() == null) return;
            Rectangle o = getOwner().getBounds();
            int x = o.x + (o.width - getWidth()) / 2;
            int y = o.y + o.height - getHeight() - 70;
            setLocation(x, y);
            if (!isVisible()) setVisible(true);
        }

        void hideKb() { setVisible(false); }

        private JButton keyBtn(String text, Runnable r) {
            JButton b = new JButton(text);
            b.setFont(new Font("SansSerif", Font.BOLD, 16));
            b.setFocusPainted(false);
            b.addActionListener(e -> r.run());
            return b;
        }

        private void insert(String s) {
            if (target == null) return;
            target.requestFocusInWindow();
            int pos = target.getCaretPosition();
            try {
                Document doc = target.getDocument();
                doc.insertString(pos, s, null);
                target.setCaretPosition(pos + s.length());
            } catch (BadLocationException ex) {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        private void backspace() {
            if (target == null) return;
            target.requestFocusInWindow();
            int pos = target.getCaretPosition();
            if (pos <= 0) return;

            try {
                Document doc = target.getDocument();
                doc.remove(pos - 1, 1);
                target.setCaretPosition(pos - 1);
            } catch (BadLocationException ex) {
                Toolkit.getDefaultToolkit().beep();
            }
        }

        private void clear() {
            if (target == null) return;
            target.requestFocusInWindow();
            try {
                Document doc = target.getDocument();
                doc.remove(0, doc.getLength());
            } catch (BadLocationException ex) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }
}
