package com.umc.pyeongsaeng.domain.job.search.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

import java.io.IOException;
import java.util.*;

import com.umc.pyeongsaeng.domain.job.search.elkoperation.ElasticOperationService;
import com.umc.pyeongsaeng.domain.job.search.annotation.GenericElkIndex;

@Component
@Slf4j
@RequiredArgsConstructor
public class ElkIndexCreatorListener implements ApplicationListener<ApplicationReadyEvent> {

	private final ElasticOperationService elasticOperationService;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		log.info("[ES] Index Scan 시작");
		scanElkDocumentAnnotation();
		log.info("[ES] Index Scan 완료");
	}

	private void scanElkDocumentAnnotation() {
		ClassPathScanningCandidateComponentProvider provider =
			new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(GenericElkIndex.class));

		String basePackage = "com.umc.pyeongsaeng.domain.job.search";
		Set<BeanDefinition> beanDefs = provider.findCandidateComponents(basePackage);

		List<String> annotatedBeans = new ArrayList<>();

		for (BeanDefinition bd : beanDefs) {
			if (bd instanceof AnnotatedBeanDefinition annotatedBd) {
				Map<String, Object> attrMap =
					annotatedBd.getMetadata().getAnnotationAttributes(GenericElkIndex.class.getCanonicalName());

				if (attrMap != null) {
					String indexName = attrMap.get("indexName").toString();
					annotatedBeans.add(indexName);
				}
			}
		}

		log.info("[ES] 찾은 Index 수 - {}", annotatedBeans.size());

		for (String indexName : annotatedBeans) {
			try {
				if (!elasticOperationService.checkIfExistIndex(indexName)) {
					String created = elasticOperationService.createIndex(indexName);
					log.info("[ES] Index 생성 완료 - {}", created);
				} else {
					log.info("[ES] Index 이미 존재 - {}", indexName);
				}
			} catch (Exception e) {
				log.error("[ES] Index 생성 실패 - {}", indexName, e);
			}
		}

	}


}
