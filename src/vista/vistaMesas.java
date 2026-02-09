package vista;

import Controlador.MesaController;
import Controlador.PedidoController;
import Controlador.ProductoController;
import Model.Mesa;
import Model.Pedido;

import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.*;

public class vistaMesas extends JFrame {

    private final MesaController mesaController;
    private final PedidoController pedidoController;
    private final ProductoController productoController;

    private final JButton[] botonesMesas;
    private final JButton btnParaLlevar;

    private int contadorPedidos = 1;

    public vistaMesas() throws IOException {
        this.mesaController = new MesaController();
        this.pedidoController = new PedidoController();

        // OJO: tu ProductoController pide la ruta del archivo en el constructor
        this.productoController = new ProductoController("data/productos.txt"); // :contentReference[oaicite:7]{index=7}

        this.botonesMesas = new JButton[5];

        setTitle("Vista de Mesas");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        Font fuente = new Font("Arial", Font.BOLD, 18);

        for (int i = 0; i < 5; i++) {
            int numeroMesa = i + 1;
            JButton btn = new JButton();
            btn.setFont(fuente);

            btn.addActionListener(e -> manejarClickMesa(numeroMesa));

            botonesMesas[i] = btn;
            panel.add(btn);
        }

        btnParaLlevar = new JButton("PARA LLEVAR");
        btnParaLlevar.setFont(fuente);
        btnParaLlevar.addActionListener(e -> manejarClickParaLlevar());
        panel.add(btnParaLlevar);

        add(panel);

        actualizarBotones();
    }

    private void manejarClickMesa(int numeroMesa) {
        try {
            Mesa mesa = mesaController.obtenerMesa(numeroMesa); // :contentReference[oaicite:8]{index=8}

            // Si está libre, creamos pedido y lo asignamos
            if (mesa.estaLibre()) {
                Pedido pedido = pedidoController.crearPedido(contadorPedidos, Pedido.MESA, numeroMesa); // :contentReference[oaicite:9]{index=9}
                contadorPedidos++;

                mesaController.asignarPedido(numeroMesa, pedido); // :contentReference[oaicite:10]{index=10}
            }

            // SEA LIBRE U OCUPADA: abrimos la vista de pedido con el pedidoActual
            Pedido pedidoActual = mesa.getPedidoActual();
            if (pedidoActual == null) {
                JOptionPane.showMessageDialog(this, "No hay pedido asociado a la mesa.");
                return;
            }

            vistaPedido vp = new vistaPedido(pedidoActual, pedidoController, productoController);
            vp.setVisible(true);

            actualizarBotones();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void manejarClickParaLlevar() {
        try {
            Pedido pedido = pedidoController.crearPedido(contadorPedidos, Pedido.PARA_LLEVAR, null); // :contentReference[oaicite:11]{index=11}
            contadorPedidos++;

            vistaPedido vp = new vistaPedido(pedido, pedidoController, productoController);
            vp.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarBotones() {
        for (int i = 0; i < 5; i++) {
            int numeroMesa = i + 1;
            Mesa mesa = mesaController.obtenerMesa(numeroMesa);
            botonesMesas[i].setText("MESA " + numeroMesa + " - " + mesa.getEstado());
        }
    }

    public static void main(String[] args) {
        try {
            new vistaMesas().setVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "No se pudo cargar productos.txt: " + e.getMessage());
        }
    }
}
