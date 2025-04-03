package fr.kuremento.dnd.config;

import org.springframework.batch.core.DefaultJobKeyGenerator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.database.support.DefaultDataFieldMaxValueIncrementerFactory;
import org.springframework.batch.support.DatabaseType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * Configuration propre au fonctionnement de Spring Batch
 */
@Configuration
public class BatchConfiguration {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder().addScript("classpath:org/springframework/batch/core/schema-drop-h2.sql")
                                            .addScript("classpath:org/springframework/batch/core/schema-h2.sql")
                                            .addScript("classpath:sql/mapping.sql")
                                            .addScript("classpath:sql/translation.sql")
                                            .setType(EmbeddedDatabaseType.H2)
                                            .build();
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("dndJobRepository")
    public JobRepository jobRepository(DataSource dataSource, DataSourceTransactionManager transactionManager) throws Exception {
        var factory = new JobRepositoryFactoryBean();
        factory.setDatabaseType(DatabaseType.H2.getProductName());
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setIncrementerFactory(new DefaultDataFieldMaxValueIncrementerFactory(dataSource));
        factory.setJdbcOperations(new JdbcTemplate(dataSource));
        factory.setConversionService(new DefaultConversionService());
        factory.setSerializer(new Jackson2ExecutionContextStringSerializer());
        factory.setJobKeyGenerator(new DefaultJobKeyGenerator());
        return factory.getObject();
    }
}
