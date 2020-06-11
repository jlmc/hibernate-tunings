package io.costax.relationships.columntransformer;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql(statements = "delete from t_document")
public class ColumnTransformerTest {

    @JpaContext
    public JpaProvider provide;

    @Test
    public void should_transform_column_content_to_bytes() {
        final UUID uuid = UUID.randomUUID();

        provide.doInTx(em -> {
            Document document = new Document(uuid, "isto é testes", "dummy");
            em.persist(document);
            em.flush();
        });


        final Document document = provide.doItWithReturn(em -> em.find(Document.class, uuid));

        assertEquals("dummy", document.getSignature());
    }
}