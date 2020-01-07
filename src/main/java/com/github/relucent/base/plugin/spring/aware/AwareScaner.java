package com.github.relucent.base.plugin.spring.aware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

/**
 * 类扫描器，用于扫描项目的类文件
 */
public class AwareScaner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AwareScaner.class);


    /**
     * 查找匹配的类列表
     * @param pattern 表达式
     * @return 类列表
     * @throws IOException 出现IO异常
     */
    // classpath*:com/github/relucent/**/*.class
    public Class<?>[] findClasses(String pattern) throws IOException {

        List<Class<?>> result = new ArrayList<>();

        // 资源解析器
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        // 元数据读取工厂类
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(resourceResolver);

        // 读取路径下的CLASS资源
        Resource[] resources = resourceResolver.getResources(pattern);

        // 遍历CLASS资源
        for (Resource resource : resources) {

            // 不可读取的排除掉
            if (!resource.isReadable()) {
                continue;
            }

            // 获得类元数据读取器
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);

            // 获得类元数据
            ClassMetadata classMetadata = metadataReader.getClassMetadata();

            // 获得类名称
            String className = classMetadata.getClassName();

            // 获得类
            Class<?> type = null;
            try {
                type = Class.forName(className);
            } catch (ClassNotFoundException e) {
                LOGGER.warn("!", e);
            }

            // 类未定义
            if (type == null) {
                continue;
            }

            // 将类添加到结果
            result.add(type);
        }

        return result.toArray(new Class[result.size()]);
    }

}
