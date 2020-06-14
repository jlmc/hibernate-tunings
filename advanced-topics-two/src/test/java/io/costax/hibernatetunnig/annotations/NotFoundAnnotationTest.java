package io.costax.hibernatetunnig.annotations;

import io.costax.hibernatetunnig.entities.Developer;
import io.costax.hibernatetunnig.entities.Machine;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.hql.internal.NameGenerator;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Tuple;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link NotFound} in hibernate is used when entities are associated to each other by ManyToOne, OneToMany etc. Suppose joined subclass has no data related to any id due to some database inconsistency. And we do not want to throw error, in this case @NotFound helps us. If we use @NotFound, then for any id if there is no data in associated joined subclass, error will not be thrown.
 * {@link NotFound} has two action {@link NotFoundAction#IGNORE} and {@link NotFoundAction#EXCEPTION}.
 *
 * NOTE: The use of annotation {@link NotFound} may have the consequence the following warning:
 *
 *  - {@code WARN  [org.hibernate.cfg.AnnotationBinder] - HHH000491: The [developer] association in the [Machine] entity uses both @NotFound(action = NotFoundAction.IGNORE) and FetchType.LAZY.
 *     The NotFoundAction.IGNORE @ManyToOne and @OneToOne associations are always fetched eagerly.}
 */
@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class NotFoundAnnotationTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(NotFoundAnnotationTest.class);

    @JpaContext
    public JpaProvider provider;

    @Test
    @DisplayName("Normal mapping record with existing association record, valid foreigner key")
    public void should_found() {

        LOGGER.info(">>>>==========================>>>>");

        final Machine machine = provider.doItWithReturn(em -> em.find(Machine.class, 1));

        assertNotNull(machine);
        assertNotNull(machine.getDeveloper());

        LOGGER.info("<<<<==========================<<<<");
    }

    @Test
    @DisplayName("How to fix not mapped associated records, invalid FOREIGNER KEY using Hibernate NotFound Annotation")
    public void should_ignore_not_found_relationship() {

        LOGGER.info(">>>>==========================>>>>");

        provider.doIt(em -> {

            final List<Tuple> machinesWithNotFoundDeveloper =
                    em.createNativeQuery("""
                    select m.ID as machine_id, m.DEVELOPER_ID as developer_id
                    from MACHINE m 
                    left join DEVELOPER d on m.DEVELOPER_ID = d.ID
                    where d.ID is null
                    order by m.ID
                    """, Tuple.class)
                    .getResultList();

            assertEquals(1, machinesWithNotFoundDeveloper.size());
            final Tuple tuple = machinesWithNotFoundDeveloper.get(0);
            final Integer machineId = tuple.get("MACHINE_ID", Integer.class);
            final Integer developerId = tuple.get("DEVELOPER_ID", Integer.class);

            assertEquals(5, machineId);
            assertEquals(8, developerId);

            LOGGER.info("=====> Find Machine '{}' with invalid FOREIGNER KEY for developer '{}'", machineId, developerId);

            // 16:07:43,377 WARN  [org.hibernate.cfg.AnnotationBinder] - HHH000491: The [developer] association in the [io.costax.hibernatetunnig.entities.Machine] entity uses both @NotFound(action = NotFoundAction.IGNORE) and FetchType.LAZY. The NotFoundAction.IGNORE @ManyToOne and @OneToOne associations are always fetched eagerly.
            // Two queries are performed when the records have invalid FOREIGNER KEY or the ManyToOne or OneToOne is mapped with lazy
            // The NotFoundAction.IGNORE @ManyToOne and @OneToOne associations are always fetched eagerly.
            // 1. select * from Machine m left join Developer d on d.id=m.developer_id where m.id = 5
            // 2 select * from Developer where id = 8
            final Machine machine = em.find(Machine.class, machineId);
            assertNotNull(machine);
            assertNull(machine.getDeveloper());

            LOGGER.info("=====> Try Find Not existing Developer '{}'", developerId);
            final Developer developer = em.find(Developer.class, developerId);
            assertNull(developer);
        });


        LOGGER.info("<<<<==========================<<<<");

    }
}