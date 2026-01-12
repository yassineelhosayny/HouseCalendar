package dataBase.pojo;

public class UtentePojo {

    private String nome;
    private String email;
    private String password;

    // costruttore 
    public UtentePojo() {

    }
    //+ costruttore completo
    public UtentePojo(String nome, String email, String password) {
        this.nome = nome;
        this.email = email;
        this.password = password;
    }

    // getter e setter
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
