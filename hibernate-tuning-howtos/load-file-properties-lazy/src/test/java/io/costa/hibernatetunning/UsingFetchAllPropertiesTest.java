package io.costa.hibernatetunning;

import io.costa.hibernatetunning.entities.Attachment;
import io.costa.hibernatetunning.entities.Message;
import io.costax.rules.EntityManagerProvider;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.UUID;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class UsingFetchAllPropertiesTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void c_fetchAllProperties() {
        final UUID uuid = UUID.fromString("8d96d50d-b675-4c54-a1d4-13a94539271e");

        final Message message = provider.em()
                .createQuery("select m from Message m join fetch m.attachments fetch all properties where m.id = :id", Message.class)
                .setParameter("id", uuid)
                .getSingleResult();

        Assert.assertNotNull(message);
        Assert.assertEquals(635, message.getAttachments().get(0).getFile().length);
    }

    @Test
    public void b_loadBytesFile() {
        byte[] ob = provider.em()
                .createQuery("select a.file from Attachment a where a.id = :id", byte[].class)
                .setParameter("id", UUID.fromString("6d12c876-5b1b-4c73-ab73-16d04444ad73"))
                .getSingleResult();

        Assert.assertNotNull(ob);
        Assert.assertEquals(635, ob.length);
    }

    @Test
    public void a_example() {
        provider.beginTransaction();

        Message message = Message.of("BF", "JC", "Certeficare", "certificate");
        provider.em().persist(message);

        Attachment attachment = Attachment.of(null, message, "xx Cerificates", "X certificate", FileContentReader.readAllBytes("certificate-pkcs8.der"));

        message.addAttachment(attachment);

        provider.commitTransaction();
    }
}
