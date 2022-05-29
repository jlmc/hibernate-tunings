package io.costax.queryhintfetchsize;

import io.costax.model.SerieDocNum;
import io.costax.model.SerieDocument;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JpaTest(persistenceUnit = "it")
public class NativeSQLSynchronizedEntityClassTest {

    private static final int ID = 1;

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryHintFetchSizeTest.class);


    @JpaContext
    public JpaProvider provider;

    @Test
    public void name() {
        provider.doInTx(em -> {
            //SerieDocument serieDocument = em.find(SerieDocument.class, 1);
            SerieDocument serieDocument = getSerieDocument(ID, em);

            LOGGER.info("--- Original: [{}]", serieDocument);

            serieDocument.setIndicator(0);
            serieDocument.setName("ex" + serieDocument.getIndicator() * -1);

            LOGGER.info("--- Original: [{}]", serieDocument);

            Query getSerieDocNumQuery =
                    em.createNativeQuery(
                            """
                            select id as serieDocumentoId, name as title, indicator as numDoc 
                            from get_rec_nr_serie_document( :_id ) as datos(id int, indicator int, name varchar)
                            ""","SerieDocNumMapping");

            getSerieDocNumQuery.unwrap(NativeQuery.class)
                    //.setFlushMode(FlushMode.ALWAYS)
                    .addSynchronizedEntityClass(SerieDocument.class);

            SerieDocNum serieDocNum = (SerieDocNum) getSerieDocNumQuery
                    .setParameter("_id", ID)
                    .getSingleResult();

            LOGGER.info("--- Returning of Execution of Function: [{}]", serieDocNum);
        });

        SerieDocument serieDocument = provider.doItWithReturn(em -> em.find(SerieDocument.class, ID));
        Assertions.assertEquals(1, serieDocument.getIndicator());
    }

    private SerieDocument getSerieDocument(final int id, final EntityManager em) {
        return em.createQuery("select sd from SerieDocument sd where sd.id = :id", SerieDocument.class)
                .setParameter("id", id)
                .getSingleResult();
    }


}
