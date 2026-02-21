package util;

import javax.swing.*;
import java.awt.*;

public class ImpresionUtil {

    /**
     * Imprime el contenido de un JTextArea usando el diálogo estándar del sistema.
     * Esto funciona perfecto con impresoras térmicas si están instaladas como impresora normal (Windows/Linux).
     */
    public static void imprimirTexto(JTextArea area, Component parent) throws Exception {
        if (area == null) throw new IllegalArgumentException("Área de impresión null.");

        // true => muestra diálogo de impresión
        boolean ok = area.print();

        if (!ok) {
            // Usuario canceló el diálogo
            throw new Exception("Impresión cancelada.");
        }
    }
}