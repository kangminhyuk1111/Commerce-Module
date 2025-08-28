package com.example.userservice.repository;

import com.example.userservice.domain.Member;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class FakeMemberRepository implements MemberRepository {

  private final Map<Long, Member> store = new HashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(0);

  @Override
  public Optional<Member> findByEmail(final String email) {
    return store.values().stream()
        .filter(member -> member.getEmail().equals(email))
        .findFirst();
  }

  @Override
  public Optional<Member> findByEmailWithLock(final String email) {
    return store.values().stream()
        .filter(member -> member.getEmail().equals(email))
        .findFirst();
  }

  @Override
  public Optional<Member> findById(final Long id) {
    return Optional.ofNullable(store.get(id));
  }

  @Override
  public List<Member> findAll() {
    return new ArrayList<>(store.values());
  }

  @Override
  public Member save(final Member member) {
    Member memberToSave = member;
    if (member.getId() == null) {
      // ID를 새로 생성한 Member 인스턴스로 부여
      memberToSave = new Member(
          idGenerator.incrementAndGet(),
          member.getName(),
          member.getEmail(),
          member.getCreatedAt(),
          member.getUpdatedAt()
      );
    }
    store.put(memberToSave.getId(), memberToSave);
    return memberToSave;
  }
}
