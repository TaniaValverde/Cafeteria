package Main;

import Controlador.ClientController;
import Controlador.TableController;
import Controlador.OrderController;
import Controlador.ProductController;
import Controlador.SaleController;
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
            ProductController productoCtrl = new ProductController("data/productos.txt");
            ClientController clienteCtrl = new ClientController("data/clientes.txt");
            OrderController pedidoCtrl = new OrderController();
            SaleController ventaCtrl = new SaleController();
            TableController mesaCtrl = new TableController();

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