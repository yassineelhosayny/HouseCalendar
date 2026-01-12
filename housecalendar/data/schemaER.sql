CREATE TABLE IF NOT EXISTS utente (
  email TEXT PRIMARY KEY,
  nome TEXT NOT NULL,
  password TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS attivita (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  descrizione TEXT NOT NULL,
  tipo TEXT NOT NULL,
  data_inizio TEXT NOT NULL,
  data_fine TEXT NOT NULL,
  data_notifica TEXT NOT NULL,
  priorita INTEGER NOT NULL,
  attivita_privata INTEGER NOT NULL,   -- 0 o 1
  context TEXT,                        -- negozioo materia o stanza
  utente_email TEXT NOT NULL,
   FOREIGN KEY (utente_email) REFERENCES utente(email)
);
