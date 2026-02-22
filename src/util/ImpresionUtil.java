package util;

import javax.swing.*;
import java.awt.*;

public class ImpresionUtil {

    /**
     * @return true si se envió a imprimir, false si el usuario canceló el
     * diálogo.
     */
    public static boolean imprimirTexto(JTextArea area, Component parent) throws Exception {
        if (area == null) {
            throw new IllegalArgumentException("Área de impresión null.");
        }

        // Retorna false cuando el usuario cancela.
        return area.print();
    }
}
