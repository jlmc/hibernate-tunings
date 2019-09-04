package io.costax.model;

import io.costax.files.FileContentReader;
import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

public class ArticleBlobTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleBlobTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void persist_and_fetch_lob_entity() {
        final byte[] sourceCover = FileContentReader.readAllBytes("cover.png");
        final String sourceContent = FileContentReader.readAllText("content_text.txt");

        provider.doInTx(em -> {
            final ArticleBlob articleBlob = ArticleBlob.createArticleBlob(1L, "Duke is the best", sourceContent, sourceCover);
            em.persist(articleBlob);
            em.flush();
        });

        // fetch the previous persisted instance;
        // we can see that all properties are load
        final EntityManager em = provider.em();

        final ArticleBlob articleBlob = em.find(ArticleBlob.class, 1L);

        LOGGER.info("Fetched the ArticleBlob [{}]", 1L);

        assertNotNull(articleBlob);
        LOGGER.info("Getting the ArticleBlob [{}] content [{}]", 1L, articleBlob.getContent());
        assertThat(articleBlob.getContent(), Matchers.equalTo(sourceContent));
        assertArrayEquals(articleBlob.getCover(), sourceCover);
    }
}