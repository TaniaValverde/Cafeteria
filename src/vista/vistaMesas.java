package vista;

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

public class vistaMesas extends JFrame {

    private final PedidoController pedidoCtrl;
    private final ProductoController productoCtrl;
    private final VentaController ventaCtrl;
    private final MesaController mesaCtrl;
    private final MenuPrincipal menuPrincipalRef;

    // ===== Palette (similar to your vistaMesas + vistaProducto style) =====
    private static final Color PRIMARY = Color.decode("#ee9d2b");          // naranja
    private static final Color WHITE = Color.WHITE;
    private static final Color SLATE_100 = Color.decode("#f1f5f9");
    private static final Color SLATE_200 = Color.decode("#e2e8f0");
    private static final Color SLATE_800 = Color.decode("#1e293b");
    private static final Color SLATE_900 = Color.decode("#0f172a");
    private static final Color TEXT_MID = Color.decode("#64748b");         // slate-500-ish
    private static final Color BORDER = Color.decode("#e5e7eb");           // similar to vistaProducto
    private static final Color RED_500 = Color.decode("#ef4444");
    private static final Color GREEN_500 = Color.decode("#10b981");

    public vistaMesas(PedidoController pedidoCtrl,
                      ProductoController productoCtrl,
                      VentaController ventaCtrl,
                      MesaController mesaCtrl,
                      MenuPrincipal menuPrincipalRef) {

        this.pedidoCtrl = pedidoCtrl;
        this.productoCtrl = productoCtrl;
        this.ventaCtrl = ventaCtrl;
        this.mesaCtrl = mesaCtrl;
        this.menuPrincipalRef = menuPrincipalRef;

        initUI();
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

    // ================= HEADER =================
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

        // ✅ Estilo "ghostButton" (como tu vistaProducto)
        JButton menuBtn = ghostButton("⬅ MENÚ PRINCIPAL");
        menuBtn.setPreferredSize(new Dimension(230, 45)); // opcional

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

    // ================= BODY =================
    private JPanel buildBody() {

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(SLATE_100);

        JPanel grid = new JPanel(new GridLayout(2, 3, 40, 40));
        grid.setBackground(SLATE_100);
        grid.setBorder(new EmptyBorder(40, 40, 40, 40));

        for (int i = 1; i <= 5; i++) {
            grid.add(buildMesaCard(i));
        }

        body.add(grid);
        return body;
    }

    // ================= MESA CARD =================
    private JPanel buildMesaCard(int numeroMesa) {

        boolean libre = mesaCtrl.estaLibre(numeroMesa);

        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(260, 200));
        card.setBackground(WHITE);
        card.setBorder(new LineBorder(SLATE_200, 3, true));

        JLabel numero = new JLabel("MESA " + numeroMesa, SwingConstants.CENTER);
        numero.setFont(new Font("SansSerif", Font.BOLD, 32));
        numero.setForeground(SLATE_800);

        JLabel estado = new JLabel(libre ? "LIBRE" : "OCUPADA", SwingConstants.CENTER);
        estado.setFont(new Font("SansSerif", Font.BOLD, 18));
        estado.setForeground(libre ? GREEN_500 : RED_500);
        estado.setBorder(new EmptyBorder(0, 0, 12, 0));

        card.add(numero, BorderLayout.CENTER);
        card.add(estado, BorderLayout.SOUTH);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                abrirPedido(numeroMesa);
            }
        });

        return card;
    }

    // ================= ABRIR / CREAR PEDIDO =================
    private void abrirPedido(int numeroMesa) {

        try {

            if (mesaCtrl.estaLibre(numeroMesa)) {

                int codigo = pedidoCtrl.cantidadPedidos() + 1;
                while (pedidoCtrl.buscarPedido(codigo) != null) {
                    codigo++;
                }

                Pedido pedido = pedidoCtrl.crearPedido(
                        codigo,
                        Pedido.MESA,
                        numeroMesa
                );

                mesaCtrl.asignarPedido(numeroMesa, pedido);

                vistaPedido vp = new vistaPedido(
                        pedido,
                        pedidoCtrl,
                        productoCtrl,
                        ventaCtrl,
                        mesaCtrl,
                        menuPrincipalRef
                );

                vp.setVisible(true);
                dispose();

            } else {

                Mesa mesa = mesaCtrl.obtenerMesa(numeroMesa);
                Pedido pedido = mesa.getPedidoActual();

                vistaPedido vp = new vistaPedido(
                        pedido,
                        pedidoCtrl,
                        productoCtrl,
                        ventaCtrl,
                        mesaCtrl,
                        menuPrincipalRef
                );

                vp.setVisible(true);
                dispose();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ================= BUTTON STYLES (from vistaProducto reference) =================

    // ✅ Botón tipo "ghost" como en vistaProducto (fondo gris claro + borde suave)
    private JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));

        b.setBackground(new Color(0xF1, 0xF5, 0xF9));
        b.setForeground(TEXT_MID);

        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 12), 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));

        // 🔥 Claves para que SIEMPRE pinte el fondo (cualquier LookAndFeel)
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(true);

        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // (Opcional) Si algún día quieres un botón primario naranja
    @SuppressWarnings("unused")
    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);

        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 10), 1, true),
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