package io.costa.hibernatetunning;

import io.costa.hibernatetunning.entities.Attachment;
import io.costa.hibernatetunning.entities.Message;
import io.costax.rules.EntityManagerProvider;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import java.util.UUID;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UsingFetchAllPropertiesTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    private int fileSize = 0;

    @Before
    public void before() {
        final EntityManager entityManager = provider.createdEntityManagerUnRuled();
        entityManager.getTransaction().begin();

        Message message = Message.of("BF", "JC", "Certeficare", "certificate");
        entityManager.persist(message);

        final byte[] file = FileContentReader.readAllBytes("certificate-pkcs8.der");
        fileSize = file.length;
        Attachment attachment = Attachment.of(null, message, "xx Cerificates", "X certificate", file);

        message.addAttachment(attachment);

        entityManager.flush();

        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @After
    public void after() {
        final EntityManager entityManager = provider.createdEntityManagerUnRuled();
        entityManager.getTransaction().begin();
        entityManager.createQuery("delete from Attachment").executeUpdate();
        entityManager.createQuery("delete from Image").executeUpdate();
        entityManager.createQuery("delete from Message").executeUpdate();
        entityManager.getTransaction().commit();
    }

    @Test
    public void should_load_message_with_all_child_properties_including_the_attachment_file() {

        final EntityManager entityManager = provider.createdEntityManagerUnRuled();
        final UUID id = (UUID) entityManager.createNativeQuery("select distinct id from communication.message limit 1").getSingleResult();

        final Message message = entityManager
                .createQuery("select m from Message m join fetch m.attachments fetch all properties where m.id = :id", Message.class)
                .setParameter("id", id)
                .getSingleResult();

        entityManager.close();

        Assert.assertNotNull(message);
        final byte[] file = message.getAttachments().get(0).getFile();
        Assert.assertEquals(fileSize, file.length);
    }

    @Test
    public void should_load_the_attachment_file() {
        final UUID id = (UUID) provider.em().createNativeQuery("select distinct id from communication.attachment limit 1").getSingleResult();

        byte[] ob = provider.em()
                .createQuery("select a.file from Attachment a where a.id = :id", byte[].class)
                .setParameter("id", id)
                .getSingleResult();

        Assert.assertNotNull(ob);
        Assert.assertEquals(fileSize, ob.length);
    }
}
