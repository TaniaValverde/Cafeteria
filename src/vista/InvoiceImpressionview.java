package vista;

import util.ImpresionUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

/**
 * Printable invoice view intended for basic ticket-style printing.
 *
 * <p>This class contains only UI code (Swing components and event handlers).</p>
 */
public class InvoiceImpressionview extends JDialog {

    private final JTextArea area;
    private boolean impresionConfirmada = false;

    private final JButton btnImprimir;
    private final JLabel lblEstado;

    /**
     * Creates the view and initializes its Swing components.
     */
    public InvoiceImpressionview(Frame owner, String textoFactura) {
        super(owner, "Impresión de factura", true);

        // Área de texto (estilo ticket)
        area = new JTextArea(textoFactura == null ? "" : textoFactura);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setEditable(false);

        // Ticket suele verse mejor sin wrap (líneas fijas)
        area.setLineWrap(false);
        area.setWrapStyleWord(false);

        area.setBorder(new EmptyBorder(12, 12, 12, 12));
        area.setCaretPosition(0); // empieza arriba

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Estado
        lblEstado = new JLabel("Listo para imprimir");
        lblEstado.setBorder(new EmptyBorder(0, 10, 0, 10));

        // Botones
        btnImprimir = new JButton("🖨 Imprimir");
        JButton btnCerrar = new JButton("Cerrar");
        JButton btnCopiar = new JButton("Copiar");
        JButton btnGuardar = new JButton("Guardar .txt");

        btnImprimir.addActionListener(e -> imprimir());
        btnCerrar.addActionListener(e -> dispose());
        btnCopiar.addActionListener(e -> copiarAlPortapapeles());
        btnGuardar.addActionListener(e -> guardarComoTxt());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        botones.add(lblEstado);
        botones.add(btnGuardar);
        botones.add(btnCopiar);
        botones.add(btnCerrar);
        botones.add(btnImprimir);

        setLayout(new BorderLayout(10, 10));
        add(scroll, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        setSize(560, 560);
        setLocationRelativeTo(owner);
    }

    /**
     * true si imprimió (no canceló).
     */
    public boolean isImpresionConfirmada() {
        return impresionConfirmada;
    }

    private void imprimir() {
        try {
            setImprimiendo(true);

            boolean ok = ImpresionUtil.imprimirTexto(area, this);

            if (!ok) {
                impresionConfirmada = false;
                JOptionPane.showMessageDialog(
                        this,
                        "Impresión cancelada. No se realizará el pago.",
                        "Impresión cancelada",
                        JOptionPane.WARNING_MESSAGE
                );
                dispose();
                return;
            }

            impresionConfirmada = true;
            JOptionPane.showMessageDialog(this, "Enviado a la impresora ✅");
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al imprimir: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            setImprimiendo(false);
        }
    }

    private void setImprimiendo(boolean imprimiendo) {
        btnImprimir.setEnabled(!imprimiendo);
        lblEstado.setText(imprimiendo ? "Imprimiendo..." : "Listo para imprimir");
        setCursor(imprimiendo
                ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                : Cursor.getDefaultCursor());
    }

    private void copiarAlPortapapeles() {
        String texto = area.getText();
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(texto), null);

        JOptionPane.showMessageDialog(this, "Factura copiada al portapapeles ✅");
    }

    private void guardarComoTxt() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar factura como .txt");
        chooser.setSelectedFile(new File("factura.txt"));

        int res = chooser.showSaveDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        try (FileWriter fw = new FileWriter(file, StandardCharsets.UTF_8)) {
            fw.write(area.getText());
            fw.flush();
            JOptionPane.showMessageDialog(this, "Guardado en:\n" + file.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al guardar: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}