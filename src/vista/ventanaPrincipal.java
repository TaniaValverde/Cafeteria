package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Menú principal estilo "pastilla" que da acceso a las demás vistas.
 */
public class ventanaPrincipal extends JFrame {

    /**
     * ✅ CAMBIA SOLO ESTO: pon aquí las clases reales de tu paquete vista.
     * Ejemplos comunes: ProductosView, ClientesView, VentasView, PedidoView,
     * MesaView, ReporteView
     *
     * IMPORTANTE: - Cada clase debe ser JFrame (o al menos extender JFrame) -
     * Debe tener constructor vacío: public MiVista() { ... }
     */
    private static final VistaItem[] VISTAS = {
        new VistaItem("🧺", "Pedidos", vistaPedido.class),
        new VistaItem("🍽", "Mesas", vistaMesas.class),
        new VistaItem("📊", "Reportes", vistaFactura.class),};

    public ventanaPrincipal() {
        super("Cafetería - Menú");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 480);
        setLocationRelativeTo(null);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(245, 246, 248));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(26, 30, 22, 30)
        ));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 0, 18, 0);

        JLabel title = new JLabel("Menú principal");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        card.add(title, c);

        int rows = (int) Math.ceil(VISTAS.length / 2.0);
        JPanel grid = new JPanel(new GridLayout(rows, 2, 18, 18));
        grid.setOpaque(false);

        for (VistaItem item : VISTAS) {
            PillButton btn = new PillButton(item.icon + "  " + item.text);
            btn.addActionListener(e -> abrirVista(item.clazz));
            grid.add(btn);
        }

        // Relleno si queda un hueco por ser impar
        if (VISTAS.length % 2 != 0) {
            JPanel filler = new JPanel();
            filler.setOpaque(false);
            grid.add(filler);
        }

        c.gridy++;
        c.insets = new Insets(0, 0, 22, 0);
        card.add(grid, c);

        // Botón salir centrado abajo
        PillButton salir = new PillButton("⎋  Salir");
        salir.setDanger(true);
        salir.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas salir?", "Salir",
                    JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        });

        c.gridy++;
        c.insets = new Insets(0, 0, 0, 0);
        card.add(salir, c);

        root.add(card);
        return root;
    }

    private void abrirVista(Class<? extends JFrame> vistaClass) {
        try {
            JFrame v = vistaClass.getDeclaredConstructor().newInstance();
            v.setLocationRelativeTo(this);
            v.setVisible(true);

            // Oculta el menú mientras la vista está abierta
            this.setVisible(false);

            // Cuando cierran la vista, vuelve a mostrarse el menú
            v.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    ventanaPrincipal.this.setVisible(true);
                }

                @Override
                public void windowClosing(WindowEvent e) {
                    ventanaPrincipal.this.setVisible(true);
                }
            });

        } catch (NoSuchMethodException ex) {
            JOptionPane.showMessageDialog(this,
                    "La vista " + vistaClass.getSimpleName() + " NO tiene constructor vacío.\n"
                    + "Agrega: public " + vistaClass.getSimpleName() + "() { ... }",
                    "Constructor faltante", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error abriendo " + vistaClass.getSimpleName() + ":\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new ventanaPrincipal().setVisible(true));
    }

    // ---------------- Helpers ----------------
    private static class VistaItem {

        final String icon;
        final String text;
        final Class<? extends JFrame> clazz;

        VistaItem(String icon, String text, Class<? extends JFrame> clazz) {
            this.icon = icon;
            this.text = text;
            this.clazz = clazz;
        }
    }

    /**
     * Botón estilo “pastilla” similar a tu imagen.
     */
    private static class PillButton extends JButton {

        private boolean danger = false;

        PillButton(String text) {
            super(text);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(new Font("SansSerif", Font.PLAIN, 18));
            setForeground(new Color(35, 35, 35));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(270, 62));
            setMargin(new Insets(10, 18, 10, 18));
        }

        void setDanger(boolean danger) {
            this.danger = danger;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int arc = 999;

            Color fill = new Color(245, 245, 245);
            if (getModel().isRollover()) {
                fill = new Color(238, 238, 238);
            }
            if (getModel().isPressed()) {
                fill = new Color(228, 228, 228);
            }

            Color border = danger ? new Color(220, 70, 70) : new Color(220, 220, 220);

            if (danger) {
                fill = new Color(255, 245, 245);
                if (getModel().isRollover()) {
                    fill = new Color(255, 235, 235);
                }
                if (getModel().isPressed()) {
                    fill = new Color(255, 225, 225);
                }
                setForeground(new Color(170, 35, 35));
            }

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

            g2.setStroke(new BasicStroke(2f));
            g2.setColor(border);
            g2.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
