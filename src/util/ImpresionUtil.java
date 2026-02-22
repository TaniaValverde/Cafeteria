package util;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class for handling text printing through the system print dialog.
 *
 * This class centralizes printing logic for Swing components, allowing
 * compatibility with standard and thermal printers configured in the OS.
 */
public class ImpresionUtil {

    /**
     * Prints the content of a {@link JTextArea} using the default system print dialog.
     *
     * @param area   text area containing the content to print (must not be null)
     * @param parent parent component for dialog context (not directly used)
     * @return true if the print job was sent, false if the user cancelled
     * @throws IllegalArgumentException if {@code area} is null
     * @throws Exception if printing fails
     */
    public static boolean imprimirTexto(JTextArea area, Component parent) throws Exception {
        if (area == null) {
            throw new IllegalArgumentException("Área de impresión null.");
        }

        // Retorna false cuando el usuario cancela.
        return area.print();
    }
}