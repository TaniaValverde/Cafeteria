package Controlador;

import Model.Cliente;
import Model.Factura;
import Model.Mesa;
import Model.Pedido;
import Model.Producto;
import Model.Venta;
import Persistencia.ProductoDAO;
import Persistencia.VentaDAO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador encargado de la gestión de ventas.
 * Inicia pedidos, agrega productos, descuenta stock
 * y finaliza la venta generando factura y guardando datos.
 */
public class VentaController {

    private Pedido pedidoActual;
    private final VentaDAO ventaDAO;
    private final ProductoDAO productoDAO;

    /**
     * Constructor del controlador de ventas.
     */
    public VentaController() {
        this.ventaDAO = new VentaDAO();
        this.productoDAO = new ProductoDAO("data/productos.txt");
        // OJO: Ya NO creamos vistaPedido aquí (eso lo hace la vistaMesas)
    }

    /**
     * Inicia un nuevo pedido para mesa o para llevar.
     *
     * @param codigoPedido código del pedido
     * @param tipoPedido Pedido.MESA o Pedido.PARA_LLEVAR
     * @param numeroMesa número de mesa (1-5) o null si es para llevar
     */
    public void iniciarPedido(int codigoPedido, String tipoPedido, Integer numeroMesa) {
        try {
            pedidoActual = new Pedido(codigoPedido, tipoPedido, numeroMesa);
        } catch (Exception e) {
            System.out.println("Error al iniciar pedido: " + e.getMessage());
        }
    }

    /**
     * Agrega un producto al pedido actual y descuenta stock.
     *
     * @param producto producto a agregar
     * @param cantidad cantidad solicitada
     */
    public void agregarProductoAlPedido(Producto producto, int cantidad) {
        try {
            if (pedidoActual == null) {
                System.out.println("No hay pedido activo");
                return;
            }

            // Descuenta stock del objeto Producto
            producto.descontarStock(cantidad);

            // Agrega el producto al pedido
            pedidoActual.agregarProducto(producto, cantidad);

        } catch (Exception e) {
            System.out.println("Error al agregar producto: " + e.getMessage());
        }
    }

    /**
     * Finaliza la venta, genera factura y guarda la venta.
     * También guarda productos en archivo para persistir el stock actualizado.
     *
     * @param cliente cliente asociado
     * @param mesa mesa asociada (null si es para llevar)
     * @param paraLlevar true si es pedido para llevar
     */
    public void finalizarVenta(Cliente cliente, Mesa mesa, boolean paraLlevar) {
        try {
            if (pedidoActual == null) {
                System.out.println("No hay pedido para finalizar");
                return;
            }

            int numeroMesa = paraLlevar ? Venta.PARA_LLEVAR : mesa.getNumero();

            // Crear la venta
            Venta venta = new Venta(
                    "V-" + System.currentTimeMillis(),
                    LocalDateTime.now(),
                    numeroMesa
            );

            // Pasar productos del pedido a la venta
            for (int i = 0; i < pedidoActual.getProductos().size(); i++) {
                Producto p = pedidoActual.getProductos().get(i);
                int cantidad = pedidoActual.getCantidadDeProducto(p);
                venta.agregarLinea(p, cantidad);
            }

            // Crear factura
            Factura factura = new Factura(pedidoActual, cliente, mesa, paraLlevar);

            // 1) Guardar la venta
            ventaDAO.guardarVenta(venta);

            // 2) Guardar productos para persistir el stock actualizado
            List<Producto> productos = productoDAO.cargar();
            productoDAO.guardar(productos);

            // (Opcional) imprimir en consola para probar
            // System.out.println(factura.generarImpresion());

            // Limpiar pedido actual
            pedidoActual = null;

        } catch (IOException e) {
            System.out.println("Error al finalizar venta: " + e.getMessage());
        }
    }

    /**
     * Devuelve el pedido actual (por si otra parte del programa necesita consultarlo).
     * @return 
     */
    public Pedido getPedidoActual() {
        return pedidoActual;
    }

    /**
     * Permite asignar un pedido actual desde afuera (por ejemplo desde otra vista/controlador).
     * @param pedidoActual
     */
    public void setPedidoActual(Pedido pedidoActual) {
        this.pedidoActual = pedidoActual;
    }
}
