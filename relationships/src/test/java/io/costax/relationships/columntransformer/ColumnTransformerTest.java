package io.costax.relationships.columntransformer;

import io.costax.rules.EntityManagerProvider;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.util.UUID;

public class ColumnTransformerTest {

    @Rule
    public EntityManagerProvider provide = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void should_transform_column_content_to_bytes() {
        final UUID uuid = UUID.randomUUID();

        provide.doInTx(em -> {
            Document document = new Document(uuid, "isto é testes", "dummy");
            em.persist(document);
            em.flush();
        });

        final Document document1 = provide.em().find(Document.class, uuid);
        Assert.assertEquals("dummy", document1.getSignature());
    }
}