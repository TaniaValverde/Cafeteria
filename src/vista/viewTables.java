package vista;

import Controlador.ClienteController;
import Controlador.MesaController;
import Controlador.PedidoController;
import Controlador.ProductoController;
import Controlador.VentaController;
import Model.Mesa;
import Model.Pedido;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * View for managing tables and opening new orders for a selected table.
 *
 * <p>
 * This class contains only UI code (Swing components and event handlers) and
 * delegates business logic to the corresponding controllers.</p>
 */
public class viewTables extends JFrame {

    private final PedidoController pedidoCtrl;
    private final ProductoController productoCtrl;
    private final VentaController ventaCtrl;
    private final MesaController mesaCtrl;
    private final ClienteController clienteCtrl;
    private final Menu menuPrincipalRef;

    private JPanel gridMesas;

    private static final Color PRIMARY = Color.decode("#ee9d2b");
    private static final Color WHITE = Color.WHITE;
    private static final Color SLATE_100 = Color.decode("#f1f5f9");
    private static final Color SLATE_200 = Color.decode("#e2e8f0");
    private static final Color SLATE_800 = Color.decode("#1e293b");
    private static final Color SLATE_900 = Color.decode("#0f172a");
    private static final Color TEXT_MID = Color.decode("#64748b");
    private static final Color RED_500 = Color.decode("#ef4444");
    private static final Color GREEN_500 = Color.decode("#10b981");

    /**
     * Creates the view and initializes its Swing components.
     */

    public viewTables(PedidoController pedidoCtrl,
            ProductoController productoCtrl,
            VentaController ventaCtrl,
            MesaController mesaCtrl,
            ClienteController clienteCtrl,
            Menu menuPrincipalRef) {

        this.pedidoCtrl = pedidoCtrl;
        this.productoCtrl = productoCtrl;
        this.ventaCtrl = ventaCtrl;
        this.mesaCtrl = mesaCtrl;
        this.clienteCtrl = clienteCtrl;
        this.menuPrincipalRef = menuPrincipalRef;

        initUI();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                if (gridMesas != null) {
                    recargarMesas();
                }
            }
        });
    }

    private void initUI() {
        setTitle("Mapa de Mesas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        getContentPane().setBackground(SLATE_100);
        getContentPane().setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 12));
        header.setBackground(WHITE);
        header.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xF1, 0xF5, 0xF9)),
                new EmptyBorder(15, 30, 15, 30)
        ));

        JLabel title = new JLabel("MAPA DE MESAS");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(SLATE_900);

        JButton menuBtn = ghostButton("⬅ MENÚ PRINCIPAL");
        menuBtn.setPreferredSize(new Dimension(230, 45));

        menuBtn.addActionListener(e -> {
            if (menuPrincipalRef != null) {
                menuPrincipalRef.setVisible(true);
            }
            dispose();
        });

        header.add(title, BorderLayout.WEST);
        header.add(menuBtn, BorderLayout.EAST);

        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(SLATE_100);

        gridMesas = new JPanel(new GridLayout(2, 3, 40, 40));
        gridMesas.setBackground(SLATE_100);
        gridMesas.setBorder(new EmptyBorder(40, 40, 40, 40));

        recargarMesas();

        body.add(gridMesas);
        return body;
    }

    private void recargarMesas() {
        gridMesas.removeAll();

        for (int i = 1; i <= 5; i++) {
            gridMesas.add(buildMesaCard(i));
        }

        gridMesas.add(Box.createGlue());

        gridMesas.revalidate();
        gridMesas.repaint();
    }

    private JPanel buildMesaCard(int numeroMesa) {

        boolean libre = mesaCtrl.estaLibre(numeroMesa);

        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(260, 200));
        card.setBackground(WHITE);
        card.setBorder(new LineBorder(SLATE_200, 3, true));

        JLabel numero = new JLabel("MESA " + numeroMesa, SwingConstants.CENTER);
        numero.setFont(new Font("SansSerif", Font.BOLD, 32));
        numero.setForeground(SLATE_800);

        String textoEstado;
        Color colorEstado;

        if (libre) {
            textoEstado = "LIBRE";
            colorEstado = GREEN_500;
        } else {
            Mesa mesa = mesaCtrl.obtenerMesa(numeroMesa);
            Pedido p = mesa.getPedidoActual();

            if (p != null) {
                textoEstado = "OCUPADA • ORDEN #" + p.getCodigoPedido();
            } else {
                textoEstado = "OCUPADA";
            }
            colorEstado = RED_500;
        }

        JLabel estado = new JLabel(textoEstado, SwingConstants.CENTER);
        estado.setFont(new Font("SansSerif", Font.BOLD, 16));
        estado.setForeground(colorEstado);
        estado.setBorder(new EmptyBorder(0, 0, 12, 0));

        card.add(numero, BorderLayout.CENTER);
        card.add(estado, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(new LineBorder(new Color(0, 0, 0, 25), 3, true));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(new LineBorder(SLATE_200, 3, true));
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                abrirPedido(numeroMesa);
            }
        });

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return card;
    }

    private void abrirPedido(int numeroMesa) {
        try {
            Pedido pedido;

            if (mesaCtrl.estaLibre(numeroMesa)) {
                int codigo = pedidoCtrl.cantidadPedidos() + 1;
                while (pedidoCtrl.buscarPedido(codigo) != null) {
                    codigo++;
                }

                pedido = pedidoCtrl.crearPedido(codigo, Pedido.MESA, numeroMesa);
                mesaCtrl.asignarPedido(numeroMesa, pedido);

            } else {
                Mesa mesa = mesaCtrl.obtenerMesa(numeroMesa);
                pedido = mesa.getPedidoActual();

                if (pedido == null) {
                    JOptionPane.showMessageDialog(this, "No se encontró pedido para esta mesa.");
                    return;
                }
            }

            viewOrder vp = new viewOrder(
                    pedido,
                    pedidoCtrl,
                    productoCtrl,
                    ventaCtrl,
                    mesaCtrl,
                    clienteCtrl,
                    menuPrincipalRef
            );

            vp.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    viewTables.this.setVisible(true);
                    recargarMesas();
                }

                @Override
                public void windowClosing(WindowEvent e) {
                    viewTables.this.setVisible(true);
                    recargarMesas();
                }
            });

            vp.setVisible(true);

            setVisible(false);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));

        b.setBackground(new Color(0xF1, 0xF5, 0xF9));
        b.setForeground(TEXT_MID);

        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 12), 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));

        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(true);

        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
