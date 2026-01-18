package dataBase.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dataBase.util.DBConnection;
import dominio.Utente;

public class UtenteDAO {

    // INSERT: true se inserito, false se errore (o già esiste)
   public static boolean aggiungiUtente(Utente u) {
    if (u == null) throw new IllegalArgumentException("Utente nullo"); 

    Connection conn = null;

    //se l'email esiste già, ignore enon fa nulla e non lancia errori
    String sql = "INSERT OR IGNORE INTO utente (nome, email, password) VALUES (?, ?, ?)";

    try {
        conn = DBConnection.startConnection(null, "");
        if (conn == null) {
            throw new IllegalStateException("Connessione DB non disponibile (driver SQLite mancante?)");
        }
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, u.getNome());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getPassword());

        int rows = ps.executeUpdate();

        // rows = 1 se inserito, rows = 0 se già esiste
        return rows == 1;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    } finally {
        DBConnection.closeConnection(conn);
    }
}

    // SELECT by email: ritorna Utente o null se non trovato
    public static Utente getUtenteByEmail(String email) {
        if (email == null || email.isBlank()) return null; 

        Connection conn = null;

        String sql = "SELECT nome, email, password FROM utente WHERE email = ?";

        try {
            conn = DBConnection.startConnection(null, "");

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                String em = rs.getString("email");
                String password = rs.getString("password");

                return new Utente(nome, em, password);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }

        return null;
    }

    // UPDATE: true se aggiornato
    public static boolean updateUtente(Utente u) {
        if (u == null) throw new IllegalArgumentException("Utente nullo"); 

        Connection conn = null;

        String sql = "UPDATE utente SET nome = ?, password = ? WHERE email = ?";

        try {
            conn = DBConnection.startConnection(null, "");

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, u.getNome());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getEmail());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    // DELETE: true se cancellato
    public static boolean rimuoviUtenteByEmail(String email) {
        if (email == null || email.isBlank()) return false; 

        Connection conn = null;

        String sql = "DELETE FROM utente WHERE email = ?";
        try {
            conn = DBConnection.startConnection(null, "");

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    // SELECT ALL: lista utenti
    public static List<Utente> getAllUtenti() {
        Connection conn = null;
        List<Utente> lista = new ArrayList<>();

        String sql = "SELECT nome, email, password FROM utente ORDER BY nome";

        try {
            conn = DBConnection.startConnection(null, "");

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String password = rs.getString("password");

                lista.add(new Utente(nome, email, password));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }

        return lista;
    }

    // Login semplice: true se email e password combaciano
    public static boolean checkLogin(String email, String password) {
        if (email == null || password == null) return false; 

        Connection conn = null;
        String sql = "SELECT 1 FROM utente WHERE email = ? AND password = ?";
        try {
            conn = DBConnection.startConnection(null, "");

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
}