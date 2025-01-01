package hello.hello_spring.core;

import hello.hello_spring.core.discount.DiscountPolicy;
import hello.hello_spring.core.discount.RateDiscountPolicy;
import hello.hello_spring.core.member.MemberRepository;
import hello.hello_spring.core.member.MemberService;
import hello.hello_spring.core.member.MemberServiceImp;
import hello.hello_spring.core.member.MemoryMemberRepository;
import hello.hello_spring.core.order.OrderService;
import hello.hello_spring.core.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MemberService memberService(){
        return new MemberServiceImp(memberRepository());
    }
    
    @Bean
    public MemberRepository memberRepository(){
        return new MemoryMemberRepository();
    }

    @Bean
    public DiscountPolicy discountPolicy(){
        return new RateDiscountPolicy();
    }

    @Bean
    public OrderService orderService(){
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

}
