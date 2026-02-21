package vista;

import util.ImpresionUtil;

import javax.swing.*;
import java.awt.*;

public class vistaImpresionFactura extends JDialog {

    private final JTextArea area;

    public vistaImpresionFactura(Frame owner, String textoFactura) {
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

    private void imprimir() {
        try {
            ImpresionUtil.imprimirTexto(area, this);
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