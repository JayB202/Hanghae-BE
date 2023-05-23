package com.hanghae7.alcoholcommunity.domain.party.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanghae7.alcoholcommunity.domain.member.entity.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class PartyParticipate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore // 순환참조 막아줌
	@JoinColumn(nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonIgnore // 순환참조 막아줌
	@JoinColumn(nullable = false)
	private Party party;

	@Builder
	public PartyParticipate(Party party, Member member) {
		this.party=party;
		this.member=member;
	}


}