package vista;

import Controlador.ClienteController;
import Controlador.MesaController;
import Controlador.PedidoController;
import Controlador.ProductoController;
import Controlador.VentaController;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class menuPrincipal extends JFrame {

    // ===== Controllers (MVC) =====
    private PedidoController pedidoCtrl;
    private ProductoController productoCtrl;
    private ClienteController clienteCtrl;
    private VentaController ventaCtrl;
    private MesaController mesaCtrl;

    // Paleta inspirada en tu HTML
    private static final Color PRIMARY = new Color(0x3C, 0xE6, 0x19);          // #3ce619
    private static final Color BG_LIGHT = new Color(0xFD, 0xFB, 0xF7);         // #fdfbf7
    private static final Color CAFE_BROWN = new Color(0x4A, 0x37, 0x28);       // #4a3728
    private static final Color CAFE_CREAM = new Color(0xF3, 0xEE, 0xE7);       // #f3eee7
    private static final Color BORDER_SOFT = new Color(0, 0, 0, 18);

    private final JLabel lblFechaHora = new JLabel();
    private final JLabel lblUsuario = new JLabel("Admin");
    private final JLabel lblEstado = new JLabel("Estado: Sistema Listo");

    private Timer reloj;

    // ===== Constructor vacío (opcional para pruebas rápidas) =====
    public menuPrincipal() {
        initUI();
        iniciarReloj();
    }

    // ===== Constructor MVC (el que usa App.java) =====
    public menuPrincipal(PedidoController pedidoCtrl,
                         ProductoController productoCtrl,
                         ClienteController clienteCtrl,
                         VentaController ventaCtrl,
                         MesaController mesaCtrl) {

        this.pedidoCtrl = pedidoCtrl;
        this.productoCtrl = productoCtrl;
        this.clienteCtrl = clienteCtrl;
        this.ventaCtrl = ventaCtrl;
        this.mesaCtrl = mesaCtrl;

        initUI();
        iniciarReloj();
    }

    private void initUI() {
        setTitle("Cafetería UCR Sede del Sur - Sistema de Pedidos");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 720));
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                salir();
            }
        });

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_LIGHT);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildMain(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
    }

    private void iniciarReloj() {
        reloj = new Timer(1000, e -> actualizarFechaHora());
        reloj.start();
        actualizarFechaHora();
    }

    private void actualizarFechaHora() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd 'de' MMMM, yyyy  |  hh:mm a");
        lblFechaHora.setText(now.format(fmt));
    }

    // ===========================
    // ====== ABRIR VISTAS =======
    // ===========================

    private void abrirProductos() {
        // Si ya tienes una vista de productos, cámbiala aquí:
        // new vistaProductos(this, productoCtrl).setVisible(true);
        JOptionPane.showMessageDialog(this,
                "Vista de Productos no conectada aquí.\nSi ya existe, dime el nombre del archivo/clase y el constructor.",
                "Pendiente",
                JOptionPane.WARNING_MESSAGE);
    }

    private void abrirClientes() {
        // Si ya tienes una vista de clientes, cámbiala aquí:
        // new vistaClientes(this, clienteCtrl).setVisible(true);
        JOptionPane.showMessageDialog(this,
                "Vista de Clientes no conectada aquí.\nSi ya existe, dime el nombre del archivo/clase y el constructor.",
                "Pendiente",
                JOptionPane.WARNING_MESSAGE);
    }

    private void abrirMesas() {
        // ✅ Tú ya tienes vistaMesas.java subida
        // Ajusta solo si tu constructor pide parámetros distintos
        vistaMesas vm = new vistaMesas(this, mesaCtrl, pedidoCtrl, productoCtrl);
        vm.setVisible(true);
    }

    private void abrirParaLlevar() {
        // Si tu vistaPedido soporta “para llevar” sin mesa, úsala aquí.
        // Si tu constructor es distinto, ajusta la llamada.
        vistaPedido vp = new vistaPedido(this, pedidoCtrl, productoCtrl, clienteCtrl);
        vp.setVisible(true);
    }

    private void abrirFacturacion() {
        // ✅ Tú ya tienes vistaFactura.java subida
        // Ajusta solo si tu constructor pide parámetros distintos
        vistaFactura vf = new vistaFactura(this, ventaCtrl, pedidoCtrl, mesaCtrl);
        vf.setVisible(true);
    }

    private void abrirInventario() {
        // Si ya tienes vistaInventario, ponla aquí
        JOptionPane.showMessageDialog(this,
                "Vista de Inventario no conectada aquí.\nSi ya existe, dime el nombre del archivo/clase y el constructor.",
                "Pendiente",
                JOptionPane.WARNING_MESSAGE);
    }

    private void abrirReportes() {
        // Si ya tienes vistaReportes, ponla aquí
        JOptionPane.showMessageDialog(this,
                "Vista de Reportes no conectada aquí.\nSi ya existe, dime el nombre del archivo/clase y el constructor.",
                "Pendiente",
                JOptionPane.WARNING_MESSAGE);
    }

    // ===========================
    // ====== UI DEL MENÚ ========
    // ===========================

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(255, 255, 255, 220));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_SOFT));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
        left.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel logo = new JPanel(new GridBagLayout());
        logo.setBackground(PRIMARY);
        logo.setPreferredSize(new Dimension(52, 52));
        logo.setMaximumSize(new Dimension(52, 52));
        logo.setBorder(new LineBorder(new Color(0, 0, 0, 0), 1, true));
        JLabel icon = new JLabel("🍽");
        icon.setForeground(Color.WHITE);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 26));
        logo.add(icon);

        left.add(logo);
        left.add(Box.createRigidArea(new Dimension(12, 0)));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        JLabel h1 = new JLabel("Sistema de Pedidos y Facturación");
        h1.setForeground(CAFE_BROWN);
        h1.setFont(new Font("SansSerif", Font.BOLD, 20));

        JLabel sub = new JLabel("Cafetería UCR Sede del Sur");
        sub.setForeground(PRIMARY);
        sub.setFont(new Font("SansSerif", Font.BOLD, 12));

        titles.add(h1);
        titles.add(Box.createRigidArea(new Dimension(0, 2)));
        titles.add(sub);

        left.add(titles);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));
        right.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel course = new JPanel();
        course.setOpaque(false);
        course.setLayout(new BoxLayout(course, BoxLayout.Y_AXIS));

        JLabel courseLabel = new JLabel("CURSO");
        courseLabel.setForeground(new Color(CAFE_BROWN.getRed(), CAFE_BROWN.getGreen(), CAFE_BROWN.getBlue(), 120));
        courseLabel.setFont(new Font("SansSerif", Font.BOLD, 10));

        JLabel courseName = new JLabel("IF-0004 Desarrollo de Software II");
        courseName.setForeground(CAFE_BROWN);
        courseName.setFont(new Font("SansSerif", Font.BOLD, 12));

        course.add(courseLabel);
        course.add(courseName);

        JPanel userPill = new JPanel();
        userPill.setBackground(CAFE_CREAM);
        userPill.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 20), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        userPill.setLayout(new BoxLayout(userPill, BoxLayout.X_AXIS));
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("SansSerif", Font.PLAIN, 16));
        JLabel userText = new JLabel(" " + lblUsuario.getText());
        userText.setForeground(CAFE_BROWN);
        userText.setFont(new Font("SansSerif", Font.BOLD, 12));
        userPill.add(userIcon);
        userPill.add(userText);

        right.add(course);
        right.add(Box.createRigidArea(new Dimension(18, 0)));
        right.add(new JSeparator(SwingConstants.VERTICAL) {{
            setPreferredSize(new Dimension(1, 28));
            setMaximumSize(new Dimension(1, 28));
            setForeground(new Color(0,0,0,30));
        }});
        right.add(Box.createRigidArea(new Dimension(18, 0)));
        right.add(userPill);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JComponent buildMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG_LIGHT);
        main.setBorder(new EmptyBorder(26, 26, 26, 26));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Panel de Control Principal", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(CAFE_BROWN);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));

        JLabel desc = new JLabel("Seleccione una opción para comenzar a trabajar", SwingConstants.CENTER);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        desc.setForeground(new Color(CAFE_BROWN.getRed(), CAFE_BROWN.getGreen(), CAFE_BROWN.getBlue(), 160));
        desc.setFont(new Font("SansSerif", Font.PLAIN, 14));

        top.add(title);
        top.add(Box.createRigidArea(new Dimension(0, 6)));
        top.add(desc);

        JPanel gridWrap = new JPanel(new GridLayout(2, 4, 18, 18));
        gridWrap.setOpaque(false);
        gridWrap.setBorder(new EmptyBorder(26, 0, 0, 0));

        gridWrap.add(cardButton("📦", "Gestión de Productos", "Administrar menú y precios", this::abrirProductos));
        gridWrap.add(cardButton("👥", "Gestión de Clientes", "Base de datos de clientes", this::abrirClientes));
        gridWrap.add(cardButton("🪑", "Gestión de Mesas", "Mapa de salón y estados", this::abrirMesas));
        gridWrap.add(cardButton("🛍", "Pedidos para Llevar", "Órdenes rápidas express", this::abrirParaLlevar));
        gridWrap.add(cardButton("🧾", "Facturación", "Caja y procesar pagos", this::abrirFacturacion));
        gridWrap.add(cardButton("📦", "Inventario", "Control de insumos", this::abrirInventario));
        gridWrap.add(cardButton("📊", "Reportes", "Estadísticas y cierres", this::abrirReportes));
        gridWrap.add(cardButton("🚪", "Salir", "Cerrar sesión segura", this::salir, true));

        main.add(top, BorderLayout.NORTH);
        main.add(gridWrap, BorderLayout.CENTER);
        return main;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(CAFE_BROWN);
        footer.setBorder(new EmptyBorder(10, 18, 10, 18));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));

        lblFechaHora.setForeground(CAFE_CREAM);
        lblFechaHora.setFont(new Font("SansSerif", Font.BOLD, 12));

        JLabel cal = new JLabel("📅 ");
        cal.setForeground(new Color(255,255,255,200));

        left.add(cal);
        left.add(lblFechaHora);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));

        JLabel u = new JLabel("Usuario: ");
        u.setForeground(new Color(255,255,255,160));
        u.setFont(new Font("SansSerif", Font.BOLD, 12));

        JLabel uName = new JLabel(lblUsuario.getText());
        uName.setForeground(PRIMARY);
        uName.setFont(new Font("SansSerif", Font.BOLD, 12));

        JPanel statusPill = new JPanel();
        statusPill.setOpaque(true);
        statusPill.setBackground(new Color(255,255,255,18));
        statusPill.setBorder(new CompoundBorder(
                new LineBorder(new Color(255,255,255,30), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        statusPill.setLayout(new BoxLayout(statusPill, BoxLayout.X_AXIS));

        JLabel dot = new JLabel("● ");
        dot.setForeground(PRIMARY);
        dot.setFont(new Font("SansSerif", Font.BOLD, 12));

        lblEstado.setForeground(Color.WHITE);
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 11));

        statusPill.add(dot);
        statusPill.add(lblEstado);

        right.add(u);
        right.add(uName);
        right.add(Box.createRigidArea(new Dimension(16, 0)));
        right.add(statusPill);

        footer.add(left, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);
        return footer;
    }

    private JPanel cardButton(String emoji, String title, String subtitle, Runnable action) {
        return cardButton(emoji, title, subtitle, action, false);
    }

    private JPanel cardButton(String emoji, String title, String subtitle, Runnable action, boolean danger) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 18), 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel(emoji, SwingConstants.CENTER);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        icon.setFont(new Font("SansSerif", Font.PLAIN, 44));

        JPanel iconBox = new JPanel(new GridBagLayout());
        iconBox.setOpaque(true);
        iconBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconBox.setMaximumSize(new Dimension(88, 88));
        iconBox.setPreferredSize(new Dimension(88, 88));
        iconBox.setBorder(new LineBorder(new Color(0,0,0,0), 1, true));

        if (danger) {
            iconBox.setBackground(new Color(0xFF, 0xF1, 0xF2));
        } else {
            iconBox.setBackground(new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 28));
        }
        iconBox.add(icon);

        JLabel t = new JLabel(title);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        t.setForeground(CAFE_BROWN);
        t.setFont(new Font("SansSerif", Font.BOLD, 16));

        JLabel s = new JLabel(subtitle);
        s.setAlignmentX(Component.CENTER_ALIGNMENT);
        s.setForeground(new Color(CAFE_BROWN.getRed(), CAFE_BROWN.getGreen(), CAFE_BROWN.getBlue(), 140));
        s.setFont(new Font("SansSerif", Font.PLAIN, 12));

        card.add(Box.createVerticalGlue());
        card.add(iconBox);
        card.add(Box.createRigidArea(new Dimension(0, 12)));
        card.add(t);
        card.add(Box.createRigidArea(new Dimension(0, 4)));
        card.add(s);
        card.add(Box.createVerticalGlue());

        Color normalBorder = new Color(0, 0, 0, 18);
        Color hoverBorder = danger ? new Color(220, 60, 60)
                : new Color(PRIMARY.getRed(), PRIMARY.getGreen(), PRIMARY.getBlue(), 140);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(new CompoundBorder(
                        new LineBorder(hoverBorder, 2, true),
                        new EmptyBorder(17, 17, 17, 17)
                ));
                card.repaint();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(new CompoundBorder(
                        new LineBorder(normalBorder, 1, true),
                        new EmptyBorder(18, 18, 18, 18)
                ));
                card.repaint();
            }
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                action.run();
            }
        });

        return card;
    }

    private void salir() {
        int op = JOptionPane.showConfirmDialog(this,
                "¿Deseas cerrar sesión y salir?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            if (reloj != null) reloj.stop();
            dispose();
        }
    }

    // ===== main solo para pruebas =====
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        try {
            ProductoController productoCtrl = new ProductoController("data/productos.txt");
            ClienteController clienteCtrl = new ClienteController("data/clientes.txt");
            PedidoController pedidoCtrl = new PedidoController();
            VentaController ventaCtrl = new VentaController();
            MesaController mesaCtrl = new MesaController();

            SwingUtilities.invokeLater(() -> new menuPrincipal(
                    pedidoCtrl, productoCtrl, clienteCtrl, ventaCtrl, mesaCtrl
            ).setVisible(true));

        } catch (IOException e) {
            System.out.println("Error de IO: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error general: " + e.getMessage());
        }
    }
}
