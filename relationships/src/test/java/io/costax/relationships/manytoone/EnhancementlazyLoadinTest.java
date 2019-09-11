package io.costax.relationships.manytoone;

import io.costax.relationships.onetomany.Director;
import io.costax.relationships.onetomany.Movie;
import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

public class EnhancementlazyLoadinTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void name() {

        // 1111 movie


        final Movie movie = provider.em().find(Movie.class, 1111);

        System.out.println("Movie: " + movie.getTitle());

        System.out.println("******************************\n" +
                           "******************************\n");

        final Director director = movie.getDirector();

        System.out.println("director: " + director);
    }
}
