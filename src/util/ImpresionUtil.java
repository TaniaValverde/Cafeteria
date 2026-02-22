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
     * @throws IllegalArgumentException if {@code area} is null
     * @throws Exception if the user cancels the print dialog or printing fails
     */
    public static void imprimirTexto(JTextArea area, Component parent) throws Exception {
        if (area == null) throw new IllegalArgumentException("Área de impresión null.");

        boolean ok = area.print();

        if (!ok) {
            throw new Exception("Impresión cancelada.");
        }
    }
}