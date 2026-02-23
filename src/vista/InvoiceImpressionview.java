package vista;

import util.ImpresionUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Printable invoice view intended for basic ticket-style printing.
 *
 * <p>
 * This class contains only UI code (Swing components and event handlers) and
 * delegates business logic to the corresponding controllers.</p>
 */
public class InvoiceImpressionview extends JDialog {

    private final JTextArea area;
    private boolean impresionConfirmada = false;

    /**
     * Creates the view and initializes its Swing components.
     */

    public InvoiceImpressionview(Frame owner, String textoFactura) {
        super(owner, "Impresión de factura", true);

        area = new JTextArea(textoFactura);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setEditable(false);

        JScrollPane scroll = new JScrollPane(area);

        JButton btnImprimir = new JButton("🖨 Imprimir");
        JButton btnCerrar = new JButton("Cerrar");

        btnImprimir.addActionListener(e -> imprimir());
        btnCerrar.addActionListener(e -> dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCerrar);
        botones.add(btnImprimir);

        setLayout(new BorderLayout(10, 10));
        add(scroll, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);

        setSize(520, 520);
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
            boolean ok = ImpresionUtil.imprimirTexto(area, this);

            if (!ok) {
                impresionConfirmada = false;
                JOptionPane.showMessageDialog(this, "Impresión cancelada. No se realizará el pago.");
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
        }
    }
}
