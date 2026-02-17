package vista;

import Controlador.MesaController;
import Controlador.PedidoController;
import Controlador.ProductoController;
import Controlador.VentaController;
import Model.Cliente;
import Model.Factura;
import Model.Mesa;
import Model.Pedido;
import Model.Producto;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class vistaPedido extends JFrame {

    private final Pedido pedido;
    private final PedidoController pedidoController;
    private final ProductoController productoController;
    private final VentaController ventaController;
    private final MesaController mesaController;

    private JLabel lblInfo;

    // ===== Autocomplete UI =====
    private JTextField txtBuscar;
    private List<Producto> productosCache = new ArrayList<>();
    private Producto productoSeleccionado = null;

    private JPopupMenu popup;
    private DefaultListModel<Producto> modelSugerencias;
    private JList<Producto> listSugerencias;

    private JTextField txtCantidad;

    private JButton btnAgregar;
    private JButton btnFinalizar;
    private JButton btnCancelar;

    // Estado mesa
    private boolean mesaAsignada = false;
    private boolean cierreControlado = false;

    public vistaPedido(Pedido pedido,
                       PedidoController pedidoController,
                       ProductoController productoController,
                       VentaController ventaController,
                       MesaController mesaController) {

        this.pedido = pedido;
        this.pedidoController = pedidoController;
        this.productoController = productoController;
        this.ventaController = ventaController;
        this.mesaController = mesaController;

        setTitle("Pedido");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        buildUI();
        cargarProductos();
        actualizarSugerencias(""); // inicia sin filtro

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                if (!cierreControlado) liberarMesaSiCorresponde();
            }
            @Override public void windowClosed(WindowEvent e) {
                if (!cierreControlado) liberarMesaSiCorresponde();
            }
        });
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.setBackground(new Color(0xF6F7F9));
        setContentPane(root);

        lblInfo = new JLabel(
                "Pedido: " + pedido.getCodigoPedido() +
                        " | Tipo: " + pedido.getTipoPedido() +
                        (pedido.getNumeroMesa() != null ? (" | Mesa: " + pedido.getNumeroMesa()) : "")
        );
        lblInfo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblInfo.setBorder(new EmptyBorder(0, 0, 12, 0));
        root.add(lblInfo, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 18, 18));
        content.setOpaque(false);
        root.add(content, BorderLayout.CENTER);

        content.add(buildBuscadorConPopup());
        content.add(buildAcciones());
    }

    // ============================
    //  BUSCADOR + LISTA DESPLEGABLE
    // ============================
    private JComponent buildBuscadorConPopup() {
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
                new EmptyBorder(10, 12, 10, 12)
        ));
        searchBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel icon = new JLabel("🔎");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchBox.add(icon, BorderLayout.WEST);

        txtBuscar = new JTextField();
        txtBuscar.setBorder(null);
        txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtBuscar.setToolTipText("Buscar producto por nombre...");
        searchBox.add(txtBuscar, BorderLayout.CENTER);

        card.add(searchBox);

        // Hint + selección actual
        JLabel lblSel = new JLabel("Seleccionado: (ninguno)");
        lblSel.setForeground(new Color(0x64748B));
        lblSel.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblSel.setBorder(new EmptyBorder(12, 2, 0, 0));
        lblSel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lblSel);

        // ===== Popup con JList como tu imagen =====
        popup = new JPopupMenu();
        popup.setBorder(new LineBorder(new Color(0xCBD5E1), 1, true));
        popup.setFocusable(false);

        modelSugerencias = new DefaultListModel<>();
        listSugerencias = new JList<>(modelSugerencias);
        listSugerencias.setFont(new Font("SansSerif", Font.BOLD, 18));
        listSugerencias.setFixedCellHeight(56);
        listSugerencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSugerencias.setCellRenderer(new ProductoCellRenderer());

        JScrollPane sp = new JScrollPane(listSugerencias);
        sp.setBorder(null);
        sp.setPreferredSize(new Dimension(520, 320)); // alto del dropdown (ajusta si quieres)
        popup.add(sp);

        // Mostrar/filtrar al escribir
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { onChange(); }
            @Override public void removeUpdate(DocumentEvent e) { onChange(); }
            @Override public void changedUpdate(DocumentEvent e) { onChange(); }

            private void onChange() {
                productoSeleccionado = null;
                lblSel.setText("Seleccionado: (ninguno)");
                actualizarSugerencias(txtBuscar.getText());
                mostrarPopupSiHay();
            }
        });

        // Click en sugerencia
        listSugerencias.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Producto p = listSugerencias.getSelectedValue();
                if (p == null) return;

                seleccionarProducto(p, lblSel);

                // doble click: agrega directo
                if (e.getClickCount() == 2) {
                    agregarProducto();
                }
            }
        });

        // Teclado: flechas + enter + escape
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!popup.isVisible()) {
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        mostrarPopupSiHay();
                        if (popup.isVisible() && modelSugerencias.size() > 0) {
                            listSugerencias.setSelectedIndex(0);
                            listSugerencias.requestFocusInWindow();
                        }
                    }
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    popup.setVisible(false);
                }
            }
        });

        // Dentro de la lista: Enter selecciona, Escape cierra
        listSugerencias.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Producto p = listSugerencias.getSelectedValue();
                    if (p != null) seleccionarProducto(p, lblSel);
                    txtBuscar.requestFocusInWindow();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    popup.setVisible(false);
                    txtBuscar.requestFocusInWindow();
                }
            }
        });

        // Si pierde foco, cerrar popup (con pequeño delay para permitir click)
        txtBuscar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> popup.setVisible(false));
            }
        });

        // Espacio para que se vea bonito
        card.add(Box.createVerticalStrut(18));

        JLabel tip = new JLabel("Tip: escribe y selecciona de la lista (como en móvil). Enter o doble click agrega.");
        tip.setForeground(new Color(0x64748B));
        tip.setFont(new Font("SansSerif", Font.BOLD, 13));
        tip.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(tip);

        return card;
    }

    private void mostrarPopupSiHay() {
        if (modelSugerencias.isEmpty()) {
            popup.setVisible(false);
            return;
        }

        // mostrar justo debajo del txtBuscar
        if (!popup.isVisible()) {
            popup.show(txtBuscar, 0, txtBuscar.getHeight() + 6);
        }
    }

    private void actualizarSugerencias(String texto) {
        modelSugerencias.clear();
        String f = (texto == null) ? "" : texto.trim().toLowerCase();

        // si no escribe nada, también puedes mostrar todas (como en tu imagen)
        for (Producto p : productosCache) {
            String nombre = p.getNombre() == null ? "" : p.getNombre();
            if (f.isEmpty() || nombre.toLowerCase().contains(f)) {
                modelSugerencias.addElement(p);
            }
        }

        if (!modelSugerencias.isEmpty()) {
            listSugerencias.setSelectedIndex(0);
        }
    }

    private void seleccionarProducto(Producto p, JLabel lblSel) {
        productoSeleccionado = p;
        // opcional: poner el nombre en el buscador
        txtBuscar.setText(p.getNombre());
        lblSel.setText("Seleccionado: " + p.getNombre());
        popup.setVisible(false);
    }

    // ============================
    //  ACCIONES
    // ============================
    private JComponent buildAcciones() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
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

        card.add(lblCant);
        card.add(Box.createVerticalStrut(8));
        card.add(txtCantidad);
        card.add(Box.createVerticalStrut(18));
        card.add(btnAgregar);
        card.add(Box.createVerticalStrut(12));
        card.add(btnFinalizar);
        card.add(Box.createVerticalStrut(12));
        card.add(btnCancelar);

        return card;
    }

    private void cargarProductos() {
        productosCache = new ArrayList<>(productoController.listar());

        if (productosCache.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay productos cargados.", "Aviso", JOptionPane.WARNING_MESSAGE);
            btnAgregar.setEnabled(false);
            txtBuscar.setEnabled(false);
        }
    }

    private void agregarProducto() {
        try {
            // Si no seleccionó del popup, usar el primero sugerido
            Producto producto = productoSeleccionado;
            if (producto == null && !modelSugerencias.isEmpty()) {
                producto = modelSugerencias.getElementAt(Math.max(0, listSugerencias.getSelectedIndex()));
                if (producto == null) producto = modelSugerencias.getElementAt(0);
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

            // Pedido ya existe (se crea con crearPedido desde vistaMesas)
            pedidoController.agregarProductoAPedido(pedido.getCodigoPedido(), producto, cantidad);

            // ✅ Ocupa la mesa al primer producto
            if (pedido.getTipoPedido().equals(Pedido.MESA) && !mesaAsignada) {
                Integer numMesa = pedido.getNumeroMesa();
                if (numMesa != null && mesaController.estaLibre(numMesa)) {
                    mesaController.asignarPedido(numMesa, pedido);
                    mesaAsignada = true;
                }
            }

            JOptionPane.showMessageDialog(this, "Producto agregado ✅");

            // Reset
            txtCantidad.setText("1");
            productoSeleccionado = null;
            txtBuscar.setText("");
            actualizarSugerencias("");
            txtBuscar.requestFocusInWindow();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Cantidad debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finalizarPedido() {
        try {
            if (pedido.getProductos().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No puedes finalizar un pedido vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Cliente cliente = pedirCliente();
            if (cliente == null) return;

            boolean paraLlevar = pedido.getTipoPedido().equals(Pedido.PARA_LLEVAR);

            Integer numMesa = pedido.getNumeroMesa();
            Mesa mesa = null;

            if (!paraLlevar) {
                mesa = mesaController.obtenerMesa(numMesa);
            }

            ventaController.iniciarPedido(pedido.getCodigoPedido(), pedido.getTipoPedido(), numMesa);

            for (Producto p : pedido.getProductos()) {
                int cant = pedido.getCantidadDeProducto(p);
                ventaController.agregarProductoAlPedido(p, cant);
            }

            ventaController.finalizarVenta(cliente, mesa, paraLlevar);

            Factura facturaPreview = new Factura(pedido, cliente, mesa, paraLlevar);
            JTextArea area = new JTextArea(facturaPreview.generarImpresion());
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Factura", JOptionPane.INFORMATION_MESSAGE);

            // ✅ liberar mesa al pagar
            if (!paraLlevar) {
                try { mesaController.liberarMesa(numMesa); } catch (Exception ignored) {}
                mesaAsignada = false;
            }

            cierreControlado = true;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al finalizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarPedido() {
        int op = JOptionPane.showConfirmDialog(this,
                "¿Cancelar el pedido?\nSi era en mesa, se liberará.",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (op != JOptionPane.YES_OPTION) return;

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

    private Cliente pedirCliente() {
        String id = JOptionPane.showInputDialog(this, "ID del cliente:");
        if (id == null) return null;

        String nombre = JOptionPane.showInputDialog(this, "Nombre del cliente:");
        if (nombre == null) return null;

        String telefono = JOptionPane.showInputDialog(this, "Teléfono (opcional):");
        if (telefono == null) return null;

        Object[] opciones = {"FRECUENTE", "VISITANTE"};
        int sel = JOptionPane.showOptionDialog(
                this,
                "Tipo de cliente:",
                "Tipo",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );
        if (sel == JOptionPane.CLOSED_OPTION) return null;

        Cliente.TipoCliente tipo = (sel == 0) ? Cliente.TipoCliente.FRECUENTE : Cliente.TipoCliente.VISITANTE;
        return new Cliente(id.trim(), nombre.trim(), telefono.trim(), tipo);
    }

    // ===== Renderer estilo lista móvil =====
    private static class ProductoCellRenderer extends JPanel implements ListCellRenderer<Producto> {

        private final JLabel lblTexto = new JLabel();

        ProductoCellRenderer() {
            setLayout(new BorderLayout());
            setOpaque(true);

            lblTexto.setFont(new Font("SansSerif", Font.BOLD, 18));
            lblTexto.setBorder(new EmptyBorder(10, 14, 10, 14));
            add(lblTexto, BorderLayout.CENTER);

            setBorder(new MatteBorder(0, 0, 1, 0, new Color(0xE2E8F0)));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Producto> list, Producto value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {

            String nombre = (value == null) ? "" : value.getNombre();
            lblTexto.setText(nombre.toUpperCase());

            if (isSelected) {
                setBackground(new Color(0xE0F2FE));
                lblTexto.setForeground(new Color(0x0F172A));
            } else {
                setBackground(Color.WHITE);
                lblTexto.setForeground(new Color(0x111827));
            }

            return this;
        }
    }
}
