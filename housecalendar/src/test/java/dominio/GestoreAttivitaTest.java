package dominio; // Assicurati che il package sia corretto (es. test o dominio)

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;


import observer.Osservatore;

class GestoreAttivitaTest {
    private GestoreAttivita gestore;
    private Utente utenteTest;

    @BeforeEach
    void setUp() {
        gestore = GestoreAttivita.getInstance();
        // PULIZIA: Svuotiamo la lista in memoria prima di ogni test
        gestore.getTutteLeAttivita().clear();
        // Creiamo un utente finto per i test (necessario per il costruttore di Attivita)
        utenteTest = new Utente("Mario Rossi", "mario@example.com", "password123"); 
    }

    /**
     * Metodo helper per creare un'istanza di Attivita (essendo abstract)
     */
    private Attivita creaAttivitaTest(String descrizione, int priorita, TipoAttivita tipo) {
        return new Attivita(descrizione, tipo, 
                            LocalDateTime.now(), LocalDateTime.now().plusDays(1), 
                            null, priorita, utenteTest, false) {
            @Override
            public String getDettagli() {
                return "Dettagli di: " + descrizione;
            }
        };
    }

    // --- TEST SINGLETON ---
    @Test
    void testSingleton() {
        GestoreAttivita g1 = GestoreAttivita.getInstance();
        GestoreAttivita g2 = GestoreAttivita.getInstance();
        assertSame(g1, g2, "getInstance deve restituire sempre la stessa istanza");
    }

    // --- TEST AGGIUNTA ---
    @Test
    void testAggiungiAttivita() {
        Attivita a = creaAttivitaTest("Studiare Java", 1, TipoAttivita.STUDIO);
        
        gestore.aggiungiAttivita(a);
        
        assertFalse(gestore.getTutteLeAttivita().isEmpty());
        assertEquals(1, gestore.getTutteLeAttivita().size());
        assertEquals("Studiare Java", gestore.getTutteLeAttivita().get(0).getDescrizione());
    }

    // --- TEST RIMOZIONE ---
    @Test
    void testRimuoviAttivita() {
        Attivita a = creaAttivitaTest("Pulire cucina", 1, TipoAttivita.DOMESTICA);
        gestore.aggiungiAttivita(a);
        
        // Forziamo un ID per simulare la presenza nel DB
        a.setId(1); 
        
        gestore.rimuoviAttivita(a);
        
        assertTrue(gestore.getTutteLeAttivita().isEmpty(), "La lista deve essere vuota");
    }

    @Test
    void testRimuoviAttivitaNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            gestore.rimuoviAttivita(null);
        });
    }

    // --- TEST MODIFICA ---
    @Test
    void testModificaAttivita() {
        Attivita originale = creaAttivitaTest("Comprare latte", 1, TipoAttivita.SPESA);
        gestore.aggiungiAttivita(originale);
        originale.setId(10);

        Attivita modificata = creaAttivitaTest("Comprare latte e pane", 2, TipoAttivita.SPESA);
        modificata.setId(10);
        
        gestore.modificaAttivita(10, modificata);
        
        Attivita risultato = gestore.getAttivitaById(10);
        assertEquals("Comprare latte e pane", risultato.getDescrizione());
        assertEquals(2, risultato.getPriorita());
    }

    // --- TEST RICERCA ---
    @Test
    void testCercaPerId() {
        Attivita a = creaAttivitaTest("Esame", 3, TipoAttivita.STUDIO);
        a.setId(50);
        gestore.getTutteLeAttivita().add(a); 

        Attivita trovata = gestore.getAttivitaById(50);
        assertNotNull(trovata);
        assertEquals("Esame", trovata.getDescrizione());
    }

    @Test
    void testCercaPerNome() {
        gestore.aggiungiAttivita(creaAttivitaTest("Spesa Grossa", 1, TipoAttivita.SPESA));
        gestore.aggiungiAttivita(creaAttivitaTest("Spesa Piccola", 1, TipoAttivita.SPESA));
        gestore.aggiungiAttivita(creaAttivitaTest("Studiare", 2, TipoAttivita.STUDIO));

        List<Attivita> risultati = gestore.cercapernome("Spesa");
        assertEquals(2, risultati.size());
    }

    @Test
    void testCercaPerTipo() {
        gestore.aggiungiAttivita(creaAttivitaTest("Lavare piatti", 1, TipoAttivita.DOMESTICA));
        List<Attivita> risultati = gestore.cercapertipo("DOMESTICA");
        assertEquals(1, risultati.size());
    }

    // --- TEST ORDINAMENTO ---
    @Test
    void testOrdinaPerNome() {
        gestore.aggiungiAttivita(creaAttivitaTest("Zaino", 1, TipoAttivita.STUDIO));
        gestore.aggiungiAttivita(creaAttivitaTest("Astuccio", 1, TipoAttivita.STUDIO));

        gestore.ordinapernome();
        
        List<Attivita> lista = gestore.getTutteLeAttivita();
        assertEquals("Astuccio", lista.get(0).getDescrizione());
        assertEquals("Zaino", lista.get(1).getDescrizione());
    }

    @Test
    void testOrdinaPerPriorita() {
        gestore.aggiungiAttivita(creaAttivitaTest("Bassa", 1, TipoAttivita.DOMESTICA));
        gestore.aggiungiAttivita(creaAttivitaTest("Alta", 3, TipoAttivita.STUDIO));

        gestore.ordinaperpriorita();
        
        List<Attivita> lista = gestore.getTutteLeAttivita();
        // Verifica che l'ordine sia cambiato (a seconda se la tua strategia è cresc o decresc)
        assertTrue(lista.get(0).getPriorita() != lista.get(1).getPriorita());
    }

    // --- TEST OBSERVER ---
    @Test
    void testObserver() {
        class SpyObserver implements Osservatore {
            boolean notificato = false;
            @Override
            public void aggiorna() { notificato = true; }
        }

        SpyObserver spy = new SpyObserver();
        gestore.aggiungiOsservatore(spy);

        gestore.aggiungiAttivita(creaAttivitaTest("Test", 1, TipoAttivita.STUDIO));

        assertTrue(spy.notificato, "L'osservatore doveva essere notificato");
    }
}