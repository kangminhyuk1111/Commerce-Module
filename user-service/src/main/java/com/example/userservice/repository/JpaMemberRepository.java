package com.example.userservice.repository;

import com.example.userservice.domain.Member;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaMemberRepository extends MemberRepository, JpaRepository<Member, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT m FROM Member m WHERE m.email = :email")
  @Override
  Optional<Member> findByEmailWithLock(@Param("email") String email);
}
