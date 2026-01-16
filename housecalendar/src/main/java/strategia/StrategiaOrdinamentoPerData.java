package strategia;

import java.time.LocalDateTime;
import java.util.List;

import dominio.Attivita;

public class StrategiaOrdinamentoPerData implements StrategiaOrdinamento {
    @Override
    public void ordina(List<Attivita> lista) {
        lista.sort((a1, a2) -> {
            LocalDateTime inizio1 = (a1 == null) ? null : a1.getDataInizio();
            LocalDateTime inizio2 = (a2 == null) ? null : a2.getDataInizio();

            if (inizio1 == null && inizio2 == null) return 0; // entrambe null,=> sono "uguali"
            if (inizio1 == null) return 1;   /// se a1 null va dopo a2
            if (inizio2 == null) return -1;   //se a2 null va dopo a1

            return inizio1.compareTo(inizio2);
        });
    }
}
