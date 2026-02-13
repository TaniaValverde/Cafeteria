package Controlador;

import Model.Pedido;
import Model.Producto;
import java.util.ArrayList;
import java.util.List;

public class PedidoController {

    private final List<Pedido> pedidos;

    // Constructor por defecto (producción)
    public PedidoController() {
        this.pedidos = new ArrayList<>();
    }

    // Constructor para tests (inyección de lista)
    public PedidoController(List<Pedido> pedidos) {
        if (pedidos == null) {
            throw new IllegalArgumentException("Lista de pedidos no puede ser null");
        }
        this.pedidos = pedidos;
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

        // El modelo Pedido ya valida codigo/tipo/mesa/para-llevar
        Pedido pedido = new Pedido(codigoPedido, tipoPedido, numeroMesa);
        pedidos.add(pedido);
        return pedido;
    }

    public void agregarProductoAPedido(int codigoPedido, Producto producto, int cantidad) {

        Pedido pedidoActual = buscarPedido(codigoPedido);
        if (pedidoActual == null) {
            throw new IllegalArgumentException("El pedido no Existe");
        }

        // El modelo Pedido valida producto null y cantidad <= 0
        pedidoActual.agregarProducto(producto, cantidad);
    }

  
    public int cantidadPedidos() {
        return pedidos.size();
    }
}
