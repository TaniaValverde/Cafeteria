package util;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PrinterException;

public class ImpresionUtil {

    public static boolean imprimirTexto(JTextArea area, Component parent) throws PrinterException {
        if (area == null) {
            throw new IllegalArgumentException("Área de impresión null.");
        }

        // MUY IMPORTANTE: asegura que tenga tamaño antes de imprimir (evita páginas en blanco)
        area.setSize(area.getPreferredSize());

        // Fuerza diálogo de impresión y diálogo de progreso (más confiable que area.print() pelado)
        return area.print(
                null,   // header
                null,   // footer
                true,   // showPrintDialog
                null,   // PrintService (null = default)
                null,   // PrintRequestAttributeSet
                true    // interactive (muestra progreso/cancelar)
        );
    }
}