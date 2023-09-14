package com.isfive.usearth.domain.maker.service;

import com.isfive.usearth.domain.maker.dto.MakerResponse;
import com.isfive.usearth.domain.maker.entity.Maker;
import com.isfive.usearth.domain.maker.repository.MakerRepository;
import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.domain.member.repository.MemberRepository;
import com.isfive.usearth.web.maker.dto.MakerUpdate;
import com.isfive.usearth.web.maker.dto.register.CorporateRegister;
import com.isfive.usearth.web.maker.dto.register.IndividualRegister;
import com.isfive.usearth.web.maker.dto.register.PersonalRegister;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MakerService {
	private final MakerRepository makerRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public void createIndividualBy(String username, IndividualRegister individualRegister) {
		Member member = memberRepository.findByUsernameOrThrow(username);
		makerRepository.save(individualRegister.toEntity(member));
	}

	@Transactional
	public void createCorporateBusinessBy(String username, CorporateRegister corporateRegister) {
		Member member = memberRepository.findByUsernameOrThrow(username);
		makerRepository.save(corporateRegister.toEntity(member));
	}

	@Transactional
	public void createPersonalBusinessBy(String username, PersonalRegister personalRegister) {
		Member member = memberRepository.findByUsernameOrThrow(username);
		makerRepository.save(personalRegister.toEntity(member));
	}

	public MakerResponse readMakerById(Long id) {
		Maker maker = makerRepository.findByIdOrThrow(id);
		if (maker.getDeletedAt() != null) {
			throw new RuntimeException();
		}
		return MakerResponse.fromEntity(maker);
	}

	@Transactional
	public MakerResponse updateMakerById(String username, Long id, MakerUpdate makerUpdate) {
		Maker maker = makerRepository.findByIdOrThrow(id);
		maker.verify(username);
		maker.update(makerUpdate);
		return MakerResponse.fromEntity(makerRepository.save(maker));
	}

	@Transactional
	public void removeMakerById(String username, Long id) {
		Maker maker = makerRepository.findByIdOrThrow(id);
		maker.verify(username);
		maker.delete();
	}
}
