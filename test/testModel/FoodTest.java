package testModel;

import Model.Food;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas unitarias para la clase Food
 * 
 */
public class FoodTest {

    private Food comida;

    @Before
    public void setUp() {
        // Se crea un objeto Food válido antes de cada test
        comida = new Food("C01", "Hamburguesa", "Comida", 2500, 10, 500);
    }

    /**
     * Verifica que las calorías se obtengan correctamente
     */
    @Test
    public void testGetCalorias() {
        assertEquals(500, comida.getCalorias());
    }

    /**
     * Verifica que se puedan cambiar las calorías
     */
    @Test
    public void testSetCalorias() {
        try {
            comida.setCalorias(600);
            assertEquals(600, comida.getCalorias());
        } catch (Exception e) {
            fail("No debería lanzar excepción al cambiar calorías");
        }
    }

    /**
     * Usuario pone calorías en cero
     */
    @Test
    public void testCaloriasCero() {
        comida.setCalorias(0);
        assertEquals(0, comida.getCalorias());
    }

    /**
     * Usuario pone calorías negativas
     * El sistema no debe caerse
     */
    @Test
    public void testCaloriasNegativas() {
        try {
            comida.setCalorias(-100);
            assertEquals(-100, comida.getCalorias());
        } catch (Exception e) {
            fail("No debería fallar con calorías negativas");
        }
    }

    /**
     * El método toString debe devolver texto válido
     */
    @Test
    public void testToString() {
        String texto = comida.toString();
        assertNotNull(texto);
        assertTrue(texto.length() > 0);
    }
}
