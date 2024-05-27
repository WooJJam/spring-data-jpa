package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustomBy() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
