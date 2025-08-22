package com.example.userservice.dto.request;

import com.example.userservice.domain.Member;
import com.example.userservice.global.ErrorCode;
import com.example.userservice.global.MemberException;
import java.util.regex.Pattern;

public record CreateMemberRequest(String name, String email, String password) {

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

  public CreateMemberRequest {
    if (name == null || name.trim().isEmpty()) {
      throw new MemberException(ErrorCode.NAME_REQUIRED);
    }

    if (email == null || email.trim().isEmpty()) {
      throw new MemberException(ErrorCode.EMAIL_REQUIRED);
    }

    name = name.trim();
    email = email.trim().toLowerCase();

    validateName(name);
    validateEmail(email);
  }

  private void validateName(String name) {
    if (name.length() < 2) {
      throw new MemberException(ErrorCode.MEMBER_NAME_TOO_SHORT);
    }
  }

  private void validateEmail(String email) {
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new MemberException(ErrorCode.EMAIL_VALIDATION_FAILED);
    }
  }

  public Member toDomain() {
    return new Member(this.name, this.email, this.password);
  }
}