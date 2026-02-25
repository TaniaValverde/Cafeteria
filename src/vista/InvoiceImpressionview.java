package vista;

import util.ImpresionUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Printable invoice view intended for basic ticket-style printing.
 */
public class InvoiceImpressionview extends JDialog {

    private final JTextArea area;
    private boolean impresionConfirmada = false;

    private final JButton btnImprimir;
    private final JLabel lblEstado;

    public InvoiceImpressionview(Frame owner, String textoFactura) {
        super(owner, "Impresión de factura", true);

        // Área tipo ticket
        area = new JTextArea(textoFactura == null ? "" : textoFactura);
        area.setFont(new Font("Monospaced", Font.PLAIN, 9));
        area.setEditable(false);
        area.setLineWrap(false);
        area.setWrapStyleWord(false);
        area.setBorder(new EmptyBorder(12, 12, 12, 12));
        area.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblEstado = new JLabel("Listo para imprimir");
        lblEstado.setBorder(new EmptyBorder(0, 10, 0, 10));

        btnImprimir = new JButton("🖨 Imprimir");
        JButton btnCerrar = new JButton("Cerrar");

        btnImprimir.addActionListener(e -> imprimir());
        btnCerrar.addActionListener(e -> dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        botones.add(lblEstado);
        botones.add(btnCerrar);
        botones.add(btnImprimir);

        setLayout(new BorderLayout(10, 10));
        add(scroll, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        setSize(520, 520);
        setLocationRelativeTo(owner);
    }

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
}