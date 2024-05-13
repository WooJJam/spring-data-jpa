package study.datajpa.repository.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    Member findUsernameByUsername(String username);

    List<Member> findFirst3ByAge(int age);

}
