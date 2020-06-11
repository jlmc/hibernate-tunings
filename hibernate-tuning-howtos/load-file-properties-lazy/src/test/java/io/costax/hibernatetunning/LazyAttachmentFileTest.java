package io.costax.hibernatetunning;

import io.costax.files.FileSupport;
import io.costax.hibernatetunning.entities.Attachment;
import io.costax.hibernatetunning.entities.Message;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LazyAttachmentFileTest {

    @JpaContext
    JpaProvider provider;

    @PersistenceContext
    EntityManager em;

    @AfterEach
    void tearDown() {
        provider.doInTx(em -> {

            em.createQuery("delete from Attachment").executeUpdate();
            em.createQuery("delete from Image").executeUpdate();
            em.createQuery("delete from Message").executeUpdate();

        });
    }

    @BeforeEach
    void setUp() {
        provider.doInTx(em -> {

            Message message = Message.of("Jcosta", "jsousa", "make it up", "Make it Up... and go on!!!");
            em.persist(message);

            final Attachment attachment = em.merge(Attachment.of(
                    UUIDs.ONE,
                    message,
                    "JPA-States-Diagram",
                    "JPA-States-Diagram with all Jpa trasiction status",
                    FileSupport.readAllBytes("example.png")));

            message.addAttachment(attachment);


            final Attachment originalUseCase = em.merge(Attachment.of(
                    UUIDs.TWO,
                    message,
                    "Original Use Case",
                    "Original Use Case from the Ivar Jacobson 'Object-oriented Software Engineering: A Use Case Driven Approach'",
                    FileSupport.readAllBytes("example.png")));

            //em.persist(originalUseCase);
            message.addAttachment(originalUseCase);

        });
    }

    @Test
    @Order(0)
    public void load_file() {

        final Attachment attachment =
                em.createQuery("select a from Attachment a where a.fileName = :fileName", Attachment.class)
                        .setParameter("fileName", "JPA-States-Diagram")
                        .getSingleResult();

        Assertions.assertNotNull(attachment);
        Assertions.assertEquals("JPA-States-Diagram", attachment.getFileName());

        final Session unwrap = em.unwrap(Session.class);
        final Statistics statistics = unwrap.getSessionFactory().getStatistics();
        final long queryExecutionCount = statistics.getQueryExecutionCount();
        // assertThat(queryExecutionCount, is(1L));

        final byte[] file = attachment.getFile();
        final Statistics statistics1 = unwrap.getSessionFactory().getStatistics();
        //assertThat(statistics1.getQueryExecutionCount(), is(2L));

        System.out.println(Arrays.toString(file));
    }

    @Test
    @Order(1)
    public void load_all_file() {
        final List<Attachment> attachments =
                em.createQuery("select a from Attachment a", Attachment.class)
                        .getResultList();

        Assertions.assertEquals(2, attachments.size());
    }

}