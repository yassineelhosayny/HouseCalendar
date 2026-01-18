package dominio;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class AttivitaTest {
	
	static class AttivitaDiTest extends Attivita{
		
	    public AttivitaDiTest(
	    		String descrizione, 
	    		TipoAttivita tipo, 
				LocalDateTime dataInizio,
	    		LocalDateTime dataFine, 
	    		LocalDateTime dataNotifica,
				int priorita, 
				Utente utenteAssegnato, 
				boolean attivitaPrivata
		) {
			super(descrizione, tipo, dataInizio,dataFine, dataNotifica, priorita, utenteAssegnato, attivitaPrivata,false);
			
		}
	    
	    @Override
	    public String getDettagli() {
	    	return "dettagli test";
	    }
	}
	
	
	private AttivitaDiTest attivitaTest;
	
	

	@BeforeEach
	void setUp() throws Exception {
		
		Utente utente = new Utente("Mario", "mariorossi@gmail.com", "Password1");
		
		attivitaTest = new AttivitaDiTest(
				"Pulire la cucina", 			//descrizione
				TipoAttivita.DOMESTICA, 		//tipo
				LocalDateTime.now().plusDays(4).plusHours(10),    //data inizio
				LocalDateTime.now().plusDays(4).plusHours(12), 	//data fine
				LocalDateTime.now().plusDays(2), 	//data notifica
				2,								//priorita
				utente, 						//utente assegnato
				true  							//privata
		);
		
	}
	

	@Test
	void testDescrizione() {
	    assertEquals("Pulire la cucina", attivitaTest.getDescrizione());
	    assertNotEquals("Pulire", attivitaTest.getDescrizione());

	}
	
	@Test
	void testTipo() {
		assertEquals("DOMESTICA", attivitaTest.getTipo().name());
	}
	
	
	@Test
	void testdataScadenza() {
		LocalDateTime data_scaduta = LocalDateTime.now().minusDays(1);
		assertThrows(IllegalArgumentException.class, ()->{
					attivitaTest.setDataFine(data_scaduta);
		});
	}

	
	@Test
	void testdataScadenzaOdierna() {
		LocalDateTime data_odierna = LocalDateTime.now();
		assertThrows(IllegalArgumentException.class, ()->{
					attivitaTest.setDataFine(data_odierna);
		});
	}
	
}
