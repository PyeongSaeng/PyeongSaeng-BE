package com.umc.pyeongsaeng.global.resolvation.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.resolvation.annotation.PageNumber;

@Component
public class PageNumberArgumentResolver implements HandlerMethodArgumentResolver {


	@Override
	public boolean supportsParameter(MethodParameter parameter) {

		return parameter.hasParameterAnnotation(PageNumber.class)
			&& (parameter.getParameterType().equals(Integer.class)) || parameter.getParameterType().equals(int.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		String pageNumber = webRequest.getParameter("page");

		if(pageNumber == null) {
			return 1;
		}

		int page;

		try {
			page = Integer.parseInt(pageNumber);
		} catch (NumberFormatException e) {
			throw new GeneralException(ErrorStatus.PAGE_NUMBER_NOT_NUMBER);
		}

		if(page <= 0) {
			throw new GeneralException(ErrorStatus.PAGE_NUMBER_NEGATIVE);
		} else {
			page = page - 1;
		}

		return page;
	}
}

