
package Main;

import Controlador.ClienteController;
import Controlador.MesaController;
import Controlador.ProductoController;
import Controlador.VentaController;
import Model.Bebida;
import Model.Cliente;
import Model.Comida;
import Model.Factura;
import Model.Mesa;
import Model.Pedido;
import Model.Producto;

import java.io.IOException;

public class App {

    public static void main(String[] args) {

        try {
            // ===== Controllers (cargan datos desde archivos) =====
            ProductoController productoCtrl = new ProductoController("data/productos.txt");
            ClienteController clienteCtrl = new ClienteController("data/clientes.txt");

            // ===== 1) Crear/asegurar productos =====
            // Si ya existen en archivo, se usan. Si no, se agregan.
            Producto cafe = productoCtrl.buscarPorCodigo("B01");
            if (cafe == null) {
                cafe = new Bebida("B01", "Café", "Bebida", 1200.0, 50);
                productoCtrl.agregar(cafe);
            }

            Producto sandwich = productoCtrl.buscarPorCodigo("C01");
            if (sandwich == null) {
                sandwich = new Comida("C01", "Sandwich", "Comida", 2500.0, 30, 450);
                productoCtrl.agregar(sandwich);
            }

            // ===== 2) Crear/asegurar cliente =====
            Cliente cliente = clienteCtrl.buscarPorId("CL01");
            if (cliente == null) {
                cliente = new Cliente("CL01", "Juan Pérez", "8888-9999", Cliente.TipoCliente.FRECUENTE);
                clienteCtrl.registrar(cliente);
            }

            // ===== 3) Mesa y Venta/Pedido =====
            MesaController mesaCtrl = new MesaController();
            Mesa mesa1 = mesaCtrl.obtenerMesa(1);

            VentaController ventaCtrl = new VentaController();

            // Iniciar pedido para mesa 1
            ventaCtrl.iniciarPedido(1, Pedido.MESA, 1);

            // Agregar productos al pedido (también descuenta stock en Producto)
            ventaCtrl.agregarProductoAlPedido(cafe, 2);
            ventaCtrl.agregarProductoAlPedido(sandwich, 1);

            // Asignar pedido a la mesa (cambia estado a OCUPADA)
            Pedido pedidoActual = ventaCtrl.getPedidoActual();
            mesa1.asignarPedido(pedidoActual);

            // ===== 4) Generar e imprimir factura =====
            Factura factura = new Factura(pedidoActual, cliente, mesa1, false);
            System.out.println(factura.generarImpresion());

            // ===== 5) Finalizar venta (guarda venta + persiste productos) =====
            ventaCtrl.finalizarVenta(cliente, mesa1, false);

            // Liberar mesa al final
            mesa1.liberar();
            System.out.println("Mesa liberada. Estado actual: " + mesa1.getEstado());

        } catch (IOException e) {
            System.out.println("Error de IO (archivos): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error general: " + e.getMessage());
        }
    }
}
