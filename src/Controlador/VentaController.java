/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Model.Cliente;
import Model.Factura;
import Model.Mesa;
import Model.Pedido;
import Model.Producto;
import Model.Venta;
import Persistencia.VentaDAO;
import vista.vistaPedido;

import java.time.LocalDateTime;

/**
 * Controlador encargado de la gestión de ventas.
 * Inicia pedidos, agrega productos, descuenta stock
 * y finaliza la venta generando factura.
 */
public class VentaController {

    private Pedido pedidoActual;
    private VentaDAO ventaDAO;
    private vistaPedido vista;

    /**
     * Constructor del controlador de ventas.
     */
    public VentaController() {
        ventaDAO = new VentaDAO();
        vista = new vistaPedido();
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

            // MÉTODO ESPERADO EN vistaPedido
            // vista.mostrarMensaje("Pedido iniciado correctamente");

        } catch (Exception e) {
            // vista.mostrarMensaje("Error al iniciar pedido: " + e.getMessage());
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
                // vista.mostrarMensaje("No hay pedido activo");
                return;
            }

            producto.descontarStock(cantidad);
            pedidoActual.agregarProducto(producto, cantidad);

            // vista.mostrarMensaje("Producto agregado al pedido");

        } catch (Exception e) {
            // vista.mostrarMensaje("Error al agregar producto: " + e.getMessage());
        }
    }

    /**
     * Finaliza la venta, genera factura y guarda la venta.
     *
     * @param cliente cliente asociado
     * @param mesa mesa asociada (null si es para llevar)
     * @param paraLlevar true si es pedido para llevar
     */
    public void finalizarVenta(Cliente cliente, Mesa mesa, boolean paraLlevar) {
        try {
            if (pedidoActual == null) {
                // vista.mostrarMensaje("No hay pedido para finalizar");
                return;
            }

            int numeroMesa = paraLlevar ? Venta.PARA_LLEVAR : mesa.getNumero();

            Venta venta = new Venta(
                    "V-" + System.currentTimeMillis(),
                    LocalDateTime.now(),
                    numeroMesa
            );

            // ERROR ESPERADO: depende de Pedido.getProductos()
            for (int i = 0; i < pedidoActual.getProductos().size(); i++) {
                Producto p = pedidoActual.getProductos().get(i);
                int cantidad = pedidoActual.getCantidadDeProducto(p);
                venta.agregarLinea(p, cantidad);
            }

            Factura factura = new Factura(pedidoActual, cliente, mesa, paraLlevar);

            // MÉTODO ESPERADO EN VentaDAO
            // ventaDAO.guardarVenta(venta);

            // MÉTODO ESPERADO EN vistaPedido o vistaFactura
            // vista.mostrarFactura(factura.generarImpresion());

            pedidoActual = null;

        } catch (Exception e) {
            // vista.mostrarMensaje("Error al finalizar venta: " + e.getMessage());
        }
    }
}

