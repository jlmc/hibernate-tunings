package io.costax.relationships.manytoone;

import io.costax.relationships.manytomany.Post;
import io.costax.relationships.manytomany.Tag;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ManyToManyBidirectionalTest {

    private static final int JAVA_EE_ID = 1;
    private static final int REACT_ID = 2;
    private static final int HIBENATE_ID = 3;
    private static final int MANEGMENT_ID = 4;
    private static final int IT_ID = 5;
    private static final Tag JAVA_EE = Tag.of(JAVA_EE_ID, "Java-EE");
    private static final Tag REACT = Tag.of(REACT_ID, "React");
    private static final Tag HIBERNATE = Tag.of(HIBENATE_ID, "Hibernate");
    private static final Tag MANEGMENT = Tag.of(MANEGMENT_ID, "MANEGMENT");
    private static final Tag IT = Tag.of(IT_ID, "IT");

    @JpaContext
    public JpaProvider provider;

    @Test
    public void should_create_and_manager_post_tags() {
        // create some tags
        provider.doInTx(em -> {
            em.persist(JAVA_EE);
            em.persist(REACT);
            em.persist(HIBERNATE);
            em.persist(MANEGMENT);
            em.persist(IT);
        });

        // create some Post
        provider.doInTx(em -> {
            final Post post1 = Post.of(1, "How to map @many to many with JPA");
            post1.addTag(em.find(Tag.class, IT_ID));
            post1.addTag(em.find(Tag.class, REACT_ID));
            em.persist(post1);

            final Post post2 = Post.of(2, "Simple Hello app.js");
            post2.addTag(em.find(Tag.class, REACT_ID));
            em.persist(post2);


            final Post post3 = Post.of(3, "Team coach");
            post3.addTag(em.find(Tag.class, MANEGMENT_ID));
            em.persist(post3);


            final Post post4 = Post.of(4, "Jax-rs Verbs");
            post4.addTag(em.find(Tag.class, JAVA_EE_ID));
            em.persist(post4);

            em.flush();
        });

        // remove some Tag from the Post and add some others
        provider.doInTx(em -> {
            final Post post1 = em.find(Post.class, 1);

            post1.removeTag(em.find(Tag.class, REACT_ID));

            post1.addTag(em.find(Tag.class, JAVA_EE_ID));
            post1.addTag(em.find(Tag.class, HIBENATE_ID));

            em.flush();
        });

        // How many Tags have the post1?
        provider.doIt(em -> {

            final Post post1 = em.find(Post.class, 1);

            assertEquals(3, post1.getTags().size());
            assertTrue(post1.getTags().containsAll(List.of(IT, JAVA_EE, HIBERNATE)));

            // how many post have the Tag Java EE
            final Tag javaEE = em.find(Tag.class, JAVA_EE_ID);
            assertEquals(2, javaEE.getPosts().size());

        });


    }
}
