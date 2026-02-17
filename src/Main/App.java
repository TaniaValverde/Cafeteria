package Main;

import Controlador.ClienteController;
import Controlador.MesaController;
import Controlador.PedidoController;
import Controlador.ProductoController;
import Controlador.VentaController;
import java.io.IOException;
import javax.swing.SwingUtilities;
import vista.MenuPrincipal;

public class App {

    public static void main(String[] args) {

        try {
            // ===== Controllers =====
            ProductoController productoCtrl = new ProductoController("data/productos.txt");
            ClienteController clienteCtrl = new ClienteController("data/clientes.txt");
            PedidoController pedidoCtrl = new PedidoController();
            VentaController ventaCtrl = new VentaController();
            MesaController mesaCtrl = new MesaController();

            // ===== Lanzar Menú Principal =====
            SwingUtilities.invokeLater(() -> {
                MenuPrincipal menu = new MenuPrincipal(
                        pedidoCtrl,
                        productoCtrl,
                        ventaCtrl,
                        mesaCtrl
                );
                menu.setVisible(true);
            });

        } catch (IOException e) {
            System.out.println("Error de IO: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error general: " + e.getMessage());
        }
    }
}
