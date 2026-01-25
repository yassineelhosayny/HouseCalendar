package dataBase.pojo;

public class UtentePojo {

    private String nome;
    private String email;
    private String password;
    private String passwordHash;
    private String passwordSalt;

    // costruttore 
    public UtentePojo() {

    }
    //+ costruttore completo
    public UtentePojo(String nome, String email, String password, String passwordHash, String passwordSalt) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }
}
