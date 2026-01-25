package dataBase.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dataBase.util.DBConnection;
import dominio.Utente;
import gestione.PasswordUtil;

public class UtenteDAO {

    // INSERT: true se inserito, false se errore (o già esiste)
   public static boolean aggiungiUtente(Utente u) {
    if (u == null) throw new IllegalArgumentException("Utente nullo"); 

    Connection conn = null;

    //se l'email esiste già, ignore enon fa nulla e non lancia errori
    String sql = "INSERT OR IGNORE INTO utente (nome, email, password, password_hash, password_salt) VALUES (?, ?, ?, ?, ?)";

    try {
        conn = DBConnection.startConnection(null, "");
        ensurePasswordColumns(conn);
        if (conn == null) {
            throw new IllegalStateException("Connessione DB non disponibile (driver SQLite mancante?)");
        }
        PreparedStatement ps = conn.prepareStatement(sql);
        String salt = PasswordUtil.generaSalt();
        String hash = PasswordUtil.hashPassword(u.getPassword(), salt);
        ps.setString(1, u.getNome());
        ps.setString(2, u.getEmail());
        ps.setString(3, "");
        ps.setString(4, hash);
        ps.setString(5, salt);

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

        String sql = "SELECT nome, email FROM utente WHERE email = ?";

        try {
            conn = DBConnection.startConnection(null, "");
            ensurePasswordColumns(conn);

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                String em = rs.getString("email");
                return new Utente(nome, em);
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

        String sqlCompleto = "UPDATE utente SET nome = ?, password = ?, password_hash = ?, password_salt = ? WHERE email = ?";
        String sqlSoloNome = "UPDATE utente SET nome = ? WHERE email = ?";

        try {
            conn = DBConnection.startConnection(null, "");
            ensurePasswordColumns(conn);

            if (u.getPassword() != null && !u.getPassword().isBlank()) {
                PreparedStatement ps = conn.prepareStatement(sqlCompleto);
                String salt = PasswordUtil.generaSalt();
                String hash = PasswordUtil.hashPassword(u.getPassword(), salt);
                ps.setString(1, u.getNome());
                ps.setString(2, "");
                ps.setString(3, hash);
                ps.setString(4, salt);
                ps.setString(5, u.getEmail());
                return ps.executeUpdate() > 0;
            } else {
                PreparedStatement ps = conn.prepareStatement(sqlSoloNome);
                ps.setString(1, u.getNome());
                ps.setString(2, u.getEmail());
                return ps.executeUpdate() > 0;
            }

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

        String sql = "SELECT nome, email FROM utente ORDER BY nome";

        try {
            conn = DBConnection.startConnection(null, "");
            ensurePasswordColumns(conn);

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                lista.add(new Utente(nome, email));
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
        String sql = "SELECT password, password_hash, password_salt FROM utente WHERE email = ?";
        try {
            conn = DBConnection.startConnection(null, "");
            ensurePasswordColumns(conn);

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return false;
            }

            String hash = rs.getString("password_hash");
            String salt = rs.getString("password_salt");
            String plain = rs.getString("password");

            if (hash != null && !hash.isBlank() && salt != null && !salt.isBlank()) {
                return PasswordUtil.verificaPassword(password, salt, hash);
            }

            if (plain != null && plain.equals(password)) {
                aggiornaHashPassword(conn, email, password);
                return true;
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    private static void ensurePasswordColumns(Connection conn) {
        if (conn == null) {
            return;
        }
        boolean hasHash = false;
        boolean hasSalt = false;
        boolean hasId = false;
        try (PreparedStatement ps = conn.prepareStatement("PRAGMA table_info(utente)");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nome = rs.getString("name");
                if ("password_hash".equalsIgnoreCase(nome)) {
                    hasHash = true;
                } else if ("password_salt".equalsIgnoreCase(nome)) {
                    hasSalt = true;
                } else if ("id".equalsIgnoreCase(nome)) {
                    hasId = true;
                }
            }
        } catch (Exception ignored) {
            return;
        }
        try (PreparedStatement alterHash = conn.prepareStatement(
                "ALTER TABLE utente ADD COLUMN password_hash TEXT")) {
            if (!hasHash) {
                alterHash.executeUpdate();
            }
        } catch (Exception ignored) {
        }
        try (PreparedStatement alterSalt = conn.prepareStatement(
                "ALTER TABLE utente ADD COLUMN password_salt TEXT")) {
            if (!hasSalt) {
                alterSalt.executeUpdate();
            }
        } catch (Exception ignored) {
        }

        try (PreparedStatement alterId = conn.prepareStatement(
                "ALTER TABLE utente ADD COLUMN id INTEGER")) {
            if (!hasId) {
                alterId.executeUpdate();
            }
        } catch (Exception ignored) {
        }

        try (PreparedStatement fixId = conn.prepareStatement(
                "UPDATE utente SET id = rowid WHERE id IS NULL")) {
            fixId.executeUpdate();
        } catch (Exception ignored) {
        }
    }

    private static void aggiornaHashPassword(Connection conn, String email, String password) {
        if (conn == null) {
            return;
        }
        String salt = PasswordUtil.generaSalt();
        String hash = PasswordUtil.hashPassword(password, salt);
        String sql = "UPDATE utente SET password = ?, password_hash = ?, password_salt = ? WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "");
            ps.setString(2, hash);
            ps.setString(3, salt);
            ps.setString(4, email);
            ps.executeUpdate();
        } catch (Exception ignored) {
        }
    }
}
