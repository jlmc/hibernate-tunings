package io.costax.hibernatetunning.inheritance;

import io.costax.hibernatetunings.entities.financial.Board;
import io.costax.hibernatetunings.entities.financial.CreditNote;
import io.costax.hibernatetunings.entities.financial.FinancialDocument;
import io.costax.hibernatetunings.entities.financial.Invoice;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.RollbackException;
import org.hibernate.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@JpaTest(persistenceUnit = "it")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class SingleTableTest {

    @PersistenceContext
    public EntityManager em;

    @Test
    public void t00_shouldGetAllAllFinancialDocumentsUsingAPolymorphicJPQL() {
        em.createQuery("select fd from FinancialDocument fd", FinancialDocument.class)
                .getResultList();
    }

    @Test
    public void t01_shouldCreateABordWithFiancialDocuments() {
        final ZoneOffset offset = OffsetDateTime.now().getOffset();
        final OffsetDateTime expirationOn = OffsetDateTime.of(LocalDate.of(2020, Month.JANUARY, 01), LocalTime.MIN, offset);

        em.getTransaction().begin();

        final Board teamBone = Board.of("t-Bone", "Team Bone");
        teamBone.add(Invoice.of(BigDecimal.valueOf(10), "Abc"));
        teamBone.add(Invoice.of(BigDecimal.valueOf(13), "Cder"));
        teamBone.add(CreditNote.of(BigDecimal.valueOf(8), expirationOn));

        em.persist(teamBone);

        em.getTransaction().commit();
    }

    @Test
    public void t02_shouldCreateABordWithFinancialDocuments() {
        //final ZoneOffset offset = OffsetDateTime.now().getOffset();
        //final OffsetDateTime expirationOn = OffsetDateTime.of(LocalDate.of(2020, Month.JANUARY, 01), LocalTime.MIN, offset);

        em.getTransaction().begin();

        final Board ptechAcademy = Board.of("PAcademy", "Ptech-Academy");
        ptechAcademy.add(Invoice.of(BigDecimal.valueOf(120), "idea"));

        em.persist(ptechAcademy);

        em.getTransaction().commit();
    }

    @Test
    public void t03_should_get_all_the_financialDocuments_the_tbone_board() {
        List<FinancialDocument> tboneFinancialDocuments = em
                .createQuery("select fd from FinancialDocument fd join fetch fd.board b where b.code = lower(:code) order by fd.class", FinancialDocument.class)
                .setParameter("code", "t-bone")
                .getResultList();

        assertEquals(3, tboneFinancialDocuments.size());

        final List<FinancialDocument> invoices = tboneFinancialDocuments.stream().filter(Invoice.class::isInstance).collect(Collectors.toList());
        final List<FinancialDocument> creditNotes = tboneFinancialDocuments.stream().filter(CreditNote.class::isInstance).collect(Collectors.toList());

        assertEquals(2, invoices.size());
        assertEquals(1, creditNotes.size());
    }

    @Test
    public void t04_should_find_only_the_invoices() {
        List<FinancialDocument> tboneInvoices = em
                .createQuery("select fd from Invoice fd join fetch fd.board b where b.code = lower(:code) order by fd.class", FinancialDocument.class)
                .setParameter("code", "t-bone")
                .getResultList();

        assertEquals(2, tboneInvoices.size());
    }

    @Test
    public void t05_should_find_board_only_the_invoices() {
        Board tbone = em
                .createQuery("select b from Board b left join fetch b.financialDocuments fd " +
                        "where b.code = lower(:code) and type (fd) = Invoice", Board.class)
                .setParameter("code", "t-bone")
                .getSingleResult();

        //Assert.assertThat(tboneInvoices, Matchers.hasSize(3));

        assertNotNull(tbone);
        assertEquals(2, tbone.getFinancialDocuments().size());
    }

    @Test
    public void t06_should_not_add_invoice_without_content() {
        final RollbackException rollbackException = assertThrows(RollbackException.class, () -> {

            em.getTransaction().begin();

            final Session unwrap = em.unwrap(Session.class);
            //final Board tbone = unwrap.byNaturalId(Board.class).using("code", "t-bone").getReference();
            //Board b = unwrap.byNaturalId(Board.class).using(Board_.code, "t-bone").load();
            //Board b = unwrap.byNaturalId(Board.class).using(Board_.code, "t-bone").with(LockOptions.UPGRADE).load();
            //Board b = unwrap.bySimpleNaturalId(Board.class).with(LockOptions.UPGRADE).load("t-bone");
            final Board tbone = unwrap.bySimpleNaturalId(Board.class).load("t-bone");

            tbone.add(Invoice.of(BigDecimal.valueOf(92.9), null));

            em.getTransaction().commit();

        });

        Assertions.assertNotNull(rollbackException);
    }

    @Test
    public void t07_should_delete_all_using_polymorphic_delete_jpql() {
        em.getTransaction().begin();

        em.createQuery("delete from FinancialDocument fd").executeUpdate();

        em.createQuery("delete from Board ").executeUpdate();

        em.getTransaction().commit();
    }
}
