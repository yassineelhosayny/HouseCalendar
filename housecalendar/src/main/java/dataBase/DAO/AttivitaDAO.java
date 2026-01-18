package dataBase.DAO;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dataBase.util.DBConnection;
import dominio.Attivita;
import dominio.AttivitaDomestica;
import dominio.AttivitaFactory;
import dominio.AttivitaSpesa;
import dominio.AttivitaStudio;
import dominio.TipoAttivita;
import dominio.Utente;

public class AttivitaDAO {

    // metodo aggiungiAttivita(attivita);
    public static int aggiungiAttivita(Attivita a) {
        Connection conn = null;

        String sql = "INSERT INTO attivita " +
                "(descrizione, tipo, data_inizio, data_fine, data_notifica, priorita, attivita_privata, context, utente_email, notificata) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; 


        try {
            conn = DBConnection.startConnection(null, "");
            if (conn == null) {
                throw new IllegalStateException("Connessione DB non disponibile (driver SQLite mancante?)");
            }
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, a.getDescrizione());
            ps.setString(2, a.getTipo().name());
            ps.setString(3, a.getDataInizio().toString());
            ps.setString(4, a.getDataFine().toString());
            ps.setString(5, a.getDataNotifica().toString());
            ps.setInt(6, a.getPriorita());
            ps.setInt(7, a.isAttivitaPrivata() ? 1 : 0);
            ps.setString(8, extractContext(a));
            ps.setString(9, a.getUtenteAssegnato().getEmail());
            ps.setInt(10, a.isNotificata() ? 1 : 0); 

            int rows = ps.executeUpdate();
            if (rows == 0) return -1;

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }

        } catch (Exception e) {
            System.err.println("ERRORE INSERT attivita: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }

        return -1;
    }

    public static boolean updateAttivita(Attivita a) {
        Connection conn = null;

        String sql = "UPDATE attivita SET " +
                "descrizione=?, tipo=?, data_inizio=?, data_fine=?, data_notifica=?, " + "priorita=?, attivita_privata=?, context=?, utente_email=?, notificata=? " +"WHERE id=?";

        try {
            conn = DBConnection.startConnection(null, "");

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, a.getDescrizione());
            ps.setString(2, a.getTipo().name());
            ps.setString(3, a.getDataInizio().toString());
            ps.setString(4, a.getDataFine().toString());
            ps.setString(5, a.getDataNotifica().toString());
            ps.setInt(6, a.getPriorita());
            ps.setInt(7, a.isAttivitaPrivata() ? 1 : 0);
            ps.setString(8, extractContext(a));
            ps.setString(9, a.getUtenteAssegnato().getEmail());
            ps.setInt(10, a.isNotificata() ? 1 : 0); 
            ps.setInt(11, a.getId()); 

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static boolean rimuoviAttivitaById(int id) {
        Connection conn = null;

        String sql = "DELETE FROM attivita WHERE id=?";

        try {
            conn = DBConnection.startConnection(null, "");

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static List<Attivita> getAllAttivita() {
        Connection conn = null;
        List<Attivita> lista = new ArrayList<>();

        String sql =
                "SELECT a.id, a.descrizione, a.tipo, a.data_inizio, a.data_fine, a.data_notifica, " +
                "a.priorita, a.attivita_privata, a.context, a.notificata, " + "u.nome, u.email, u.password " +
                "FROM attivita a " +"JOIN utente u ON a.utente_email = u.email " + "ORDER BY a.data_inizio";

        try {
            conn = DBConnection.startConnection(null, "");

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String descrizione = rs.getString("descrizione");
                TipoAttivita tipo = TipoAttivita.valueOf(rs.getString("tipo"));

                LocalDateTime dataInizio = LocalDateTime.parse(rs.getString("data_inizio"));
                LocalDateTime dataFine = LocalDateTime.parse(rs.getString("data_fine"));
                LocalDateTime dataNotifica = LocalDateTime.parse(rs.getString("data_notifica"));

                int priorita = rs.getInt("priorita");
                boolean privata = rs.getInt("attivita_privata") == 1;
                String context = rs.getString("context");

                boolean notificata = rs.getInt("notificata") == 1; 

                // utente (join)
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String password = rs.getString("password");
                Utente utente = new Utente(nome, email, password);

                // Factory crea la sottoclasse 
                Attivita a = AttivitaFactory.crea(
                        descrizione, tipo, dataInizio, dataFine, dataNotifica,
                        priorita, utente, privata, context, notificata 
                );
                a.setId(id);

                lista.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }

        return lista;
    }

    public static boolean setNotificata(int idAttivita, boolean value) {
        Connection conn = null;
        String sql = "UPDATE attivita SET notificata=? WHERE id=?";

        try {
            conn = DBConnection.startConnection(null, "");
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, value ? 1 : 0);
            ps.setInt(2, idAttivita);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    public static List<Attivita> getAttivitaDaNotificare(LocalDateTime now) {
        Connection conn = null;
        List<Attivita> lista = new ArrayList<>();

        String sql =
                "SELECT a.id, a.descrizione, a.tipo, a.data_inizio, a.data_fine, a.data_notifica, " +
                "a.priorita, a.attivita_privata, a.context, a.notificata, " +
                "u.nome, u.email, u.password " +
                "FROM attivita a " +
                "JOIN utente u ON a.utente_email = u.email " +
                "WHERE a.data_notifica <= ? AND a.notificata = 0 " +
                "ORDER BY a.data_notifica";

        try {
            conn = DBConnection.startConnection(null, "");
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, now.toString());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String descrizione = rs.getString("descrizione");
                TipoAttivita tipo = TipoAttivita.valueOf(rs.getString("tipo"));

                LocalDateTime dataInizio = LocalDateTime.parse(rs.getString("data_inizio"));
                LocalDateTime dataFine = LocalDateTime.parse(rs.getString("data_fine"));
                LocalDateTime dataNotifica = LocalDateTime.parse(rs.getString("data_notifica"));

                int priorita = rs.getInt("priorita");
                boolean privata = rs.getInt("attivita_privata") == 1;
                String context = rs.getString("context");
                boolean notificata = rs.getInt("notificata") == 1; // qui sarà sempre false, leggiamo lo stesso

                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String password = rs.getString("password");
                Utente utente = new Utente(nome, email, password);

                Attivita a = AttivitaFactory.crea(
                        descrizione, tipo, dataInizio, dataFine, dataNotifica,
                        priorita, utente, privata, context, notificata
                );
                a.setId(id);
                lista.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }

        return lista;
    }

    // verificare cotext
    private static String extractContext(Attivita a) {
        if (a instanceof AttivitaSpesa spesa)
            return spesa.getNegozio();
        if (a instanceof AttivitaStudio studio)
            return studio.getMateria();
        if (a instanceof AttivitaDomestica dom)
            return dom.getStanzaCasa();
        return null;
    }
}
