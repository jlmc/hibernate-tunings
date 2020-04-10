package io.costax.hibernatetunning.inheritance;

import io.costa.hibernatetunings.entities.financial.Board;
import io.costa.hibernatetunings.entities.financial.CreditNote;
import io.costa.hibernatetunings.entities.financial.FinancialDocument;
import io.costa.hibernatetunings.entities.financial.Invoice;
import io.costax.rules.EntityManagerProvider;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.DiscriminatorValue;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.math.BigDecimal;
import java.time.*;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SingleTableTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void t00_shouldGetAllAllFinancialDocumentsUsingAPolymorphicJPQL() {
        provider.em().createQuery("select fd from FinancialDocument fd",
                FinancialDocument.class).getResultList();
    }

    @Test
    public void t01_shouldCreateABordWithFiancialDocuments() {
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
    public void t02_shouldCreateABordWithFinancialDocuments() {
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
    public void t03_should_get_all_the_financialDocuments_the_tbone_board() {
        List<FinancialDocument> tboneFinancialDocuments =
                provider.em()
                        .createQuery("select fd from FinancialDocument fd join fetch fd.board b where b.code = lower(:code) order by fd.class", FinancialDocument.class)
                        .setParameter("code", "t-bone")
                        .getResultList();

        assertThat(tboneFinancialDocuments, hasSize(3));

        final List<FinancialDocument> invoices = tboneFinancialDocuments.stream().filter(f -> f instanceof Invoice).collect(Collectors.toList());
        final List<FinancialDocument> creditNotes = tboneFinancialDocuments.stream().filter(f -> f instanceof CreditNote).collect(Collectors.toList());

        assertThat(invoices, hasSize(2));
        assertThat(creditNotes, hasSize(1));
    }

    @Test
    public void t04_should_find_only_the_invoices() {
        List<FinancialDocument> tboneInvoices =
                provider.em()
                        .createQuery("select fd from Invoice fd join fetch fd.board b where b.code = lower(:code) order by fd.class", FinancialDocument.class)
                        .setParameter("code", "t-bone")
                        .getResultList();

        assertThat(tboneInvoices, hasSize(2));
    }

    @Test
    public void t05_should_find_board_only_the_invoices() {
        Board tbone =
                provider.em()
                        .createQuery("select b from Board b left join fetch b.financialDocuments fd " +
                                "where b.code = lower(:code) and type (fd) = Invoice", Board.class)
                        .setParameter("code", "t-bone")
                        .getSingleResult();

        //Assert.assertThat(tboneInvoices, Matchers.hasSize(3));

        assertThat(tbone, notNullValue());
        assertThat(tbone.getFinancialDocuments(), hasSize(2));
    }

    //@Test(expected = org.hibernate.exception.ConstraintViolationException.class)
    @Test(expected = javax.persistence.RollbackException.class)
    public void t06_should_not_add_invoice_without_content() {
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
    public void t07_should_extract_DiscriminatorValue_annotation_value() {
        // WARN - HHH90000017: Found use of deprecated entity-type selector syntax in HQL/JPQL query ['t.class']; use TYPE operator instead : type(t)
        List<Object[]> resultList = provider.em().createQuery("SELECT t.class, t FROM FinancialDocument t", Object[].class).getResultList();

        Assert.assertThat(resultList.size(), is(4));
        Assert.assertThat(resultList.get(0).length, is(2));

        final Object objects0 = resultList.get(0)[0];
        final Object objects1 = resultList.get(0)[1];

        Assert.assertNotNull(objects0);
        Assert.assertNotNull(objects1);
        Assert.assertTrue(String.class.isAssignableFrom(objects0.getClass()));
        Assert.assertFalse(FinancialDocument.class.isAssignableFrom(objects0.getClass()));

        Assert.assertEquals("Invoice", objects0);
        Assert.assertEquals(Invoice.class, objects1.getClass());

        System.out.println(resultList);
    }

    @Test
    public void t08_should_extract_DiscriminatorValue_annotation_value_without_HHH90000017_warning() {
        List<Object[]> resultList = provider.em().createQuery("SELECT type(t), t FROM FinancialDocument t", Object[].class).getResultList();

        Assert.assertThat(resultList.size(), is(4));
        Assert.assertThat(resultList.get(0).length, is(2));

        final Object objects0 = resultList.get(0)[0];
        final Object objects1 = resultList.get(0)[1];

        Assert.assertNotNull(objects0);
        Assert.assertNotNull(objects1);
        Assert.assertNotNull(Class.class.isAssignableFrom(objects0.getClass()));
        Assert.assertNotNull(FinancialDocument.class.isAssignableFrom(objects0.getClass()));

        Assert.assertEquals(Invoice.class, objects0);
        Assert.assertEquals(Invoice.class, objects1.getClass());

        //Class<? extends FinancialDocument> clazz = (Class<? extends FinancialDocument>) objects0;
        //final DiscriminatorValue annotation = clazz.getAnnotation(DiscriminatorValue.class);
        //final String value = annotation.value();

        final Map<String, ? extends List<? extends FinancialDocument>> collect = resultList.stream()
                .map(obj -> {
                            return new AbstractMap.SimpleEntry<>(
                                    ((DiscriminatorValue) ((Class<? extends FinancialDocument>) obj[0]).getAnnotation(DiscriminatorValue.class)).value(),
                                    ((Class<? extends FinancialDocument>) obj[0]).cast(obj[1])
                            );
                        }
                ).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

        Assert.assertNotNull(collect);
    }

    @Test
    public void t99_should_delete_all_using_polymorphic_delete_jpql() {
        provider.beginTransaction();
        final EntityManager em = provider.em();

        em.createQuery("delete from FinancialDocument fd").executeUpdate();

        em.createQuery("delete from Board ").executeUpdate();

        provider.commitTransaction();
    }
}
