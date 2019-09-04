package io.costax.model;

import io.costax.files.FileContentReader;
import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.hibernate.engine.jdbc.BlobProxy;
import org.hibernate.engine.jdbc.ClobProxy;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.*;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class ArticleClobTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleClobTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");


    @Test
    public void persist_and_fetch_lob_entity() {
        final String sourceContent = FileContentReader.readAllText("content_text.txt");
        final byte[] sourceCover = FileContentReader.readAllBytes("cover.png");
        //InputStream content = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));

        provider.doInTx(em -> {
            final ArticleClob articleClob = ArticleClob.createArticleClob(1L, "Duke is the best");

            articleClob.setCover(BlobProxy.generateProxy(sourceCover));
            articleClob.setContent(ClobProxy.generateProxy(sourceContent));

            em.persist(articleClob);
            em.flush();
        });


        // fetch the previous persisted instance;
        // we can see that all properties are load
        // prevent: java.lang.IllegalStateException: org.postgresql.util.PSQLException: Large Objects may not be used in auto-commit mode.
        final EntityManager em = provider.em();
        provider.beginTransaction();

        final ArticleClob articleClob = em.find(ArticleClob.class, 1L);

        LOGGER.info("Fetched the ArticleClob [{}]", 1L);

        assertNotNull(articleClob);
        LOGGER.info("Getting the ArticleClob [{}] content [{}]", 1L, articleClob.getContent());


        //Book b2 = em.find(Book.class, b.getId());
        //Reader charStream = b2.getContent().getCharacterStream();
        //InputStream binaryStream = b2.getCover().getBinaryStream();

        final String content = getContent(articleClob);
        assertThat(content, Matchers.equalTo(sourceContent));

        byte[] cover = getCover(articleClob);
        assertArrayEquals(cover, sourceCover);

        provider.rollbackTransaction();
    }

    private byte[] getCover(final ArticleClob articleClob) {
        try (InputStream binaryStream = articleClob.getCover().getBinaryStream()) {

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = binaryStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            return buffer.toByteArray();

        } catch (SQLException | IOException e) {
            throw new IllegalStateException(e);
        }

    }

    private String getContent(ArticleClob articleClob) {
        try (final Reader characterStream = articleClob.getContent().getCharacterStream();
             BufferedReader bufferReader = new BufferedReader(characterStream)) {

            StringBuilder stringBuilder = new StringBuilder();

            String str;
            while ((str = bufferReader.readLine()) != null) {
                stringBuilder.append(str).append("\n");
            }

            final String s = stringBuilder.toString();
            return s.substring(0, s.length() - 1);

        } catch (SQLException | IOException e) {
            throw new IllegalStateException(e);
        }
    }
}