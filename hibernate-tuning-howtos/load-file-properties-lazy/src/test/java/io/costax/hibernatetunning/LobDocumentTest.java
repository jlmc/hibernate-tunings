package io.costax.hibernatetunning;

import io.costax.files.FileSupport;
import io.costax.hibernatetunning.entities.Image;
import io.costax.hibernatetunning.entities.Message;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LobDocumentTest {

    @JpaContext
    public JpaProvider provider;

    @BeforeEach
    void setUp() {
        provider.doInTx(em -> {

            Message message = Message.of("Jcosta",
                    "jsousa",
                    "make it up",
                    "Make it Up... and go on!!!");

            em.persist(message);

            final Image png = Image.png(FileSupport.readAllBytes("example.png"));

            message.addImage(png);
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
    @Order(0)
    public void read() {
        // To load the file array a transaction must be active
        // because the collumn is defined as
        // @Lob
        // @Basic(fetch = FetchType.LAZY)
        // @Column(name = "file", columnDefinition = "oid")

        provider.doInTx(em -> {


            final List<Image> images = em.createQuery("select i from Image i", Image.class).getResultList();

            Assertions.assertFalse(images.isEmpty());
            final Image image = images.get(0);
            Assertions.assertNotNull(image);
            Assertions.assertSame(image.getType(), Image.Type.PNG);

            final byte[] file = image.getFile();
            Assertions.assertNotNull(file);

            System.out.println(Arrays.toString(file));
            Assertions.assertNotNull(file);
            Assertions.assertTrue(file.length > 0);
        });

    }

    @Test
    public void should_fail_read() {

        final Image imageSaved =
                provider.doItWithReturn(em -> {


                    final List<Image> images = em.createQuery("select i from Image i", Image.class).getResultList();

                    Assertions.assertFalse(images.isEmpty());
                    final Image image = images.get(0);
                    Assertions.assertNotNull(image);
                    Assertions.assertSame(Image.Type.PNG, image.getType());

                    System.out.println(image.getType());

                    return image;
                });


        Assertions.assertThrows(
                LazyInitializationException.class,
                () -> {

                    final byte[] file = imageSaved.getFile();
                    System.out.println(new String(file));
                    fail("should fail because the entityManager is closed");

                });

    }
}
