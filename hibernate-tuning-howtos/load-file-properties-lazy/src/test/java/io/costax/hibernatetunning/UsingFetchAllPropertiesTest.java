package io.costax.hibernatetunning;

import io.costax.files.FileSupport;
import io.costax.hibernatetunning.entities.Attachment;
import io.costax.hibernatetunning.entities.Message;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.annotations.QueryHints;
import org.junit.jupiter.api.*;

import java.util.UUID;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsingFetchAllPropertiesTest {

    @JpaContext
    JpaProvider provider;

    private int fileSize = 0;

    @BeforeEach
    void setUp() {
        provider.doInTx(em -> {

            Message message = Message.of("BF", "JC", "Certeficare", "certificate");
            em.persist(message);

            final byte[] file = FileSupport.readAllBytes("certificate-pkcs8.der");
            fileSize = file.length;
            Attachment attachment = Attachment.of(null, message, "xx Cerificates", "X certificate", file);

            message.addAttachment(attachment);

            em.flush();

        });
    }

    @AfterEach
    void tearDown() {
        provider.doInTx(em -> {
            em.createQuery("delete from Attachment").executeUpdate();
            em.createQuery("delete from Image").executeUpdate();
            em.createQuery("delete from Message").executeUpdate();
        });
    }

    @Test
    public void load_message_with_all_child_properties_including_the_attachment_file() {

        final Message message = provider.doItWithReturn(em -> {

            @SuppressWarnings("SqlResolve")
            final UUID id = (UUID)
                    em.createNativeQuery("select distinct id from communication.message")
                            .setHint(QueryHints.FETCH_SIZE, 1)
                            .setMaxResults(1)
                            .getSingleResult();

            return em
                    .createQuery("select m from Message m join fetch m.attachments fetch all properties where m.id = :id", Message.class)
                    .setParameter("id", id)
                    //.setHint(QueryHints.FETCH_SIZE, 1)
                    //.setMaxResults(1)
                    .getSingleResult();
        });


        Assertions.assertNotNull(message);
        final byte[] file = message.getAttachments().get(0).getFile();
        Assertions.assertEquals(fileSize, file.length);
    }

    @Test
    public void load_the_attachment_file() {
        final byte[] ob = provider.doItWithReturn(em -> {


            @SuppressWarnings("SqlResolve")
            final UUID id = (UUID)
                    em.createNativeQuery("select distinct id from communication.attachment")
                            .setHint(QueryHints.FETCH_SIZE, 1)
                            .setMaxResults(1)
                            .getSingleResult();

            final byte[] ids = em
                    .createQuery("select a.file from Attachment a where a.id = :id", byte[].class)
                    .setParameter("id", id)
                    .getSingleResult();

            return ids;
        });

        Assertions.assertNotNull(ob);
        Assertions.assertEquals(fileSize, ob.length);
    }
}
