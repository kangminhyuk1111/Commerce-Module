package com.example.userservice.dto.response;

import com.example.userservice.domain.Member;
import java.time.LocalDateTime;

public record MemberResponse(Long id, String name, String email, LocalDateTime createdAt, LocalDateTime updatedAt) {

  public static MemberResponse of(final Member member) {
    return new MemberResponse(
        member.getId(),
        member.getName(),
        member.getEmail(),
        member.getCreatedAt(),
        member.getUpdatedAt()
    );
  }
}
