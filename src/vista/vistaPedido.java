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
import javax.swing.*;

public class vistaPedido extends JFrame {

    private final Pedido pedido;
    private final PedidoController pedidoController;
    private final ProductoController productoController;

    // NUEVO: para finalizar venta y liberar mesa
    private final VentaController ventaController;
    private final MesaController mesaController;

    private final JLabel lblInfo;
    private final JTextField txtCodigoProducto;
    private final JTextField txtCantidad;

    private final JButton btnAgregar;
    private final JButton btnFinalizar; // NUEVO

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
        setSize(550, 280);
        setLocationRelativeTo(null);

        Font fuente = new Font("Arial", Font.BOLD, 16);

        lblInfo = new JLabel("Pedido: " + pedido.getCodigoPedido() + " | Tipo: " + pedido.getTipoPedido());
        lblInfo.setFont(fuente);

        txtCodigoProducto = new JTextField();
        txtCantidad = new JTextField();

        btnAgregar = new JButton("Agregar Producto");
        btnAgregar.setFont(fuente);
        btnAgregar.addActionListener(e -> agregarProducto());

        // NUEVO BOTÓN
        btnFinalizar = new JButton("Finalizar Pedido");
        btnFinalizar.setFont(fuente);
        btnFinalizar.addActionListener(e -> finalizarPedido());

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(lblInfo);
        panel.add(new JLabel(""));

        panel.add(new JLabel("Código Producto:"));
        panel.add(txtCodigoProducto);

        panel.add(new JLabel("Cantidad:"));
        panel.add(txtCantidad);

        panel.add(btnAgregar);
        panel.add(btnFinalizar);

        add(panel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void agregarProducto() {
        try {
            String codigo = txtCodigoProducto.getText().trim();
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());

            // 1) Buscar producto
            Producto producto = productoController.buscarPorCodigo(codigo);

            // 2) Agregar al pedido
            pedidoController.agregarProductoAPedido(pedido.getCodigoPedido(), producto, cantidad);

            JOptionPane.showMessageDialog(this, "Producto agregado al pedido ✅");

            txtCodigoProducto.setText("");
            txtCantidad.setText("");

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Cantidad debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finalizarPedido() {
        try {
            // A) Validación rápida: que haya productos
            if (pedido.getProductos().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No puedes finalizar un pedido vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // B) Pedir datos del cliente (simple)
            Cliente cliente = pedirCliente();
            if (cliente == null) return; // canceló

            // C) Determinar si es para llevar o mesa
            boolean paraLlevar = pedido.getTipoPedido().equals(Pedido.PARA_LLEVAR);

            Integer numMesa = pedido.getNumeroMesa(); // null si PARA_LLEVAR
            Mesa mesa = null;

            if (!paraLlevar) {
                // Si es mesa, obtenemos el objeto Mesa desde MesaController
                mesa = mesaController.obtenerMesa(numMesa);
            }

            // D) IMPORTANTE:
            // VentaController maneja "pedidoActual" internamente, entonces:
            // 1) iniciamos un pedido dentro de VentaController (misma info del pedido real)
            ventaController.iniciarPedido(pedido.getCodigoPedido(), pedido.getTipoPedido(), numMesa);

            // 2) pasamos los productos del pedido real al pedido del VentaController
            // y aquí es donde se descuenta stock (producto.descontarStock)
            for (Producto p : pedido.getProductos()) {
                int cant = pedido.getCantidadDeProducto(p);
                ventaController.agregarProductoAlPedido(p, cant);
            }

            // 3) finalizamos venta (genera factura, guarda venta, guarda stock actualizado)
            ventaController.finalizarVenta(cliente, mesa, paraLlevar);

            // E) (Opcional) Mostrar “factura” en un dialog (texto)
            // La Factura puede generarse con tu pedido real (para mostrar al usuario)
            Factura facturaPreview = new Factura(pedido, cliente, mesa, paraLlevar);
            JTextArea area = new JTextArea(facturaPreview.generarImpresion());
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Factura", JOptionPane.INFORMATION_MESSAGE);

            // F) Liberar mesa si era mesa
            if (!paraLlevar) {
                mesaController.liberarMesa(numMesa);
            }

            // G) Cerrar ventana
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al finalizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Cliente pedirCliente() {
        // Si el usuario cancela cualquier paso, devolvemos null.

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

        // Constructor de Cliente (id, nombre, telefono, tipo) :contentReference[oaicite:4]{index=4}
        return new Cliente(id.trim(), nombre.trim(), telefono.trim(), tipo);
    }
}
