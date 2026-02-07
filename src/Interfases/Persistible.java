package Interfases;

import java.io.IOException;

public interface Persistible {

    void guardarEnArchivo(String archivo) throws IOException;
}
