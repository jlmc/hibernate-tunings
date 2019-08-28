package io.costax.resourcebundle;

import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;

public class DbResourceBoundleMappingTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void create_sample_beer() {
        provider.doInTx(em -> {
            final Beer pilsener = Beer.of(1L, "Pilsener", new BigDecimal("1.55"));

            em.persist(pilsener);
            em.flush();

            pilsener.addLocalization("en", "description", "Pilsner (also pilsener or simply pils) is a type of pale lager. It takes its name from the Czech city of Pilsen");
            pilsener.addLocalization("pt", "description", "Pilsner (também pilsener ou simplesmente pils) é um tipo de cerveja clara. Leva o nome da cidade checa de Pilsen");
            pilsener.addLocalization("en", "history", "Pilsner (also pilsener or simply pils) is a type of pale lager. It takes its name from the Czech city of Pilsen");

            em.flush();
        });

        provider.doInTx(em -> {
            final Beer beer = em.find(Beer.class, 1L);
            beer.removeLocalization("history");
            em.flush();

            em.remove(beer);

            em.flush();
        });
    }
}