package com.example.springpractice.config.batch.SendMail;

import com.example.springpractice.config.mail.MailService;
import com.example.springpractice.domain.Member;
import lombok.AllArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
@AllArgsConstructor
public class HumanEmailSender {
    private final DataSource dataSource;
    private final MailService emailService;
    
    //휴먼회원 이메일 확인
    @Bean
    public JdbcCursorItemReader<Member>humanUserReader(){
        return new JdbcCursorItemReaderBuilder<Member>()
                .fetchSize(10)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Member.class))
                .sql("SELECT count(*) FROM member where user_state = HUMAN")
                .name("humanUserReader")
                .build();
    }
    
    //이메일 발송
    @Bean
    public ItemWriter<Member> inactiveUserEmailSender() {
        return ((List<? extends Member> members) -> {
            for (Member member : members) {
                String to = member.getMemberEmail();
                String subject = "휴먼 회원 안내";
                String content = "안녕하세요, " + member.getMemberName() + "님! 휴먼 회원으로 전환되었습니다.";
                emailService.sendEmail(to, subject, content);
            }
        });
    }
}
