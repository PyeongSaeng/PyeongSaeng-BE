package com.umc.pyeongsaeng.domain.senior.entity;

import java.util.ArrayList;
import java.util.List;

import com.umc.pyeongsaeng.global.common.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SeniorQuestion extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String question;

	@OneToMany(mappedBy = "seniorQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SeniorQuestionOption> options = new ArrayList<>();


}
