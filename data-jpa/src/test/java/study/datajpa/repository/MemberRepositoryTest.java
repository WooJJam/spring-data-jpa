package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;
import study.datajpa.repository.springdata.MemberRepository;
import study.datajpa.repository.springdata.TeamRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    
    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void 회원_등록하기() throws Exception {

        //given
        Member member = new Member("memberB");

        //when
        Member saveMember = memberRepository.save(member);
        Optional<Member> byId = memberRepository.findById(saveMember.getId());
        Member findMember = byId.get();

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    @Rollback(value = false)
    public void basicCRUD() throws Exception {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);


        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);


        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreatherThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void 유저_이름_검색() throws Exception {
        Member m1 = new Member("AAA", 10);
        memberRepository.save(m1);
        Member member = memberRepository.findUsernameByUsername(m1.getUsername());
        assertThat(member.getUsername()).isEqualTo("AAA");
    }

    @Test
    public void TOP3_검색() throws Exception {
        memberRepository.findFirst3ByAge(10);
    }

    @Test
    public void namedQueryTest() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        assertThat(memberRepository.findByUsername("AAA").get(0).getUsername()).isEqualTo("AAA");
    }
    
    @Test
    public void testQuery() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }
    
    @Test
    public void findUsernameList() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
    
    @Test
    public void findMemberDto() throws Exception {
        Member m1 = new Member("AAA", 10);
        Team team = new Team("teamA");
        m1.setTeam(team);
        teamRepository.save(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }
    
    @Test
    public void findByNames() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> usernameList = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : usernameList) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void 반환_값_테스트() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findListMember = memberRepository.findListByUsername("AAA");
        Member findMember = memberRepository.findMemberByUsername("AAA");
        memberRepository.findOptionalByUsername("AAA");
    }

    @Test
    public void paging() throws Exception {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // then
        List<Member> content = page.getContent();
        for (Member member : content) {
            System.out.println("member = " + member);
        }

        int totalPages = page.getTotalPages();
        System.out.println("totalPages = " + totalPages);
        long totalElements = page.getTotalElements();
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void slicing() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);

        // then
        List<Member> content = page.getContent();
        for (Member member : content) {
            System.out.println("member = " + member);
        }

        System.out.println("page.getSize() = " + page.getSize());
        System.out.println("page.getContent() = " + page.getContent());
        System.out.println("page.getNumber() = " + page.getNumber());
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        // em.clear()

        List<Member> result = memberRepository.findByUsername("member5");
        Member member = result.get(0);
        System.out.println("member = " + member);

        //then
        assertThat(resultCount).isEqualTo(3);
    }
    
    @Test
    public void findMemberLazy() throws Exception {
        //given
        // member1 -> teamA
        // member2 -> teamB
        
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member m1 = new Member("member1", 10, teamA);
        Member m2 = new Member("member2", 20, teamB);

        memberRepository.save(m1);
        memberRepository.save(m2);
        
        em.flush();
        em.clear();
        
        // when
        List<Member> members = memberRepository.findMemberFetchJoin();
        for (Member member : members) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() throws Exception {
        
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();
        
        // when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void lockTest() throws Exception {
        // given
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");

    }

    @Test
    public void calCustom() throws Exception {
        List<Member> result = memberRepository.findMemberCustomBy();
    }

    @Test
    public void queryByExample() throws Exception {
        //given
       Team teamA = new Team("teamA");
       em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        //Prove
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }

}  