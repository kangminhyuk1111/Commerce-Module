package com.example.userservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MemberTest {

  @Nested
  class 멤버_생성 {

    @Test
    void 기본_생성자() {
      // Arrange & Act
      Member member = new Member();

      // Assert
      assertThat(member.getId()).isNull();
      assertThat(member.getName()).isNull();
      assertThat(member.getEmail()).isNull();
      assertThat(member.getCreatedAt()).isNull();
      assertThat(member.getUpdatedAt()).isNull();
    }

    @Test
    void 이름과_이메일로_생성() {
      // Arrange
      String name = "홍길동";
      String email = "hong@example.com";
      String password = "password@";

      // Act
      Member member = new Member(name, email, password);

      // Assert
      assertThat(member)
          .extracting(
              Member::getName,
              Member::getEmail
          )
          .containsExactly(name, email);

      assertThat(member.getId()).isNull();
      assertThat(member.getCreatedAt()).isNull();
      assertThat(member.getUpdatedAt()).isNull();
    }

    @Test
    void 모든_필드로_생성() {
      // Arrange
      Long id = 1L;
      String name = "김철수";
      String email = "kim@example.com";
      LocalDateTime now = LocalDateTime.now();

      // Act
      Member member = new Member(id, name, email, now, now);

      // Assert
      assertThat(member)
          .extracting(
              Member::getId,
              Member::getName,
              Member::getEmail,
              Member::getCreatedAt,
              Member::getUpdatedAt
          )
          .containsExactly(id, name, email, now, now);
    }
  }

  @Nested
  class Getter_메서드 {

    @Test
    void 모든_필드_조회() {
      // Arrange
      Long expectedId = 100L;
      String expectedName = "이영희";
      String expectedEmail = "lee@example.com";
      LocalDateTime expectedCreatedAt = LocalDateTime.of(2024, 1, 1, 10, 0);
      LocalDateTime expectedUpdatedAt = LocalDateTime.of(2024, 1, 2, 15, 30);
      Member member = new Member(expectedId, expectedName, expectedEmail,
          expectedCreatedAt, expectedUpdatedAt);

      // Act
      Long actualId = member.getId();
      String actualName = member.getName();
      String actualEmail = member.getEmail();
      LocalDateTime actualCreatedAt = member.getCreatedAt();
      LocalDateTime actualUpdatedAt = member.getUpdatedAt();

      // Assert
      assertThat(actualId).isEqualTo(expectedId);
      assertThat(actualName).isEqualTo(expectedName);
      assertThat(actualEmail).isEqualTo(expectedEmail);
      assertThat(actualCreatedAt).isEqualTo(expectedCreatedAt);
      assertThat(actualUpdatedAt).isEqualTo(expectedUpdatedAt);
    }
  }

  @Nested
  class 객체_동등성 {

    @Test
    void 같은_값으로_생성된_멤버는_필드값이_동일() {
      // Arrange
      String name = "박민수";
      String email = "park@example.com";
      LocalDateTime time = LocalDateTime.now();
      Member member1 = new Member(1L, name, email, time, time);
      Member member2 = new Member(1L, name, email, time, time);

      // Act
      Long id1 = member1.getId();
      String name1 = member1.getName();
      String email1 = member1.getEmail();
      LocalDateTime createdAt1 = member1.getCreatedAt();
      LocalDateTime updatedAt1 = member1.getUpdatedAt();

      Long id2 = member2.getId();
      String name2 = member2.getName();
      String email2 = member2.getEmail();
      LocalDateTime createdAt2 = member2.getCreatedAt();
      LocalDateTime updatedAt2 = member2.getUpdatedAt();

      // Assert
      assertThat(id1).isEqualTo(id2);
      assertThat(name1).isEqualTo(name2);
      assertThat(email1).isEqualTo(email2);
      assertThat(createdAt1).isEqualTo(createdAt2);
      assertThat(updatedAt1).isEqualTo(updatedAt2);
    }

    @Test
    void 다른_ID를_가진_멤버는_다름() {
      // Arrange
      String name = "최영수";
      String email = "choi@example.com";
      LocalDateTime time = LocalDateTime.now();
      Member member1 = new Member(1L, name, email, time, time);
      Member member2 = new Member(2L, name, email, time, time);

      // Act
      Long id1 = member1.getId();
      Long id2 = member2.getId();

      // Assert
      assertThat(id1).isNotEqualTo(id2);
    }
  }

  @Nested
  class 필드_검증 {

    @Test
    void null_값_허용_확인() {
      // Arrange
      // (준비할 데이터 없음)

      // Act
      Member member = new Member();

      // Assert
      assertThat(member)
          .extracting(
              Member::getId,
              Member::getName,
              Member::getEmail,
              Member::getCreatedAt,
              Member::getUpdatedAt
          )
          .containsOnlyNulls();
    }

    @Test
    void 부분_생성자_나머지_필드_null() {
      // Arrange
      String name = "테스트";
      String email = "test@example.com";
      String password = "password@";

      // Act
      Member member = new Member(name, email, password);

      // Assert
      assertThat(member)
          .extracting(
              Member::getName,
              Member::getEmail,
              Member::getPassword
          )
          .containsExactly(name, email, password);

      assertThat(member)
          .extracting(
              Member::getId,
              Member::getCreatedAt,
              Member::getUpdatedAt
          )
          .containsOnlyNulls();
    }
  }
}