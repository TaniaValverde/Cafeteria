package Controlador;

import Model.Pedido;
import Model.Producto;
import java.util.ArrayList;
import java.util.List;

public class PedidoController {

    private final List<Pedido> pedidos;

    public PedidoController() {
        pedidos = new ArrayList<>();
    }

    public Pedido buscarPedido(int codigoPedido) {

        for (Pedido p : pedidos) {

            if (p.getCodigoPedido() == codigoPedido) {
                return p;
            }

        }
        return null;

    }

    public Pedido crearPedido(int codigoPedido, String tipoPedido, Integer numeroMesa) {

        if (buscarPedido(codigoPedido) != null) {

            throw new IllegalArgumentException("El pedido ya Existe");

        }
        Pedido pedido = new Pedido(codigoPedido, tipoPedido, numeroMesa);

        pedidos.add(pedido);

        return pedido;

    }

    public void agregarProductoAPedido(int codigoPedido, Producto producto, int cantidad) {

        if (buscarPedido(codigoPedido) == null) {
            throw new IllegalArgumentException("El pedido no Existe");

        }

        Pedido pedidoActual = buscarPedido(codigoPedido);

        pedidoActual.agregarProducto(producto, cantidad);

    }

}
