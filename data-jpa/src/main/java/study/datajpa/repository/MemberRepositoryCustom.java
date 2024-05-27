package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;

@Repository
public interface MemberRepositoryCustom {
    List<Member> findMemberCustomBy();
}
