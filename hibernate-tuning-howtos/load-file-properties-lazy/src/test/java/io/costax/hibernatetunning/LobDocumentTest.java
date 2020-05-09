package io.costax.hibernatetunning;

import io.costax.hibernatetunning.entities.Image;
import io.costax.hibernatetunning.entities.Message;
import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LobDocumentTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Before
    public void createAMessage() {
        provider.beginTransaction();
        final EntityManager em = provider.em();

        Message message = Message.of("Jcosta", "jsousa", "make it up", "Make it Up... and go on!!!");
        em.persist(message);

        final Image png = Image.png(FileContentReader.readAllBytes("example.png"));

        message.addImage(png);

        provider.commitTransaction();
    }

    @After
    public void removeAllDataTest() {
        final EntityManager em = provider.createdEntityManagerUnRuled();
        em.getTransaction().begin();
        em.createQuery("delete from Attachment").executeUpdate();
        em.createQuery("delete from Image").executeUpdate();
        em.createQuery("delete from Message").executeUpdate();
        em.getTransaction().commit();
    }

    @Test
    public void should_read() {
        final EntityManager em = provider.em();

        // To load the file array a transaction must be active
        // because the collumn is defined as
        // @Lob
        // @Basic(fetch = FetchType.LAZY)
        // @Column(name = "file", columnDefinition = "oid")
        provider.beginTransaction();

        final List<Image> images = em.createQuery("select i from Image i", Image.class).getResultList();

        Assert.assertThat(images, Matchers.not(Matchers.empty()));
        final Image image = images.get(0);
        Assert.assertThat(image, Matchers.notNullValue());
        Assert.assertThat(image.getType(), Matchers.is(Image.Type.PNG));

        final byte[] file = image.getFile();
        Assert.assertNotNull(file);

        System.out.println(file);
        Assert.assertNotNull(file);
        Assert.assertNotNull(file.length > 0);

        provider.commitTransaction();
    }

    @Test(expected = Exception.class)
    public void should_fail_read() {
        final EntityManager em = provider.createdEntityManagerUnRuled();
        final List<Image> images = em.createQuery("select i from Image i", Image.class).getResultList();

        Assert.assertThat(images, Matchers.not(Matchers.empty()));
        final Image image = images.get(0);
        Assert.assertThat(image, Matchers.notNullValue());
        Assert.assertThat(image.getType(), Matchers.is(Image.Type.PNG));

        System.out.println(image.getType());

        em.close();

        try {
            final byte[] file = image.getFile();
            System.out.println(new String(file));

            Assert.fail("should fail because the entityManager is closed");

        } catch (org.hibernate.LazyInitializationException e) {
            throw e;
        }
    }
}
