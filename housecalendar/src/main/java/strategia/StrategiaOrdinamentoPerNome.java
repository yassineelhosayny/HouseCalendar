package strategia;


import java.util.List;

import dominio.Attivita;

public class StrategiaOrdinamentoPerNome implements StrategiaOrdinamento {
    @Override
    public void ordina(List<Attivita> lista) {
        lista.sort((a1, a2) -> {
            String desc1 = (a1 == null) ? null : a1.getDescrizione();
            String desc2 = (a2 == null) ? null : a2.getDescrizione();

            if (desc1 == null && desc2 == null) return 0;
            if (desc1 == null) return 1;   // null in fondo
            if (desc2 == null) return -1;

            return desc1.compareToIgnoreCase(desc2);
        });
    }
}
