package io.costax.hpjp.hibernate.schema.flyway;

import org.hibernate.Session;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PostgreSQLFlywayConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DropPostgreSQLPublicSchemaTest {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Resource
    private String databaseType;

    private boolean drop = true;

    @Test
    public void test() {
        if (drop) {
            try {
                transactionTemplate.execute((TransactionCallback<Void>) transactionStatus -> {
                    Session session = entityManager.unwrap(Session.class);
                    session.doWork(connection -> {
                        ScriptUtils.executeSqlScript(connection,
                                new EncodedResource(
                                        new ClassPathResource(
                                                String.format("flyway/db/%1$s/drop/drop.sql", databaseType)
                                        )
                                ),
                                true, true,
                                ScriptUtils.DEFAULT_COMMENT_PREFIX,
                                ScriptUtils.DEFAULT_BLOCK_COMMENT_START_DELIMITER,
                                ScriptUtils.DEFAULT_BLOCK_COMMENT_END_DELIMITER,
                                ScriptUtils.DEFAULT_COMMENT_PREFIX);
                    });
                    return null;
                });
            } catch (TransactionException e) {
                LOGGER.error("Failure", e);
            }
        }
    }

    /*
    @Test
    public void name1() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));

        System.out.println(calendar.getTime());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime localDateTime = now.withDayOfMonth(now.toLocalDate().lengthOfMonth());

        System.out.println(localDateTime);
    }

    @Test
    public void name() {

        System.out.println(

        LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
    */
}
