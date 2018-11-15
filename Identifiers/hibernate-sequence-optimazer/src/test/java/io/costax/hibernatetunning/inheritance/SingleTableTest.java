package io.costax.hibernatetunning.inheritance;

import io.costa.hibernatetunings.entities.financial.Board;
import io.costa.hibernatetunings.entities.financial.CreditNote;
import io.costa.hibernatetunings.entities.financial.FinancialDocument;
import io.costa.hibernatetunings.entities.financial.Invoice;
import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SingleTableTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void a_shouldGetAllAllFinancialDocumentsUsingAPolymorphicJPQL() {
        provider.em().createQuery("select fd from FinancialDocument fd",
                FinancialDocument.class).getResultList();
    }

    @Test
    public void b_shouldCreateABordWithFiancialDocuments() {
        final ZoneOffset offset = OffsetDateTime.now().getOffset();
        final OffsetDateTime expirationOn = OffsetDateTime.of(LocalDate.of(2020, Month.JANUARY, 01), LocalTime.MIN, offset);

        provider.beginTransaction();
        final EntityManager em = provider.em();

        final Board teamBone = Board.of("t-Bone", "Team Bone");
        teamBone.add(Invoice.of(BigDecimal.valueOf(10), "Abc"));
        teamBone.add(Invoice.of(BigDecimal.valueOf(13), "Cder"));
        teamBone.add(CreditNote.of(BigDecimal.valueOf(8), expirationOn));

        em.persist(teamBone);

        provider.commitTransaction();
    }

    @Test
    public void c_shouldCreateABordWithFinancialDocuments() {
        //final ZoneOffset offset = OffsetDateTime.now().getOffset();
        //final OffsetDateTime expirationOn = OffsetDateTime.of(LocalDate.of(2020, Month.JANUARY, 01), LocalTime.MIN, offset);

        provider.beginTransaction();
        final EntityManager em = provider.em();

        final Board ptechAcademy = Board.of("PAcademy", "Ptech-Academy");
        ptechAcademy.add(Invoice.of(BigDecimal.valueOf(120), "idea"));

        em.persist(ptechAcademy);

        provider.commitTransaction();
    }

    @Test
    public void d_should_get_all_the_financialDocuments_the_tbone_board() {
        List<FinancialDocument> tboneFinancialDocuments =
                provider.em()
                        .createQuery("select fd from FinancialDocument fd join fetch fd.board b where b.code = lower(:code) order by fd.class", FinancialDocument.class)
                        .setParameter("code", "t-bone")
                        .getResultList();

        Assert.assertThat(tboneFinancialDocuments, Matchers.hasSize(3));

        final List<FinancialDocument> invoices = tboneFinancialDocuments.stream().filter(f -> f instanceof Invoice).collect(Collectors.toList());
        final List<FinancialDocument> creditNotes = tboneFinancialDocuments.stream().filter(f -> f instanceof CreditNote).collect(Collectors.toList());

        Assert.assertThat(invoices, Matchers.hasSize(2));
        Assert.assertThat(creditNotes, Matchers.hasSize(1));

    }

    //@Test(expected = org.hibernate.exception.ConstraintViolationException.class)
    @Test(expected = javax.persistence.RollbackException.class)
    public void e_should_not_add_invoice_without_content() {
        try {
            provider.beginTransaction();

            final Session unwrap = provider.em().unwrap(Session.class);
            //final Board tbone = unwrap.byNaturalId(Board.class).using("code", "t-bone").getReference();
            //Board b = unwrap.byNaturalId(Board.class).using(Board_.code, "t-bone").load();
            //Board b = unwrap.byNaturalId(Board.class).using(Board_.code, "t-bone").with(LockOptions.UPGRADE).load();
            //Board b = unwrap.bySimpleNaturalId(Board.class).with(LockOptions.UPGRADE).load("t-bone");
            final Board tbone = unwrap.bySimpleNaturalId(Board.class).load("t-bone");

            tbone.add(Invoice.of(BigDecimal.valueOf(92.9), null));

            provider.commitTransaction();
        } catch (RollbackException e) {
            final Throwable cause = e.getCause();

            if (!org.hibernate.exception.ConstraintViolationException.class.isAssignableFrom(cause.getCause().getClass())) {
                Assert.fail("Wrong cause, different from: org.hibernate.exception.ConstraintViolationException");
            }

            throw e;
        }
    }

    @Test
    public void z_should_delete_all_using_polymorphic_delete_jpql() {
        provider.beginTransaction();
        final EntityManager em = provider.em();

        em.createQuery("delete from FinancialDocument fd").executeUpdate();

        em.createQuery("delete from Board ").executeUpdate();

        provider.commitTransaction();
    }
}
