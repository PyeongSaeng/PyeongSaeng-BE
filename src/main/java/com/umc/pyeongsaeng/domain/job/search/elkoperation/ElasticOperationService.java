package com.umc.pyeongsaeng.domain.job.search.elkoperation;

import com.umc.pyeongsaeng.domain.job.search.document.BaseElkDocument;

public interface ElasticOperationService {

	boolean checkIfExistIndex(String indexName);
	String createIndex(String indexName);
	<T extends BaseElkDocument> String insertDocumentGeneric(T document);

}
