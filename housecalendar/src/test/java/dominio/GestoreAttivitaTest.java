package dominio; // Assicurati che il package sia corretto (es. test o dominio)

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataBase.DAO.UtenteDAO;
import gestione.AttivitaGestione;
import gestione.AttivitaGestioneImp;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Test per GestoreAttivita ma chiamano metodi "AttivitaGestioneImp" che gestisci controlli prima di chiamare GestoreAttivita
class GestoreAttivitaTest {
    
    private AttivitaGestione gestione;
    private Utente utenteTest;

    //id test creati per cancellazione all fine
    private Integer id1 = null;
    private Integer id2 = null;

    @BeforeEach
    void setUp() {
        gestione = new AttivitaGestioneImp();
        utenteTest = new Utente("UtenteTest", "test@email.com", "00000000");
        // assicuro che l'utente esista 
        UtenteDAO.aggiungiUtente(utenteTest);
        //sync 
        gestione.caricaDaDB();
        id1 = null;
        id2 = null;
    }

    @AfterEach
    void tearDown() {
        // rimuovo le attività create (se esistono)
        try { 
            if (id1 != null) 
            gestione.rimuoviAttivita(id1); 
        }catch (Exception ignored){}
        try {
            if (id2 != null) 
            gestione.rimuoviAttivita(id2); 
        }catch (Exception ignored){}

        // rimuovo anche l'utente test (solo dopo aver tolto le attività)
        try { 
            UtenteDAO.rimuoviUtenteByEmail(utenteTest.getEmail()); 
        }catch (Exception ignored) {}
    }

private Attivita creaAttivitaConFactory(String descrizione, TipoAttivita tipo,
                                        LocalDateTime inizio, LocalDateTime fine,
                                        int priorita, boolean privata, String context) {

    LocalDateTime notifica = inizio.minusMinutes(10); // sempre presente nei test

    return AttivitaFactory.crea(
            descrizione, tipo,
            inizio, fine, notifica,
            priorita, utenteTest, privata,
            context,false
    );
}


private Map<String, Object> parametriDiUnaAttivita(Attivita a) {
    Map<String, Object> p = new HashMap<>();
    p.put("descrizione", a.getDescrizione());
    p.put("tipo", a.getTipo());
    p.put("dataInizio", a.getDataInizio());
    p.put("dataFine", a.getDataFine());
    p.put("dataNotifica", a.getDataNotifica());
    p.put("priorita", a.getPriorita());
    p.put("utenteAssegnato", a.getUtenteAssegnato());
    p.put("attivitaPrivata", a.isAttivitaPrivata());

    // context: dalla sottoclasse 
    String context = null;
    if (a instanceof AttivitaSpesa spesa) context = spesa.getNegozio();
    else if (a instanceof AttivitaStudio studio) context = studio.getMateria();
    else if (a instanceof AttivitaDomestica dom) context = dom.getStanzaCasa();

    p.put("context", context);

    return p;
}
//trova se attivita appena creata esiste
private int trovaIdPerDescrizione(String descrizione) {
    for (Attivita a : gestione.getTutteLeAttivita()) {
        if (descrizione.equals(a.getDescrizione())) {
            return a.getId();
        }
    }
    fail("Non ho trovato l'attività appena creata: " + descrizione);
    return -1;
}

  

