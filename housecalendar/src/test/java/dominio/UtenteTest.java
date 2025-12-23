package dominio;




import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;




public class UtenteTest {
	static Utente u1 ;
	static Utente u2;
	static Attivita a1;
	
	@BeforeEach
	public void setUp() {
		u1= new Utente("yassine","yassineelhosayny99@gmail.com","12345678");
		u2= new Utente("marco","marco@gmail.com","87654321");

		new AttivitaFactory();
		a1= AttivitaFactory.crea("Compra Latte",TipoAttivita.SPESA,LocalDateTime.parse("2026-04-12T08:00") , LocalDateTime.parse("2026-04-12T10:00"), LocalDateTime.parse("2026-04-06T08:00"), 2, u1, false,"familia");
	}
	@AfterEach
	public void tearDown() {
		u1=null;u2=null;
	}
	

	@Test
	public void testConstutoreUtente() {
		assertEquals("yassine",u1.getNome());
		assertEquals(u2.getEmail(),"marco@gmail.com");
		
		assertFalse(u1.verificaPassword("00000"));
		
		assertThrows(IllegalArgumentException.class,()-> {
			new Utente("var1","var2@dominio.com",null);
		});
	}

	
	
	@Test
	public void testGetNome() {
		assertEquals(u1.getNome(),"yassine");
		assertNotEquals(u2.getNome(),"ohohho");
	}
	
	
	@Test
	public void testVerificaPassword() {
		assertTrue(u1.verificaPassword("12345678"));
		assertFalse(u2.verificaPassword(null));
	}
	@Test 
	public void testAggiungiAttivita(){
		assertEquals(0,u1.getListaAttivita().size());
		u1.aggiungiAttivita(a1);
		assertEquals(1,u1.getListaAttivita().size());
		assertThrows(IllegalArgumentException.class, ()->{
			u1.aggiungiAttivita(a1);
		});

	}
	@Test
	public void testRimuoviAttivita(){
		u1.aggiungiAttivita(a1);
		assertEquals(1,u1.getListaAttivita().size());
		u1.rimuoviAttivita(a1);
		assertEquals(0,u1.getListaAttivita().size());
		assertThrows(IllegalArgumentException.class, ()->{
			u1.rimuoviAttivita(a1);
		});
	}	
	@Test
	public void testGetListaAttivita(){
		assertEquals(0,u1.getListaAttivita().size());
		u1.aggiungiAttivita(a1);
		assertEquals(1,u1.getListaAttivita().size());
		assertEquals(u1,u1.getUtente());
	}
}

