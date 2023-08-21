package com.isfive.usearth.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isfive.usearth.domain.member.entity.Member;
import com.isfive.usearth.exception.EntityNotFoundException;
import com.isfive.usearth.exception.ErrorCode;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByEmail(String email);

	default Member findByEmailOrThrow(String email) {
		return findByEmail(email).orElseThrow(
			() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
	};
}
