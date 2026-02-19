package vista;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * VistaFactura Muestra visualmente la factura generada por el sistema. No
 * contiene lógica de negocio (MVC).
 */
public class vistaFactura extends JFrame {

    private JTextArea areaFactura = null;
    private JButton btnImprimir;
    private JButton btnCerrar;

    // Si un controlador registra un listener externo, removemos el listener por defecto
    // para evitar doble impresión.
    private transient ActionListener defaultPrintListener;

    public vistaFactura() {
        setTitle("Factura - Cafetería UCR Sede del Sur");
        setSize(600, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        crearHeader();
        crearCuerpoFactura();
        crearBotones();
    }

    private void crearHeader() {
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(230, 128, 25));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel("Factura - Cafetería UCR");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Sede del Sur - Sistema de Gestión");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(Color.WHITE);

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        textos.add(lblTitulo);
        textos.add(lblSubtitulo);

        panelHeader.add(textos, BorderLayout.WEST);
        add(panelHeader, BorderLayout.NORTH);
    }

    private void crearCuerpoFactura() {
        areaFactura = new JTextArea();
        areaFactura.setEditable(false);
        areaFactura.setFont(new Font("Courier New", Font.PLAIN, 13));
        areaFactura.setBackground(Color.WHITE);
        areaFactura.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Texto de ejemplo (luego el controlador lo reemplaza)
        areaFactura.setText(
                "CAFETERÍA UCR - SEDE DEL SUR\n"
                + "----------------------------------\n"
                + "Factura generada correctamente\n\n"
                + "FECHA: 24/05/2024\n"
                + "HORA: 14:30\n"
                + "ORDEN: Mesa #5\n\n"
                + "PRODUCTO        CANT    SUBT\n"
                + "----------------------------------\n"
                + "Café Latte       2     ₡3000\n"
                + "Empanada         1     ₡1200\n\n"
                + "----------------------------------\n"
                + "TOTAL:                 ₡4200\n\n"
                + "¡Gracias por su visita!"
        );

        JScrollPane scroll = new JScrollPane(areaFactura);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    private void crearBotones() {
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 15, 0));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelBotones.setBackground(new Color(0xF8, 0xFA, 0xFC));

        btnImprimir = primaryButton("🖨 Imprimir Factura");
        btnCerrar = ghostButton("⬅ Menú Principal");

        // Listener por defecto: imprime lo que se ve en pantalla.
        defaultPrintListener = e -> {
            String ticket = areaFactura.getText();
            imprimirTicket(ticket, true); // true = 80mm | false = 58mm
        };
        btnImprimir.addActionListener(defaultPrintListener);

        btnCerrar.addActionListener(e -> volverAlMenu());

        panelBotones.add(btnImprimir);
        panelBotones.add(btnCerrar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    /* =====================
       MÉTODOS PARA EL CONTROLADOR
       ===================== */
    // Muestra la factura generada (ej: generarImpresion())
    public void mostrarFactura(String textoFactura) {
        areaFactura.setText(textoFactura);
    }

    public void agregarListenerImprimir(ActionListener listener) {
        if (defaultPrintListener != null) {
            btnImprimir.removeActionListener(defaultPrintListener);
            defaultPrintListener = null;
        }
        btnImprimir.addActionListener(listener);
    }

    public void agregarListenerCerrar(ActionListener listener) {
        btnCerrar.addActionListener(listener);
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

    // =========================
    // ===== IMPRESIÓN TICKET ===
    // =========================
    private void imprimirTicket(String texto, boolean es80mm) {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Factura Cafetería");

            PageFormat pf = new PageFormat();
            Paper paper = new Paper();

            // 58mm ~ 164pt, 80mm ~ 226pt
            double width = es80mm ? 226 : 164;
            double height = 1000; // alto grande (ticket largo)

            double margin = 6;
            paper.setSize(width, height);
            paper.setImageableArea(margin, margin, width - 2 * margin, height - 2 * margin);

            pf.setPaper(paper);
            pf.setOrientation(PageFormat.PORTRAIT);

            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

                Graphics2D g2 = (Graphics2D) graphics;
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                g2.setColor(Color.BLACK);

                Font font = new Font("Monospaced", Font.PLAIN, 10);
                g2.setFont(font);

                FontMetrics fm = g2.getFontMetrics();
                int lineHeight = fm.getHeight();
                int maxWidth = (int) pageFormat.getImageableWidth();

                String[] lineas = wrapTexto(texto == null ? "" : texto, fm, maxWidth);

                int y = 0;
                for (String linea : lineas) {
                    y += lineHeight;
                    g2.drawString(linea, 0, y);
                }

                return Printable.PAGE_EXISTS;
            }, pf);

            // El usuario elige impresora
            if (job.printDialog()) {
                job.print();
                JOptionPane.showMessageDialog(this, "Enviado a impresión ✅", "Impresión", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo imprimir:\n" + ex.getMessage(),
                    "Impresión", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String[] wrapTexto(String text, FontMetrics fm, int maxWidth) {
        java.util.List<String> out = new java.util.ArrayList<>();
        for (String rawLine : text.split("\\R")) {
            if (rawLine.isEmpty()) {
                out.add("");
                continue;
            }
            String line = rawLine;
            while (fm.stringWidth(line) > maxWidth) {
                int cut = line.length();
                while (cut > 0 && fm.stringWidth(line.substring(0, cut)) > maxWidth) {
                    cut--;
                }
                if (cut <= 0) break;
                out.add(line.substring(0, cut));
                line = line.substring(cut).stripLeading();
            }
            out.add(line);
        }
        return out.toArray(new String[0]);
    }

    // =========================
    // ====== BOTONES ESTILO ====
    // =========================
    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 16));
        b.setBackground(new Color(46, 125, 50));
        b.setForeground(Color.WHITE);
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 12), 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Arial", Font.BOLD, 16));
        b.setBackground(new Color(0xF1, 0xF5, 0xF9));
        b.setForeground(new Color(0x64, 0x74, 0x8B));
        b.setBorder(new CompoundBorder(
                new LineBorder(new Color(0, 0, 0, 12), 1, true),
                new EmptyBorder(12, 16, 12, 16)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
