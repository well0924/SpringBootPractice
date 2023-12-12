package com.example.springpractice.config.batch.IsHuman;

import com.example.springpractice.config.constant.UserState;
import com.example.springpractice.domain.Member;
import com.example.springpractice.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class HumanActive {
    private final MemberRepository memberRepository;

    @Bean
    @StepScope
    public ItemWriter<Member> inactiveUserWriter() {
        return ((List<? extends Member> members) ->{
            memberRepository.saveAll(members);
        });
    }

    @Bean
    @StepScope
    public ItemProcessor<Member,Member> inactiveUserProcessor() {
        //회원 엔티티에서 휴먼회원을 바꾸는 로직실행
        return Member::setUserStateHuman;
    }

    @Bean
    @StepScope //(1)
    public QueueItemReader<Member> inactiveUserReader() {
        //디비에서 가입후 6개월간 회원활동을 하지 않은 회원을 휴먼회원으로 전환하는 방법
        List<Member> oldUsers =
                memberRepository.findByUpdatedTimeBeforeAndUserStateEquals(
                        LocalDateTime.now().minusYears(1),
                        UserState.NONHUMAN);
        log.info(oldUsers.size());
        return new QueueItemReader<>(oldUsers); //(3)따로 만든 ItemReader로 휴먼회원을 추출
    }
}
