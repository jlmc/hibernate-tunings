package io.costax.hibernatetunning.inheritance;

import io.costax.hibernatetunings.entities.financial.Board;
import io.costax.hibernatetunings.entities.financial.CreditNote;
import io.costax.hibernatetunings.entities.financial.FinancialDocument;
import io.costax.hibernatetunings.entities.financial.Invoice;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.Session;
import org.junit.jupiter.api.*;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.RollbackException;
import java.math.BigDecimal;
import java.time.*;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@JpaTest(persistenceUnit = "it")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class SingleTableTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    @Order(0)
    public void get_all_financial_documents_using_a_polymorphic_jpql() {
        final EntityManager em = provider.em();

        final List<FinancialDocument> financialDocuments =
                em.createQuery("select fd from FinancialDocument fd", FinancialDocument.class)
                        .getResultList();

        em.close();
    }

    @Test
    @Order(1)
    public void create_a_board_with_financial_documents_1() {
        final ZoneOffset offset = OffsetDateTime.now().getOffset();
        final OffsetDateTime expirationOn = OffsetDateTime.of(LocalDate.of(2020, Month.JANUARY, 01), LocalTime.MIN, offset);

        final EntityManager em = provider.em();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        final Board teamBone = Board.of("t-Bone", "Team Bone");
        teamBone.add(Invoice.of(BigDecimal.valueOf(10), "Abc"));
        teamBone.add(Invoice.of(BigDecimal.valueOf(13), "Cder"));
        teamBone.add(CreditNote.of(BigDecimal.valueOf(8), expirationOn));

        em.persist(teamBone);

        tx.commit();
        em.close();
    }

    @Test
    @Order(2)
    public void create_a_board_with_financial_documents_2() {
        final EntityManager em = provider.em();

        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        final Board ptechAcademy = Board.of("PAcademy", "Ptech-Academy");
        ptechAcademy.add(Invoice.of(BigDecimal.valueOf(120), "idea"));

        em.persist(ptechAcademy);

        tx.commit();
        em.close();
    }

    @Test
    @Order(3)
    public void get_all_the_financialDocuments_the_tbone_board() {
        final EntityManager em = provider.em();

        List<FinancialDocument> tboneFinancialDocuments = em
                .createQuery("select fd from FinancialDocument fd join fetch fd.board b where b.code = lower(:code) order by fd.class", FinancialDocument.class)
                .setParameter("code", "t-bone")
                .getResultList();

        assertEquals(3, tboneFinancialDocuments.size());

        final List<FinancialDocument> invoices = tboneFinancialDocuments.stream().filter(f -> f instanceof Invoice).collect(Collectors.toList());
        final List<FinancialDocument> creditNotes = tboneFinancialDocuments.stream().filter(f -> f instanceof CreditNote).collect(Collectors.toList());

        em.close();

        assertEquals(2, invoices.size());
        assertEquals(1, creditNotes.size());
    }

    @Test
    @Order(4)
    public void find_only_the_invoices() {

        List<FinancialDocument> tboneInvoices =
                provider.doItWithReturn(em ->
                        em.createQuery("select fd from Invoice fd join fetch fd.board b where b.code = lower(:code) order by fd.class", FinancialDocument.class)
                                .setParameter("code", "t-bone")
                                .getResultList()
                );

        assertEquals(2, tboneInvoices.size());
    }

    @Test
    @Order(5)
    public void should_find_board_only_the_invoices() {
        Board tbone = provider.doItWithReturn(em -> {

            return em.createQuery(
                    """
                            select b from Board b left join fetch b.financialDocuments fd 
                            where b.code = lower(:code) and type (fd) = Invoice
                            """, Board.class)
                    .setParameter("code", "t-bone")
                    .getSingleResult();
        });

        assertNotNull(tbone);
        assertEquals(2, tbone.getFinancialDocuments().size());
    }

    //@Test(expected = org.hibernate.exception.ConstraintViolationException.class)
    @Test//(expected = jakarta.persistence.RollbackException.class)
    @Order(6)
    public void should_not_add_invoice_without_content() {
        final EntityManager em = provider.em();
        try {

            em.getTransaction().begin();

            final Session unwrap = em.unwrap(Session.class);
            //final Board tbone = unwrap.byNaturalId(Board.class).using("code", "t-bone").getReference();
            //Board b = unwrap.byNaturalId(Board.class).using(Board_.code, "t-bone").load();
            //Board b = unwrap.byNaturalId(Board.class).using(Board_.code, "t-bone").with(LockOptions.UPGRADE).load();
            //Board b = unwrap.bySimpleNaturalId(Board.class).with(LockOptions.UPGRADE).load("t-bone");
            final Board tbone = unwrap.bySimpleNaturalId(Board.class).load("t-bone");

            final Invoice invoiceWithoutContent = Invoice.of(BigDecimal.valueOf(92.9), null);
            tbone.add(invoiceWithoutContent);

            em.getTransaction().commit();

            //Assertions.fail("should not happen");

        } catch (RollbackException e) {
            final Throwable cause = e.getCause();

            if (!org.hibernate.exception.ConstraintViolationException.class.isAssignableFrom(cause.getCause().getClass())) {
                Assertions.fail("Wrong cause, different from: org.hibernate.exception.ConstraintViolationException");
            }

            //throw e;

        } finally {
            em.close();
        }
    }

    @Test
    @Order(7)
    public void should_extract_discriminator_value_annotation_value() {
        // WARN - HHH90000017: Found use of deprecated entity-type selector syntax in HQL/JPQL query ['t.class']; use TYPE operator instead : type(t)
        final EntityManager em = provider.em();

        List<Object[]> resultList = em.createQuery("SELECT t.class, t FROM FinancialDocument t", Object[].class).getResultList();

        Assertions.assertEquals(4, resultList.size());
        Assertions.assertEquals(2, resultList.get(0).length);

        final Object objects0 = resultList.get(0)[0];
        final Object objects1 = resultList.get(0)[1];

        assertNotNull(objects0);
        assertNotNull(objects1);
        assertTrue(String.class.isAssignableFrom(objects0.getClass()));
        assertFalse(FinancialDocument.class.isAssignableFrom(objects0.getClass()));

        assertEquals("Invoice", objects0);
        assertEquals(Invoice.class, objects1.getClass());

        em.close();
    }

    @Test
    @Order(8)
    public void should_extract_DiscriminatorValue_annotation_value_without_HHH90000017_warning() {
        final EntityManager em = provider.em();

        List<Object[]> resultList = em.createQuery("SELECT type(t), t FROM FinancialDocument t", Object[].class).getResultList();

        Assertions.assertEquals(4, resultList.size());
        Assertions.assertEquals(2, resultList.get(0).length);

        final Object objects0 = resultList.get(0)[0];
        final Object objects1 = resultList.get(0)[1];

        assertNotNull(objects0);
        assertNotNull(objects1);
        assertNotNull(Class.class.isAssignableFrom(objects0.getClass()));
        assertNotNull(FinancialDocument.class.isAssignableFrom(objects0.getClass()));

        assertEquals(Invoice.class, objects0);
        assertEquals(Invoice.class, objects1.getClass());

        final Map<String, ? extends List<? extends FinancialDocument>> collect = resultList.stream()
                .map(obj -> {
                            return new AbstractMap.SimpleEntry<>(
                                    ((Class<? extends FinancialDocument>) obj[0]).getAnnotation(DiscriminatorValue.class).value(),
                                    ((Class<? extends FinancialDocument>) obj[0]).cast(obj[1])
                            );
                        }
                ).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        assertNotNull(collect);

        em.close();
    }

    @Test
    public void t99_should_delete_all_using_polymorphic_delete_jpql() {
        provider.doInTx(em -> {

            em.createQuery("delete from FinancialDocument fd").executeUpdate();
            em.createQuery("delete from Board ").executeUpdate();

        });
    }
}
