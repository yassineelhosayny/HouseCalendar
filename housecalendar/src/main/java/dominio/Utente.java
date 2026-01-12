package dominio;

import java.util.ArrayList;

//class rappresentante un'utente
public class Utente {
    //attributi dell'utente
    private String nome;
    private String email;
    private String password;
    private ArrayList<Attivita> attivitaAssegnate;
    
    //costruttore
    public Utente(String nome, String email, String password) {
        if(nome == null || nome.isEmpty()){
            throw new IllegalArgumentException("Il nome non può essere vuoto.");
        }
        if(email == null || email.isEmpty() || !email.contains("@")){
            throw new IllegalArgumentException("Email non valida.");
        }
        if(password == null || password.length() < 6){
            throw new IllegalArgumentException("La password deve contenere almeno 6 caratteri.");
        }
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.attivitaAssegnate = new ArrayList<>();
    }
    

    //metodi per gestire l'utente
    public String getNome() {
        return nome;
    }
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return this.password;
    }
    public Utente getUtente() {
        return this;
    }
    public boolean verificaPassword(String password) {
        return this.password.equals(password);
    }

    public ArrayList<Attivita> getListaAttivita(){
        return attivitaAssegnate;
    }
    public void aggiungiAttivita(Attivita a){
        if(a == null){
            throw new IllegalArgumentException("L'attività non può essere nulla.");
        }
        if(attivitaAssegnate.contains(a)){
            throw new IllegalArgumentException("L'attività è già assegnata a questo utente.");
        }
        attivitaAssegnate.add(a);
    }
    public boolean rimuoviAttivita(Attivita a){
        if(a == null || !attivitaAssegnate.contains(a)){
            throw new IllegalArgumentException("L'attività non è assegnata a questo utente.");
        }
        return attivitaAssegnate.remove(a);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Utente utente = (Utente) obj;
        return email.equals(utente.email);
    }
    @Override
    public int hashCode() {
        return email.hashCode();
    }


}
