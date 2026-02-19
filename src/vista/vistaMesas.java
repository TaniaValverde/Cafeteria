package vista;

import Controlador.MesaController;
import Controlador.PedidoController;
import Controlador.ProductoController;
import Controlador.VentaController;
import Model.Pedido;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class vistaMesas extends JFrame {

    // ===== Colors / UI =====
    private static final Color PRIMARY = new Color(0xEC5B13);
    private static final Color BG_LIGHT = new Color(0xF8F6F6);
    private static final Color FREE = new Color(0x10B981);
    private static final Color BUSY = new Color(0xF43F5E);

    // Touch 15"
    private static final int FONT_TITLE = 30;
    private static final int FONT_CARD_TITLE = 24;
    private static final int FONT_CARD_SUB = 18;
    private static final int FONT_TOP_BUTTON = 18;
    private static final int CARD_PADDING = 26;

    private final PedidoController pedidoController;
    private final ProductoController productoController;
    private final VentaController ventaController;
    private final MesaController mesaController;

    // ✅ Grid reference for refresh
    private JPanel gridMesas;

    // ✅ Cache last states to highlight changes
    private final boolean[] lastLibre = new boolean[6]; // index 1..5

    public vistaMesas(PedidoController pedidoController,
            ProductoController productoController,
            VentaController ventaController,
            MesaController mesaController) {

        this.pedidoController = pedidoController;
        this.productoController = productoController;
        this.ventaController = ventaController;
        this.mesaController = mesaController;

        initUI();

        // Estados iniciales
        for (int i = 1; i <= 5; i++) {
            lastLibre[i] = mesaController.estaLibre(i);
        }
    }

    private void initUI() {
        setTitle("Sistema de Cafetería UCR – Sede del Sur");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setMinimumSize(new Dimension(1280, 720));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_LIGHT);
        setContentPane(root);

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildMainContent(), BorderLayout.CENTER);
        root.add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private JComponent buildTopBar() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(0xE2E8F0)),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JButton btnProductos = createTopButton("Gestión de Productos");
        btnProductos.addActionListener(e -> JOptionPane.showMessageDialog(this, "Conecta aquí tu vistaProducto si deseas."));
        left.add(btnProductos);

        JButton btnActualizar = createTopButton("🔄 Actualizar");
        btnActualizar.addActionListener(e -> refrescarGridMesas(true));
        left.add(btnActualizar);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        titleRow.setOpaque(false);

        JLabel badge = new JLabel("☕");
        badge.setOpaque(true);
        badge.setBackground(PRIMARY);
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("SansSerif", Font.BOLD, 18));
        badge.setBorder(new CompoundBorder(
                new LineBorder(PRIMARY.darker(), 1, true),
                new EmptyBorder(4, 12, 4, 12)
        ));

        JLabel title = new JLabel("Sistema de Cafetería UCR");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(0x0F172A));

        titleRow.add(badge);
        titleRow.add(title);

        JLabel sub = new JLabel("SEDE DEL SUR");
        sub.setFont(new Font("SansSerif", Font.BOLD, 13));
        sub.setForeground(new Color(0x64748B));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(titleRow);
        center.add(Box.createVerticalStrut(4));
        center.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton btnMenu = createTopButton("Menú Principal");
        btnMenu.addActionListener(e -> volverAlMenu());
        right.add(btnMenu);

        JButton btnReportes = createTopButton("Reportes");
        btnReportes.addActionListener(e -> JOptionPane.showMessageDialog(this, "Abrir Reportes (pendiente)"));
        right.add(btnReportes);

        btnReportes.addActionListener(e -> JOptionPane.showMessageDialog(this, "Abrir Reportes (pendiente)"));

        top.add(left, BorderLayout.WEST);
        top.add(center, BorderLayout.CENTER);
        top.add(right, BorderLayout.EAST);

        return top;
    }

    private JButton createTopButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(new Font("SansSerif", Font.BOLD, FONT_TOP_BUTTON));
        b.setBackground(Color.WHITE);
        b.setForeground(PRIMARY);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 150), 2, true),
                new EmptyBorder(12, 18, 12, 18)
        ));
        b.setPreferredSize(new Dimension(260, 56));
        return b;
    }

    private JComponent buildMainContent() {
        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(new EmptyBorder(28, 32, 28, 32));

        JLabel h2 = new JLabel("Panel de Mesas");
        h2.setFont(new Font("SansSerif", Font.BOLD, FONT_TITLE));
        h2.setForeground(new Color(0x0F172A));
        h2.setAlignmentX(Component.CENTER_ALIGNMENT);

        main.add(h2);
        main.add(Box.createVerticalStrut(22));

        gridMesas = new JPanel(new GridLayout(2, 3, 22, 22));
        gridMesas.setOpaque(false);

        for (int i = 1; i <= 5; i++) {
            gridMesas.add(createMesaCard(i, false));
        }
        gridMesas.add(createParaLlevarCard());

        main.add(gridMesas);
        return main;
    }

    private void refrescarGridMesas(boolean conHighlight) {
        if (gridMesas == null) {
            return;
        }

        gridMesas.removeAll();

        for (int i = 1; i <= 5; i++) {
            boolean libreActual = mesaController.estaLibre(i);
            boolean cambio = (libreActual != lastLibre[i]);
            lastLibre[i] = libreActual;

            gridMesas.add(createMesaCard(i, conHighlight && cambio));
        }

        gridMesas.add(createParaLlevarCard());
        gridMesas.revalidate();
        gridMesas.repaint();
    }

    private JButton createMesaCard(int mesa, boolean resaltarCambio) {
        boolean libre = mesaController.estaLibre(mesa);
        Color accent = libre ? FREE : BUSY;

        JButton card = new JButton();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setFocusPainted(false);
        card.setOpaque(true);

        if (resaltarCambio) {
            card.setBackground(new Color(0xE0F2FE)); // resalte suave
            Timer t = new Timer(600, e -> card.setBackground(Color.WHITE));
            t.setRepeats(false);
            t.start();
        } else {
            card.setBackground(Color.WHITE);
        }

        card.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 8, 0, accent),
                new CompoundBorder(
                        new LineBorder(new Color(0xE2E8F0), 2, true),
                        new EmptyBorder(CARD_PADDING, CARD_PADDING, CARD_PADDING, CARD_PADDING)
                )
        ));

        JLabel icon = new JLabel(libre ? "🍽️" : "🍷");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 44));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel t = new JLabel("Mesa " + mesa);
        t.setFont(new Font("SansSerif", Font.BOLD, FONT_CARD_TITLE));
        t.setForeground(new Color(0x1F2937));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel s = new JLabel(libre ? "Libre" : "Ocupada");
        s.setFont(new Font("SansSerif", Font.BOLD, FONT_CARD_SUB));
        s.setForeground(accent.darker());
        s.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(icon);
        card.add(Box.createVerticalStrut(12));
        card.add(t);
        card.add(Box.createVerticalStrut(10));
        card.add(s);

        // ✅ Bloquear mesa ocupada (sin Mesa.getPedido no podemos reabrir pedido existente)
        card.addActionListener(e -> {
            if (!mesaController.estaLibre(mesa)) {
                JOptionPane.showMessageDialog(this,
                        "La mesa " + mesa + " está OCUPADA.\n"
                        + "Finaliza o cancela el pedido actual para liberarla.",
                        "Mesa ocupada",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            abrirVistaPedidoMesa(mesa);
        });

        return card;
    }

    private JButton createParaLlevarCard() {
        JButton card = new JButton();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setFocusPainted(false);
        card.setOpaque(true);
        card.setBackground(PRIMARY);

        card.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 8, 0, new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 170)),
                new EmptyBorder(CARD_PADDING, CARD_PADDING, CARD_PADDING, CARD_PADDING)
        ));

        JLabel icon = new JLabel("🛍️");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 44));
        icon.setForeground(Color.WHITE);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel t = new JLabel("Para Llevar");
        t.setFont(new Font("SansSerif", Font.BOLD, FONT_CARD_TITLE));
        t.setForeground(Color.WHITE);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel s = new JLabel("Nuevo Pedido");
        s.setFont(new Font("SansSerif", Font.BOLD, FONT_CARD_SUB));
        s.setForeground(new Color(255, 255, 255, 220));
        s.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(icon);
        card.add(Box.createVerticalStrut(12));
        card.add(t);
        card.add(Box.createVerticalStrut(10));
        card.add(s);

        card.addActionListener(e -> abrirVistaPedidoParaLlevar());
        return card;
    }

    private JComponent buildBottomBar() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(0xE2E8F0)),
                new EmptyBorder(12, 18, 12, 18)
        ));

        JLabel left = new JLabel("Operador: Admin - Sede del Sur");
        left.setForeground(new Color(0x475569));
        left.setFont(new Font("SansSerif", Font.BOLD, 14));

        bottom.add(left, BorderLayout.WEST);
        return bottom;
    }

    private void abrirVistaPedidoMesa(int numMesa) {
        try {
            int codigo = ThreadLocalRandom.current().nextInt(1, 1_000_000_000);

            // ✅ IMPORTANTE: crear pedido dentro del controller (soluciona "El pedido no Existe")
            Pedido pedido = pedidoController.crearPedido(codigo, Pedido.MESA, numMesa);

            vistaPedido vp = new vistaPedido(pedido, pedidoController, productoController, ventaController, mesaController);

            // ✅ ocultar mesas (no destruir)
            this.setVisible(false);

            // ✅ al cerrar pedido, volver a mesas y refrescar
            vp.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    vistaMesas.this.setVisible(true);
                    refrescarGridMesas(true);
                }

                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    vistaMesas.this.setVisible(true);
                    refrescarGridMesas(true);
                }
            });

            vp.setExtendedState(JFrame.MAXIMIZED_BOTH);
            vp.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error abriendo pedido de mesa:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.setVisible(true);
        }
    }

    private void abrirVistaPedidoParaLlevar() {
        try {
            int codigo = ThreadLocalRandom.current().nextInt(1, 1_000_000_000);

            // ✅ crear pedido dentro del controller
            Pedido pedido = pedidoController.crearPedido(codigo, Pedido.PARA_LLEVAR, null);

            vistaPedido vp = new vistaPedido(pedido, pedidoController, productoController, ventaController, mesaController);

            this.setVisible(false);

            vp.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    vistaMesas.this.setVisible(true);
                    refrescarGridMesas(true);
                }

                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    vistaMesas.this.setVisible(true);
                    refrescarGridMesas(true);
                }
            });

            vp.setExtendedState(JFrame.MAXIMIZED_BOTH);
            vp.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error abriendo pedido para llevar:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            this.setVisible(true);
        }
    }

    private void volverAlMenu() {
        dispose();

        SwingUtilities.invokeLater(() -> {
            for (java.awt.Frame f : java.awt.Frame.getFrames()) {
                if (f instanceof JFrame && f.isVisible()
                        && f.getTitle() != null
                        && f.getTitle().contains("Cafetería UCR")) {
                    f.toFront();
                    f.requestFocus();
                    break;
                }
            }
        });
    }

}
