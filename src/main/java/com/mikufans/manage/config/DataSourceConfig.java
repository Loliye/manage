package com.mikufans.manage.config;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@MapperScan(basePackages = "com.mikufans.manage.dao",sqlSessionTemplateRef = "testSqlSessionTemplate")
public class DataSourceConfig
{
    /**
     * 创建datasource对象
     * @return
     */
    @Bean(name = "testDataSource")
    @ConfigurationProperties(prefix = "slave.datasource.test")
    @Primary
    public DataSource testDataSource()
    {
        return DataSourceBuilder.create().build();
    }

    /**
     * 创建sql工程
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "testSqlSessionFactory")
    @Primary
    public SqlSessionFactory testSqlSessionFactory(@Qualifier("testDataSource") DataSource dataSource) throws Exception
    {
        SqlSessionFactoryBean bean=new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //对应的 mybatis.type-aliases-package配置
        bean.setTypeAliasesPackage("com.mikufans.manage.pojo");
        //对应mybatis.mapper-locations配置
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        //驼峰映射
        bean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        return bean.getObject();

    }

    /**
     * 配置事务
     * @param dataSource
     * @return
     */
    @Bean(name = "testTransactionManager")
    @Primary
    public DataSourceTransactionManager testTransactionManage(@Qualifier("testDataSource") DataSource dataSource)
    {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "testSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("testSqlSessionFactory") SqlSessionFactory sqlSessionFactory)
    {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
