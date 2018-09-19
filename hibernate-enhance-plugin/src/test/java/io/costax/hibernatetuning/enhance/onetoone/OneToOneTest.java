package io.costax.hibernatetuning.enhance.onetoone;

import io.costax.hibernatetuning.enhance.model.Cc;
import io.costax.hibernatetuning.enhance.model.Document;
import io.costax.jpa.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

public class OneToOneTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void verifyMappings() {
        this.provider.beginTransaction();

        Cc cc = Cc.of("abcder abc");
        final Document doc = new Document();
        cc.setDocument(doc);

        provider.em().persist(cc);

        this.provider.commitTransaction();

    }
}
