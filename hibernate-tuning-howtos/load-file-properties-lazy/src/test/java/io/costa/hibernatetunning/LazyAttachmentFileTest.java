package io.costa.hibernatetunning;

import io.costa.hibernatetunning.entities.Attachment;
import io.costa.hibernatetunning.entities.Message;
import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LazyAttachmentFileTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @After
    public void after() {
        provider.beginTransaction();
        provider.em().createQuery("delete from Attachment").executeUpdate();
        provider.em().createQuery("delete from Image").executeUpdate();
        provider.em().createQuery("delete from Message").executeUpdate();
        provider.commitTransaction();
    }

    @Before
    public void before() {
        provider.beginTransaction();
        final EntityManager em = provider.em();

        Message message = Message.of("Jcosta", "jsousa", "make it up", "Make it Up... and go on!!!");
        em.persist(message);

        final Attachment attachment = em.merge(Attachment.of(
                UUIDs.ONE,
                message,
                "JPA-States-Diagram",
                "JPA-States-Diagram with all Jpa trasiction status",
                FileContentReader.readAllBytes("example.png")));

        message.addAttachment(attachment);


        final Attachment originalUseCase = em.merge(Attachment.of(
                UUIDs.TWO,
                message,
                "Original Use Case",
                "Original Use Case from the Ivar Jacobson 'Object-oriented Software Engineering: A Use Case Driven Approach'",
                FileContentReader.readAllBytes("example.png")));

        //em.persist(originalUseCase);
        message.addAttachment(originalUseCase);

        provider.commitTransaction();
    }

    @Test
    public void b_loadFile() {
        final Attachment attachment = provider.em().createQuery("select a from Attachment a where a.fileName = :fileName", Attachment.class)
                .setParameter("fileName", "JPA-States-Diagram")
                .getSingleResult();

        assertThat(attachment, notNullValue());
        assertThat(attachment.getFileName(), is("JPA-States-Diagram"));

        final Session unwrap = provider.em().unwrap(Session.class);
        final Statistics statistics = unwrap.getSessionFactory().getStatistics();
        final long queryExecutionCount = statistics.getQueryExecutionCount();
        // assertThat(queryExecutionCount, is(1L));

        final byte[] file = attachment.getFile();
        final Statistics statistics1 = unwrap.getSessionFactory().getStatistics();
        //assertThat(statistics1.getQueryExecutionCount(), is(2L));

        System.out.println(file);
    }

    @Test
    public void c_load_all_file() {
        final List<Attachment> attachments = provider.em().createQuery("select a from Attachment a", Attachment.class).getResultList();

        assertThat(attachments, Matchers.hasSize(2));
    }


}