package Main;

import Controlador.ClienteController;
import Controlador.MesaController;
import Controlador.PedidoController;
import Controlador.ProductoController;
import Controlador.VentaController;
import java.io.IOException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import vista.Menu;

public class App {

    /**
 * Application entry point.
 *
 * Initializes the MVC controllers, sets the system Look &amp; Feel, and launches
 * the main Swing UI on the Event Dispatch Thread (EDT).
 *
 * @param args command-line arguments (not used)
 */
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ignored) {}

        try {
            // ===== Controllers =====
            ProductoController productoCtrl = new ProductoController("data/productos.txt");
            ClienteController clienteCtrl = new ClienteController("data/clientes.txt");
            PedidoController pedidoCtrl = new PedidoController();
            VentaController ventaCtrl = new VentaController();
            MesaController mesaCtrl = new MesaController();

            // ===== Launch Main Menu =====
            SwingUtilities.invokeLater(() -> {
                Menu menu = new Menu(
                        pedidoCtrl,
                        productoCtrl,
                        clienteCtrl,
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