package io.costax.queryhintfetchsize;

import io.costax.model.SerieDocNum;
import io.costax.model.SerieDocument;
import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.hamcrest.Matchers;
import org.hibernate.FlushMode;
import org.hibernate.query.NativeQuery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class NativeSQLSynchronizedEntityClassTest {

    private static final int ID = 1;

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryHintFetchSizeTest.class);

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void name() {
        provider.doInTx(em -> {
            //SerieDocument serieDocument = em.find(SerieDocument.class, 1);
            SerieDocument serieDocument = getSerieDocument(ID, em);

            LOGGER.info("--- Original: [{}]", serieDocument);

            serieDocument.setIndicator(0);
            serieDocument.setName("ex" + serieDocument.getIndicator() * -1);

            LOGGER.info("--- Original: [{}]", serieDocument);

            Query getSerieDocNumQuery = em.createNativeQuery("select id as serieDocumentoId, name as title, indicator as numDoc " +
                            "from get_rec_nr_serie_document( :_id ) as datos(id int, indicator int, name varchar)",
                    "SerieDocNumMapping");

            getSerieDocNumQuery.unwrap(NativeQuery.class)
                    //.setFlushMode(FlushMode.ALWAYS)
                    .addSynchronizedEntityClass(SerieDocument.class);

            SerieDocNum serieDocNum = (SerieDocNum) getSerieDocNumQuery
                    .setParameter("_id", ID)
                    .getSingleResult();

            LOGGER.info("--- Returning of Execution of Function: [{}]", serieDocNum);
        });


        SerieDocument serieDocument = provider.em().find(SerieDocument.class, ID);

        Assert.assertThat(serieDocument.getIndicator(), Matchers.is(1));
    }

    private SerieDocument getSerieDocument(final int id, final EntityManager em) {
        return em.createQuery("select sd from SerieDocument sd where sd.id = :id", SerieDocument.class).setParameter("id", id).getSingleResult();
    }


}
