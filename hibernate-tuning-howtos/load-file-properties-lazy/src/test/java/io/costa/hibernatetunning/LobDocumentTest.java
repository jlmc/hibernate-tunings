package io.costa.hibernatetunning;

import io.costa.hibernatetunning.entities.Image;
import io.costa.hibernatetunning.entities.Message;
import io.costax.rules.EntityManagerProvider;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;

import static io.costa.hibernatetunning.FileContentReader.readAllBytes;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LobDocumentTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void a_create() {
        provider.beginTransaction();
        final EntityManager em = provider.em();

        Message message = Message.of("Jcosta", "jsousa", "make it up", "Make it Up... and go on!!!");
        em.persist(message);


        final Image png = Image.png(readAllBytes("example.png"));

        message.addImage(png);


        provider.commitTransaction();
    }

    @Test
    public void b_read() {
        provider.beginTransaction();
        final Image document = provider.em().find(Image.class, 1);

        System.out.println(document.getType());
        provider.commitTransaction();
    }
}
