package io.costax.blob_and_clob;

import io.costax.files.FileSupport;
import io.costax.model.ArticleBlob;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ArticleBlobTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleBlobTest.class);

    @JpaContext
    public JpaProvider provider;

    @Test
    public void persist_and_fetch_lob_entity() {
        final byte[] sourceCover = FileSupport.readAllBytes("cover.png");
        final String sourceContent = FileSupport.readAllText("content_text.txt");

        provider.doInTx(em -> {
            final ArticleBlob articleBlob = ArticleBlob.createArticleBlob(1L, "Duke is the best", sourceContent, sourceCover);
            em.persist(articleBlob);
            em.flush();
        });

        // fetch the previous persisted instance;
        // we can see that all properties are load

        final ArticleBlob articleBlob = provider.doItWithReturn(em -> {

            LOGGER.info("Fetched the ArticleBlob [{}]", 1L);
            final ArticleBlob articleBlob1 = em.find(ArticleBlob.class, 1L);
            return articleBlob1;

        });

        LOGGER.info("Getting the ArticleBlob [{}] content [{}]", 1L, articleBlob.getContent());

        assertNotNull(articleBlob);
        assertEquals(articleBlob.getContent(), sourceContent);
        assertArrayEquals(articleBlob.getCover(), sourceCover);


    }
}