package com.example.springpractice.config.batch.MemberListExcel;

import com.example.springpractice.domain.Member;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManagerFactory;
import java.util.List;

public class ExcelItemReader implements ItemReader<List<Member>> {
    @Autowired
    private EntityManagerFactory entityManagerFactory;


    @Override
    public List<Member> read() throws Exception{
        JpaPagingItemReader<List<Member>> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT m FROM Member m");
        reader.setPageSize(10);
        return reader.read();
    }
}
