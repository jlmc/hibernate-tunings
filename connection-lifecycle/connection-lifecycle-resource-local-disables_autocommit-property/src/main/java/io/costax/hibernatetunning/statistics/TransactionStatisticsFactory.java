package io.costax.hibernatetunning.statistics;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.stat.spi.StatisticsFactory;
import org.hibernate.stat.spi.StatisticsImplementor;

public class TransactionStatisticsFactory implements StatisticsFactory {

    @Override
    public StatisticsImplementor buildStatistics(final SessionFactoryImplementor sessionFactory) {
        return new TransactionStatistics(sessionFactory);
    }
}
