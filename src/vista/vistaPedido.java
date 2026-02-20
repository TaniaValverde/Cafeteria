package vista;

import Controlador.MesaController;
import Controlador.PedidoController;
import Controlador.ProductoController;
import Controlador.VentaController;
import Model.Pedido;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class vistaPedido extends JFrame {

    // ===== Controllers =====
    private final Pedido pedido;
    private final PedidoController pedidoCtrl;
    private final ProductoController productoCtrl;
    private final VentaController ventaCtrl;
    private final MesaController mesaCtrl;
    private final MenuPrincipal menuPrincipalRef;

    // ===== Palette (similar to mock) =====
    private static final Color BG = new Color(0xF5, 0xF7, 0xFA);
    private static final Color CARD = Color.WHITE;
    private static final Color BORDER = new Color(0xE5, 0xE7, 0xEB);
    private static final Color TEXT = new Color(0x0F, 0x17, 0x2A);
    private static final Color TEXT_MID = new Color(0x64, 0x74, 0x8B);

    private static final Color PRIMARY = new Color(0xEE, 0x9D, 0x2B);   // naranja
    private static final Color NAVY = new Color(0x0B, 0x12, 0x22);      // azul oscuro panel resumen
    private static final Color NAVY_2 = new Color(0x10, 0x1A, 0x33);
    private static final Color DANGER = new Color(0xEF, 0x44, 0x44);

    public vistaPedido(Pedido pedido,
                       PedidoController pedidoCtrl,
                       ProductoController productoCtrl,
                       VentaController ventaCtrl,
                       MesaController mesaCtrl,
                       MenuPrincipal menuPrincipalRef) {

        this.pedido = pedido;
        this.pedidoCtrl = pedidoCtrl;
        this.productoCtrl = productoCtrl;
        this.ventaCtrl = ventaCtrl;
        this.mesaCtrl = mesaCtrl;
        this.menuPrincipalRef = menuPrincipalRef;

        initUI();
    }

    private void initUI() {
        setTitle("Orden #" + pedido.getCodigoPedido());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(CARD);
        shell.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));

        shell.add(buildTopBar(), BorderLayout.NORTH);
        shell.add(buildContent(), BorderLayout.CENTER);

        root.add(shell, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ================= TOP BAR =================
    private JComponent buildTopBar() {
        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setBackground(CARD);
        top.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xF1, 0xF5, 0xF9)),
                new EmptyBorder(12, 14, 12, 14)
        ));

        // left: order + pill
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));

        JLabel order = new JLabel("ORDEN #" + pedido.getCodigoPedido());
        order.setFont(new Font("SansSerif", Font.BOLD, 18));
        order.setForeground(TEXT);

        left.add(order);
        left.add(Box.createRigidArea(new Dimension(12, 0)));

        String pillTxt = pedido.getTipoPedido().equals(Pedido.MESA)
                ? ("MESA " + pedido.getNumeroMesa())
                : "PARA LLEVAR";

        JPanel pill = pill(pillTxt);
        left.add(pill);

        // right: menu button + avatar
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));

        JButton btnMenu = ghostButton("▦  MENÚ PRINCIPAL");
        btnMenu.addActionListener(e -> {
            if (pedido.getTipoPedido().equals(Pedido.MESA)) {
                vistaMesas vm = new vistaMesas(pedidoCtrl, productoCtrl, ventaCtrl, mesaCtrl, menuPrincipalRef);
                vm.setVisible(true);
            } else {
                menuPrincipalRef.setVisible(true);
            }
            dispose();
        });

        right.add(btnMenu);
        right.add(Box.createRigidArea(new Dimension(10, 0)));
        right.add(avatar("JD"));

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private JPanel pill(String text) {
        JPanel p = new JPanel();
        p.setBackground(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 35));
        p.setBorder(new CompoundBorder(
                new LineBorder(new Color(0,0,0,10), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));

        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        l.setForeground(new Color(0xB4, 0x6A, 0x07)); // naranja oscuro

        p.add(l);
        return p;
    }

    private JComponent avatar(String initials) {
        JPanel a = new JPanel(new GridBagLayout());
        a.setPreferredSize(new Dimension(34, 34));
        a.setMaximumSize(new Dimension(34, 34));
        a.setBackground(new Color(0xF1, 0xF5, 0xF9));
        a.setBorder(new LineBorder(new Color(0,0,0,12), 1, true));

        JLabel t = new JLabel(initials);
        t.setFont(new Font("SansSerif", Font.BOLD, 12));
        t.setForeground(TEXT_MID);
        a.add(t);
        return a;
    }

    // ================= MAIN CONTENT (2 columns) =================
    private JComponent buildContent() {
        JPanel content = new JPanel(new BorderLayout(14, 14));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(14, 14, 14, 14));

        content.add(buildLeftColumn(), BorderLayout.CENTER);
        content.add(buildRightSummary(), BorderLayout.EAST);

        return content;
    }

    // ================= LEFT COLUMN =================
    private JComponent buildLeftColumn() {
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        left.add(searchBar());
        left.add(Box.createRigidArea(new Dimension(0, 12)));
        left.add(productCardMock());
        left.add(Box.createRigidArea(new Dimension(0, 12)));
        left.add(quantityRow());
        left.add(Box.createRigidArea(new Dimension(0, 12)));
        left.add(notesBox());
        left.add(Box.createRigidArea(new Dimension(0, 14)));

        JButton add = solidButton("🛒  AGREGAR PRODUCTO", PRIMARY, Color.WHITE, 16, 14);
        add.setAlignmentX(Component.LEFT_ALIGNMENT);
        add.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        // TODO: aquí conectas con tu lógica real de agregar items al pedido
        add.addActionListener(e -> JOptionPane.showMessageDialog(this, "Agregar producto (demo)"));

        left.add(add);

        return left;
    }

    private JComponent searchBar() {
        JTextField search = new JTextField();
        search.setFont(new Font("SansSerif", Font.PLAIN, 14));
        search.setForeground(TEXT);
        search.setBackground(Color.WHITE);
        search.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
        search.setToolTipText("Buscar producto (Hamburguesa, bebida...)");

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(search, BorderLayout.CENTER);
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        return wrap;
    }

    private JComponent productCardMock() {
        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(14, 14, 14, 14)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel name = new JLabel("Hamburguesa Clásica");
        name.setFont(new Font("SansSerif", Font.BOLD, 24));
        name.setForeground(TEXT);

        JLabel price = new JLabel("$12.50");
        price.setFont(new Font("SansSerif", Font.BOLD, 18));
        price.setForeground(PRIMARY);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(name, BorderLayout.WEST);
        top.add(price, BorderLayout.EAST);

        JLabel desc = new JLabel("<html><span style='color:#64748b;'>Carne premium, lechuga, tomate y nuestra salsa secreta...</span></html>");
        desc.setBorder(new EmptyBorder(6, 0, 0, 0));

        card.add(top, BorderLayout.NORTH);
        card.add(desc, BorderLayout.CENTER);
        return card;
    }

    private JComponent quantityRow() {
        JPanel row = new JPanel(new BorderLayout(10, 10));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("CANTIDAD");
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(TEXT_MID);

        JPanel controls = new JPanel(new BorderLayout(10, 0));
        controls.setOpaque(false);

        JButton minus = outlineButton("−", new Color(0,0,0,15), TEXT_MID, 18, 14);
        JButton plus  = solidButton("+", PRIMARY, Color.WHITE, 18, 14);

        JLabel qty = new JLabel("1", SwingConstants.CENTER);
        qty.setFont(new Font("SansSerif", Font.BOLD, 18));
        qty.setOpaque(true);
        qty.setBackground(new Color(0xF8, 0xFA, 0xFC));
        qty.setBorder(new LineBorder(BORDER, 1, true));
        qty.setPreferredSize(new Dimension(80, 44));

        JPanel mid = new JPanel(new BorderLayout());
        mid.setOpaque(false);
        mid.add(qty, BorderLayout.CENTER);

        controls.add(minus, BorderLayout.WEST);
        controls.add(mid, BorderLayout.CENTER);
        controls.add(plus, BorderLayout.EAST);

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.add(label);
        box.add(Box.createRigidArea(new Dimension(0, 6)));
        box.add(controls);

        row.add(box, BorderLayout.CENTER);
        return row;
    }

    private JComponent notesBox() {
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel("NOTAS ESPECIALES");
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(TEXT_MID);

        JTextArea area = new JTextArea(4, 20);
        area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(10, 12, 10, 12));

        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(new LineBorder(BORDER, 1, true));
        sp.setBackground(Color.WHITE);

        box.add(label);
        box.add(Box.createRigidArea(new Dimension(0, 6)));
        box.add(sp);
        return box;
    }

    // ================= RIGHT SUMMARY =================
    private JComponent buildRightSummary() {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(420, 0));
        card.setBackground(CARD);
        card.setBorder(new LineBorder(BORDER, 1, true));

        // header
        JLabel title = new JLabel("  🧾  RESUMEN DE PEDIDO");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(TEXT);
        title.setBorder(new EmptyBorder(14, 14, 14, 14));
        card.add(title, BorderLayout.NORTH);

        // items list mock
        JPanel list = new JPanel();
        list.setBackground(CARD);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(new EmptyBorder(10, 14, 10, 14));

        list.add(summaryItem("1x", "Hamburguesa", "Queso", "$12.00"));
        list.add(Box.createRigidArea(new Dimension(0, 10)));
        list.add(summaryItem("1x", "Papas Fritas", "Extra crujientes", "$8.50"));

        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);

        card.add(sp, BorderLayout.CENTER);

        // bottom navy total + buttons
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(NAVY);
        bottom.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);

        JLabel totalLbl = new JLabel("TOTAL");
        totalLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        totalLbl.setForeground(new Color(0x9C, 0xA3, 0xAF));

        JLabel totalVal = new JLabel("$20.50", SwingConstants.RIGHT);
        totalVal.setFont(new Font("SansSerif", Font.BOLD, 28));
        totalVal.setForeground(PRIMARY);

        totalRow.add(totalLbl, BorderLayout.WEST);
        totalRow.add(totalVal, BorderLayout.EAST);

        bottom.add(totalRow);
        bottom.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton finish = solidButton("✅  FINALIZAR PEDIDO", PRIMARY, Color.WHITE, 14, 12);
        finish.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        finish.addActionListener(e -> {
            if (pedido.getTipoPedido().equals(Pedido.MESA)) {
                mesaCtrl.liberarMesa(pedido.getNumeroMesa());
            }
            JOptionPane.showMessageDialog(this, "Pedido finalizado correctamente");
            menuPrincipalRef.setVisible(true);
            dispose();
        });

        JButton cancel = outlineButton("✖  CANCELAR PEDIDO", DANGER, DANGER, 14, 12);
        cancel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        cancel.addActionListener(e -> {
            if (pedido.getTipoPedido().equals(Pedido.MESA)) {
                mesaCtrl.liberarMesa(pedido.getNumeroMesa());
            }
            JOptionPane.showMessageDialog(this, "Pedido cancelado");
            menuPrincipalRef.setVisible(true);
            dispose();
        });

        bottom.add(finish);
        bottom.add(Box.createRigidArea(new Dimension(0, 10)));
        bottom.add(cancel);

        JPanel bottomWrap = new JPanel(new BorderLayout());
        bottomWrap.setBackground(NAVY);
        bottomWrap.add(bottom, BorderLayout.CENTER);

        card.add(bottomWrap, BorderLayout.SOUTH);

        return card;
    }

    private JComponent summaryItem(String qty, String name, String sub, String price) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(new Color(0xF8, 0xFA, 0xFC));
        row.setBorder(new CompoundBorder(
                new LineBorder(new Color(0,0,0,10), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel q = new JLabel(qty);
        q.setFont(new Font("SansSerif", Font.BOLD, 12));
        q.setForeground(PRIMARY);

        JPanel mid = new JPanel();
        mid.setOpaque(false);
        mid.setLayout(new BoxLayout(mid, BoxLayout.Y_AXIS));

        JLabel n = new JLabel(name);
        n.setFont(new Font("SansSerif", Font.BOLD, 13));
        n.setForeground(TEXT);

        JLabel s = new JLabel(sub);
        s.setFont(new Font("SansSerif", Font.PLAIN, 11));
        s.setForeground(TEXT_MID);

        mid.add(n);
        mid.add(Box.createRigidArea(new Dimension(0, 2)));
        mid.add(s);

        JLabel p = new JLabel(price, SwingConstants.RIGHT);
        p.setFont(new Font("SansSerif", Font.BOLD, 12));
        p.setForeground(TEXT);

        row.add(q, BorderLayout.WEST);
        row.add(mid, BorderLayout.CENTER);
        row.add(p, BorderLayout.EAST);

        return row;
    }

    // ================= BUTTONS (LAF-proof) =================
    private JButton solidButton(String text, Color bg, Color fg, int fontSize, int pad) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        b.setForeground(fg);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0,0,0,18), 1, true),
                new EmptyBorder(pad, 16, pad, 16)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton outlineButton(String text, Color borderColor, Color fg, int fontSize, int pad) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        b.setForeground(fg);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorder(new CompoundBorder(
                new LineBorder(borderColor, 2, true),
                new EmptyBorder(pad, 16, pad, 16)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(TEXT_MID);
        b.setBackground(new Color(0xF1, 0xF5, 0xF9));
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0,0,0,12), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}