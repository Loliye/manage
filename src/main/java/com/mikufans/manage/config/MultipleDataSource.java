//package com.mikufans.manage.config;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.MutablePropertyValues;
//import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.beans.factory.config.BeanDefinitionHolder;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
//import org.springframework.beans.factory.support.BeanNameGenerator;
//import org.springframework.boot.bind.RelaxedPropertyResolver;
//import org.springframework.context.EnvironmentAware;
//import org.springframework.context.annotation.*;
//import org.springframework.core.env.Environment;
//import org.springframework.core.env.PropertyResolver;
//import org.springframework.core.env.PropertySourcesPropertyResolver;
//import sun.util.resources.cldr.ss.CalendarData_ss_SZ;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 多数据源配置
// * BeanDefinitionRegistryPostProcessor 注入bean
// * EnvironmentAware 重写方法 setEnvironment
// * 可以在工程启动时，获取到系统环境变量和application配置文件中的变量
// * 方法的执行顺序是：
// * setEnvironment()-->postProcessBeanDefinitionRegistry() --> postProcessBeanFactory()
// */
//
////@Configuration
//public class MultipleDataSource implements BeanDefinitionRegistryPostProcessor, EnvironmentAware
//{
//
//    //如配置文件中未指定数据源类型，使用该默认值
//    private static final Object DATASOURCE_TYPE_DEFAULT = "com.alibaba.druid.pool.DruidDataSource";
//    //作用域对象
//    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();
//    //bean名称生成器
//    private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
//    // 存放DataSource配置的集合;
//    private Map<String, Map<String, Object>> dataSourceMap = new HashMap<String, Map<String, Object>>();
//
//    @Override
//    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException
//    {
//        System.out.println("postProcessBeanDefinitionRegistry...");
//        try
//        {
//            if (!dataSourceMap.isEmpty())
//            {
//                //不为空的时候，进行注册bean.
//                for (Map.Entry<String, Map<String, Object>> entry : dataSourceMap.entrySet())
//                {
//                    Object type = entry.getValue().get("type");//获取数据源类型
//                    if (type == null)
//                    {
//                        type = DATASOURCE_TYPE_DEFAULT;
//                    }
//                    registerBean(beanDefinitionRegistry, entry.getKey(), (Class<? extends DataSource>) Class.forName(type.toString()));
//                }
//            }
//        } catch (ClassNotFoundException e)
//        {
//            //异常捕捉.
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException
//    {
//        System.out.println("postProcessBeanFactory...");
//        //设置著数据源
//        configurableListableBeanFactory.getBeanDefinition("datasource").setPrimary(true);
//        if (!dataSourceMap.isEmpty())
//        {
//            BeanDefinition beanDefinition = null;
//            Map<String, Object> map = null;
//            MutablePropertyValues mutablePropertyValues = null;
//            for (Map.Entry<String, Map<String, Object>> entry : dataSourceMap.entrySet())
//            {
//                beanDefinition = configurableListableBeanFactory.getBeanDefinition(entry.getKey());
//                mutablePropertyValues = beanDefinition.getPropertyValues();
//                map = entry.getValue();
//
//                mutablePropertyValues.addPropertyValue("driverClassName", map.get("driverClassName"));
//                mutablePropertyValues.addPropertyValue("url", map.get("url"));
//                mutablePropertyValues.addPropertyValue("username", map.get("username"));
//                mutablePropertyValues.addPropertyValue("password", map.get("password"));
//
//            }
//        }
//    }
//
//    @Override
//    public void setEnvironment(Environment environment)
//    {
//        System.out.println("MultipleDataSourceBeanDefinitionRegistryPostProcessor.setEnvironment()");
//        /*
//         * 获取application.properties配置的多数据源配置，添加到map中，之后在postProcessBeanDefinitionRegistry进行注册。
//         */
//        //获取到前缀是"slave.datasource." 的属性列表值.
//        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, "slave.datasource.");
//        //获取到所有数据源的名称.
//        String dsPrefixs = propertyResolver.getProperty("names");
//        String[] dsPrefixsArr = dsPrefixs.split(",");
//        for (String dsPrefix : dsPrefixsArr)
//        {
//            /*
//             * 获取到子属性，对应一个map;
//             * 也就是这个map的key就是
//             * type、driver-class-name等;
//             */
//            Map<String, Object> dsMap = propertyResolver.getSubProperties(dsPrefix + ".");
//            //存放到一个map集合中，之后在注入进行使用.
//            dataSourceMap.put(dsPrefix, dsMap);
//        }
//    }
//
//    /**
//     * 注册bean到spring中
//     *
//     * @param registry
//     * @param name
//     * @param beanClass
//     */
//    public void registerBean(BeanDefinitionRegistry registry, String name, Class<?> beanClass)
//    {
//        AnnotatedGenericBeanDefinition annotatedGenericBeanDefinition = new AnnotatedGenericBeanDefinition(beanClass);
//        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(annotatedGenericBeanDefinition);
//        annotatedGenericBeanDefinition.setScope(scopeMetadata.getScopeName());
//
//        String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(annotatedGenericBeanDefinition, registry));
//        AnnotationConfigUtils.processCommonDefinitionAnnotations(annotatedGenericBeanDefinition);
//        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(annotatedGenericBeanDefinition, beanName);
//        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
//    }
//}