    //singleton
    @Test
    void testSingleton() {
        GestoreAttivita g1 = GestoreAttivita.getInstance();
        GestoreAttivita g2 = GestoreAttivita.getInstance();
        assertSame(g1, g2, "getinstance restituire la stessa istanza");
    }
    @Test
    void testAggiungiGetByIdERimuovi() {
        String desc = "TEST_aggiungi_1";
        LocalDateTime inizio = LocalDateTime.of(2026, 10, 16, 8, 0);
        LocalDateTime fine = LocalDateTime.of(2026, 10, 16, 10, 0);

        Attivita a = creaAttivitaConFactory(desc, TipoAttivita.STUDIO, inizio, fine, 2, false, "Programmazione");

        gestione.aggiungiAttivita(parametriDiUnaAttivita(a));

        gestione.caricaDaDB();

        id1 = trovaIdPerDescrizione(desc);

        Attivita presa = gestione.getAttivitaById(id1);
        assertNotNull(presa);
        assertEquals(desc, presa.getDescrizione());
        assertEquals(TipoAttivita.STUDIO, presa.getTipo());

        // rimozione
        int idRimosso = id1;          // <-- salva l'id
        gestione.rimuoviAttivita(idRimosso);
        id1 = null;

        gestione.caricaDaDB();

        // ora deve NON esistere più
        assertThrows(IllegalArgumentException.class, () -> gestione.getAttivitaById(idRimosso));
}
@Test
void testRimuoviAttivitaById() {
    String desc = "TEST_rimuovi_separamente";
    LocalDateTime inizio = LocalDateTime.of(2026, 10, 16, 8, 0);
    LocalDateTime fine = LocalDateTime.of(2026, 10, 16, 10, 0);

    Attivita a = creaAttivitaConFactory(desc, TipoAttivita.DOMESTICA, inizio, fine, 1, false, "Cucina");
    gestione.aggiungiAttivita(parametriDiUnaAttivita(a));
    gestione.caricaDaDB();
    id1 = trovaIdPerDescrizione(desc);

    int idRimosso = id1;
    gestione.rimuoviAttivita(idRimosso);
    id1 = null;

    gestione.caricaDaDB();
    assertThrows(IllegalArgumentException.class, () -> gestione.getAttivitaById(idRimosso));
}
//se una utente ha già assegnato una data a una attivita non po creare un altra nello stesso intervallo
@Test
void testVerificaConflitti() {
    LocalDateTime inizio = LocalDateTime.of(2026, 10, 16, 8, 0);
    LocalDateTime fine = LocalDateTime.of(2026, 10, 16, 10, 0);

    String desc = "TEST_JUNIT_conf_1";

    // attività già presente 08:00-10:00
    gestione.aggiungiAttivita(parametriDiUnaAttivita(
            creaAttivitaConFactory(desc, TipoAttivita.STUDIO, inizio, fine, 1, false, "Prog")));
    gestione.caricaDaDB();
    id1 = trovaIdPerDescrizione(desc);

    // nuova attività sovrapposta 09:00-09:30 (NON la inserisco, la uso solo per verificaConflitti)
    Attivita nuova = creaAttivitaConFactory(
            "TEST_JUNIT_conf_2",
            TipoAttivita.STUDIO,
            LocalDateTime.of(2026, 10, 16, 9, 0),
            LocalDateTime.of(2026, 10, 16, 9, 30),
            1, false, "Prog"
    );

    assertTrue(gestione.verificaConflitti(nuova));
}

   
@Test
    void testRimuoviAttivitaNonesiste() {
        assertThrows(IllegalArgumentException.class, () -> {
            gestione.rimuoviAttivita(-1);
        });
    }
@Test
void testModificaAttivita() {
    String desc = "TEST_modifica";
    LocalDateTime inizio = LocalDateTime.of(2026, 10, 16, 8, 0);
    LocalDateTime fine = LocalDateTime.of(2026, 10, 16, 10, 0);

    // Creo e aggiungo
    Attivita a = creaAttivitaConFactory(desc, TipoAttivita.SPESA, inizio, fine, 1, false, "Conad");
    gestione.aggiungiAttivita(parametriDiUnaAttivita(a));
    gestione.caricaDaDB();
    id1 = trovaIdPerDescrizione(desc);

    //modifico (nuova descrizione + priorita + context)
    String nuovaDesc = "TEST_dopo_modifica";
    Attivita nuova = creaAttivitaConFactory(nuovaDesc, TipoAttivita.SPESA, inizio, fine, 3, false, "Esselunga");

    gestione.modificaAttivita(id1, parametriDiUnaAttivita(nuova));
    gestione.caricaDaDB();

    Attivita aggiornata = gestione.getAttivitaById(id1);
    assertEquals(nuovaDesc, aggiornata.getDescrizione());
    assertEquals(3, aggiornata.getPriorita());
}

@Test
void testCercaPerDataIntervallo() {
    String desc = "TEST_JUNIT_data_1";
    LocalDateTime inizio = LocalDateTime.of(2026, 10, 16, 8, 0);
    LocalDateTime fine = LocalDateTime.of(2026, 10, 16, 10, 0);

    Attivita a = creaAttivitaConFactory(desc, TipoAttivita.STUDIO, inizio, fine, 2, false, "Programmazione");
    gestione.aggiungiAttivita(parametriDiUnaAttivita(a));
    gestione.caricaDaDB();
    id1 = trovaIdPerDescrizione(desc);

    // Cerco una finestra che si sovrappone (09:00-09:30)
    LocalDateTime ricercaInizio = LocalDateTime.of(2026, 10, 16, 9, 0);
    LocalDateTime ricercaFine = LocalDateTime.of(2026, 10, 16, 9, 30);

    List<Attivita> risultati = gestione.cercaPerData(ricercaInizio, ricercaFine);
    assertTrue(risultati.stream().anyMatch(x -> x.getId() == id1));
}

@Test
void testCercaPerNome() {
    LocalDateTime inizio = LocalDateTime.of(2026, 10, 16, 8, 0);
    LocalDateTime fine = LocalDateTime.of(2026, 10, 16, 10, 0);  //8-10
    LocalDateTime inizio2 = LocalDateTime.of(2026, 10, 16, 10, 0); //10-12
    LocalDateTime fine2 = LocalDateTime.of(2026, 10, 16, 12, 0);
    String d1 = "TEST_nome_spesa_1";
    String d2 = "TEST_nome_spesa_2";

    gestione.aggiungiAttivita(parametriDiUnaAttivita(
            creaAttivitaConFactory(d1, TipoAttivita.SPESA, inizio, fine, 1, false, "Coop")));
    gestione.aggiungiAttivita(parametriDiUnaAttivita(
            creaAttivitaConFactory(d2, TipoAttivita.SPESA, inizio2, fine2, 1, false, "Conad")));

    gestione.caricaDaDB();
    id1 = trovaIdPerDescrizione(d1);
    id2 = trovaIdPerDescrizione(d2);

    List<Attivita> risultati = gestione.cercaPerNome("TEST_nome_spesa");
    assertTrue(risultati.size() >= 2);
}

@Test
void testCercaPerTipo() {
    String desc = "TEST_tipo_domestica_1";
    LocalDateTime inizio = LocalDateTime.of(2026, 10, 16, 8, 0);
    LocalDateTime fine = LocalDateTime.of(2026, 10, 16, 10, 0);

    gestione.aggiungiAttivita(parametriDiUnaAttivita(
            creaAttivitaConFactory(desc, TipoAttivita.DOMESTICA, inizio, fine, 1, false, "Bagno")));

    gestione.caricaDaDB();
    id1 = trovaIdPerDescrizione(desc);

    List<Attivita> risultati = gestione.cercaPerTipo("DOMESTICA");
    assertTrue(risultati.stream().anyMatch(x -> x.getId() == id1));
}

@Test
void testCercaPerId() {
    String desc = "TEST_id_1";
    LocalDateTime inizio = LocalDateTime.of(2026, 10, 16, 8, 0);
    LocalDateTime fine = LocalDateTime.of(2026, 10, 16, 10, 0);

    gestione.aggiungiAttivita(parametriDiUnaAttivita(
            creaAttivitaConFactory(desc, TipoAttivita.STUDIO, inizio, fine, 1, false, "Reti")));

    gestione.caricaDaDB();
    id1 = trovaIdPerDescrizione(desc);

    Attivita trovata = gestione.cercaperid(id1);
    assertNotNull(trovata);
    assertEquals(desc, trovata.getDescrizione());
}

@Test
void testCercaPerPriorita() {
    LocalDateTime inizio = LocalDateTime.of(2026, 10, 16, 8, 0);
    LocalDateTime fine = LocalDateTime.of(2026, 10, 16, 10, 0);
    LocalDateTime inizio2 = LocalDateTime.of(2026, 10, 16, 10, 0);
    LocalDateTime fine2 = LocalDateTime.of(2026, 10, 16, 12, 0);

    String d1 = "TEST_prio_1";
    String d2 = "TEST_prio_3";

    gestione.aggiungiAttivita(parametriDiUnaAttivita(
            creaAttivitaConFactory(d1, TipoAttivita.STUDIO, inizio, fine, 1, false, "Algo")));
    gestione.aggiungiAttivita(parametriDiUnaAttivita(
            creaAttivitaConFactory(d2, TipoAttivita.STUDIO, inizio2, fine2, 3, false, "Algo")));

    gestione.caricaDaDB();
    id1 = trovaIdPerDescrizione(d1);
    id2 = trovaIdPerDescrizione(d2);

    List<Attivita> risultati = gestione.cercaperpriorita(3);
    assertTrue(risultati.stream().anyMatch(x -> x.getId() == id2));
}

@Test
void testFiltra() {
    String desc = "TEST_filtra_1";
    LocalDateTime inizio = LocalDateTime.of(2026, 10, 16, 8, 0);
    LocalDateTime fine = LocalDateTime.of(2026, 10, 16, 10, 0);

    gestione.aggiungiAttivita(parametriDiUnaAttivita(
            creaAttivitaConFactory(desc, TipoAttivita.STUDIO, inizio, fine, 2, false, "Algoritmi")));

    gestione.caricaDaDB();
    id1 = trovaIdPerDescrizione(desc);

    Map<String, Object> criteri = new HashMap<>();
    criteri.put("descrizione", "TEST_filtra_1");
    criteri.put("tipo", "STUDIO");
    criteri.put("priorita", 2);

    List<Attivita> risultati = gestione.filtra(criteri);
    assertTrue(risultati.stream().anyMatch(x -> x.getId() == id1));
}


//ordinamento

@Test
void testOrdinaPerNome() {
    LocalDateTime inizio = LocalDateTime.of(2026, 10, 16, 8, 0);
    LocalDateTime fine   = LocalDateTime.of(2026, 10, 16, 9, 0);

    Attivita a1 = creaAttivitaConFactory("B-progetto java", TipoAttivita.STUDIO, inizio, fine, 1, false, "Java");
    Attivita a2 = creaAttivitaConFactory("A-reti", TipoAttivita.STUDIO, inizio.plusHours(1), fine.plusHours(1), 1, false, "Java");

    gestione.aggiungiAttivita(parametriDiUnaAttivita(a1));
    gestione.aggiungiAttivita(parametriDiUnaAttivita(a2));

    gestione.caricaDaDB();
    GestoreAttivita.getInstance().ordinapernome();

    List<Attivita> lista = gestione.getTutteLeAttivita();

    assertEquals("A-reti", lista.get(0).getDescrizione());  //A > B
    assertEquals("B-progetto java", lista.get(1).getDescrizione());

    id1 = trovaIdPerDescrizione("B-progetto java");
    id2 = trovaIdPerDescrizione("A-reti");
}

@Test
void testOrdinaPerPriorita() {
    LocalDateTime inizio = LocalDateTime.of(2026, 10, 16, 8, 0);
    LocalDateTime fine   = LocalDateTime.of(2026, 10, 16, 9, 0);

    Attivita bassa = creaAttivitaConFactory("Bassa", TipoAttivita.DOMESTICA, inizio, fine, 1, false, "Cucina");
    Attivita alta  = creaAttivitaConFactory("Alta", TipoAttivita.DOMESTICA, inizio.plusHours(1), fine.plusHours(1), 3, false, "Cucina");

    gestione.aggiungiAttivita(parametriDiUnaAttivita(bassa));
    gestione.aggiungiAttivita(parametriDiUnaAttivita(alta));

    gestione.caricaDaDB();
    GestoreAttivita.getInstance().ordinaperpriorita();

    List<Attivita> lista = gestione.getTutteLeAttivita();

    assertTrue(lista.get(0).getPriorita() <= lista.get(1).getPriorita());

    id1 = trovaIdPerDescrizione("Bassa");
    id2 = trovaIdPerDescrizione("Alta");
    
}

@Test
void testOrdinaPerData() {
    LocalDateTime inizio1 = LocalDateTime.of(2026, 10, 16, 8, 0);
    LocalDateTime fine1   = LocalDateTime.of(2026, 10, 16, 9, 0);

    LocalDateTime inizio2 = LocalDateTime.of(2026, 10, 17, 8, 0);
    LocalDateTime fine2   = LocalDateTime.of(2026, 10, 17, 9, 0);

    Attivita prima = creaAttivitaConFactory("prima", TipoAttivita.SPESA, inizio2, fine2, 1, false, "Market");
    Attivita seconda = creaAttivitaConFactory("seconda", TipoAttivita.SPESA, inizio1, fine1, 1, false, "Market");

    gestione.aggiungiAttivita(parametriDiUnaAttivita(prima));
    gestione.aggiungiAttivita(parametriDiUnaAttivita(seconda));

    gestione.caricaDaDB();
    GestoreAttivita.getInstance().ordinaperdata();

    List<Attivita> lista = gestione.getTutteLeAttivita();

    assertEquals("seconda", lista.get(0).getDescrizione());

    id1 = trovaIdPerDescrizione("prima");
    id2 = trovaIdPerDescrizione("seconda");
}



}