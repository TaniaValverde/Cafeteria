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

import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class vistaPedido extends JFrame {

    private final Pedido pedido;
    private final PedidoController pedidoController;
    private final ProductoController productoController;
    private final VentaController ventaController;
    private final MesaController mesaController;

    private final JLabel lblInfo;

    // ✅ NUEVO: buscador por nombre + lista filtrada
    private final JTextField txtBuscarProducto;
    private final DefaultListModel<String> modeloLista;
    private final JList<String> listaProductos;
    private List<Producto> productos; // cache local

    private final JTextField txtCantidad;

    private final JButton btnAgregar;
    private final JButton btnFinalizar;

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
        setSize(780, 360);
        setLocationRelativeTo(null);

        Font fuente = new Font("Arial", Font.BOLD, 16);

        lblInfo = new JLabel("Pedido: " + pedido.getCodigoPedido() + " | Tipo: " + pedido.getTipoPedido());
        lblInfo.setFont(fuente);

        // ✅ Buscador
        txtBuscarProducto = new JTextField();
        txtBuscarProducto.setFont(fuente);

        // ✅ Lista de productos (filtrada)
        modeloLista = new DefaultListModel<>();
        listaProductos = new JList<>(modeloLista);
        listaProductos.setFont(fuente);
        listaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Doble click = agregar
        listaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    agregarProducto();
                }
            }
        });

        txtCantidad = new JTextField();
        txtCantidad.setFont(fuente);

        btnAgregar = new JButton("Agregar Producto");
        btnAgregar.setFont(fuente);
        btnAgregar.addActionListener(e -> agregarProducto());

        btnFinalizar = new JButton("Finalizar Pedido");
        btnFinalizar.setFont(fuente);
        btnFinalizar.addActionListener(e -> finalizarPedido());

        // Cargar lista inicial
        cargarProductos();
        refrescarListaConFiltro("");

        // Filtrar al escribir
        txtBuscarProducto.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }

            private void filtrar() {
                refrescarListaConFiltro(txtBuscarProducto.getText());
            }
        });

        // Layout (5 filas x 2 columnas)
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(lblInfo);
        panel.add(new JLabel(""));

        panel.add(new JLabel("Buscar producto (por nombre):"));
        panel.add(txtBuscarProducto);

        panel.add(new JLabel("Selecciona producto:"));
        panel.add(new JScrollPane(listaProductos));

        panel.add(new JLabel("Cantidad:"));
        panel.add(txtCantidad);

        panel.add(btnAgregar);
        panel.add(btnFinalizar);

        add(panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void cargarProductos() {
        // Requiere que ProductoController tenga obtenerTodos()
        this.productos = new ArrayList<>(productoController.listar());

        if (productos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay productos cargados.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            btnAgregar.setEnabled(false);
            txtBuscarProducto.setEnabled(false);
        }
    }

    private void refrescarListaConFiltro(String filtro) {
        modeloLista.clear();

        String f = (filtro == null) ? "" : filtro.trim().toLowerCase();

        for (Producto p : productos) {
            String nombre = p.getNombre();
            if (f.isEmpty() || nombre.toLowerCase().contains(f)) {
                modeloLista.addElement(nombre);
            }
        }

        if (modeloLista.size() > 0) {
            listaProductos.setSelectedIndex(0);
        }
    }

    private Producto obtenerProductoSeleccionado() {
        String nombreSeleccionado = listaProductos.getSelectedValue();
        if (nombreSeleccionado == null) return null;

        for (Producto p : productos) {
            if (p.getNombre().equalsIgnoreCase(nombreSeleccionado)) {
                return p;
            }
        }
        return null;
    }

    private void agregarProducto() {
        try {
            Producto producto = obtenerProductoSeleccionado();
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

            JOptionPane.showMessageDialog(this, "Producto agregado al pedido ✅");
            txtCantidad.setText("");
            txtBuscarProducto.requestFocus();

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

            if (!paraLlevar) {
                mesaController.liberarMesa(numMesa);
            }

            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al finalizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
}
