package strategia;

import java.util.List;

import dominio.Attivita;

public class StrategiaOrdinamentoPerPriorita implements StrategiaOrdinamento {
    @Override
    public void ordina(List<Attivita> lista) {
        lista.sort((a1, a2) -> {
            if (a1 == null && a2 == null) return 0;
            if (a1 == null) return 1;
            if (a2 == null) return -1;

            //crescente
            return Integer.compare(a1.getPriorita(), a2.getPriorita());

            //o decrescente
                 // return Integer.compare(a2.getPriorita(), a1.getPriorita());
        });
    }
}
