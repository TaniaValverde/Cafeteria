package vista;

import Controlador.MesaController;
import Controlador.PedidoController;
import Controlador.ProductoController;
import Controlador.VentaController;

import java.awt.*;
import javax.swing.*;

public class MenuPrincipal extends JFrame {

    private final PedidoController pedidoCtrl;
    private final ProductoController productoCtrl;
    private final VentaController ventaCtrl;
    private final MesaController mesaCtrl;

    public MenuPrincipal(PedidoController pedidoCtrl,
                         ProductoController productoCtrl,
                         VentaController ventaCtrl,
                         MesaController mesaCtrl) {

        this.pedidoCtrl = pedidoCtrl;
        this.productoCtrl = productoCtrl;
        this.ventaCtrl = ventaCtrl;
        this.mesaCtrl = mesaCtrl;

        initUI();
    }

    private void initUI() {

        setTitle("Menú principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);

        Font fuente = new Font("Arial", Font.BOLD, 22);

        JButton btnMesas = new JButton("Abrir Mesas");
        btnMesas.setFont(fuente);
        btnMesas.setPreferredSize(new Dimension(300, 100));

        btnMesas.addActionListener(e -> abrirMesas());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.add(btnMesas);

        add(panel);
    }

    private void abrirMesas() {

        vistaMesas vm = new vistaMesas(
                pedidoCtrl,
                productoCtrl,
                ventaCtrl,
                mesaCtrl
        );

        vm.setVisible(true);
        this.dispose(); // cierra menú
    }
}
