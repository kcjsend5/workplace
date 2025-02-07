package hello.hello_spring.core.discount;

import hello.hello_spring.core.member.Grade;
import hello.hello_spring.core.member.Member;
import org.springframework.stereotype.Component;

@Component
public class RateDiscountPolicy implements DiscountPolicy{

    private final int discountPercent = 10;

    @Override
    public int discount(Member member, int price) {

        if(member.getGrade() == Grade.VIP){
            return price*discountPercent/100;
        }
        else{
            return 0;
        }
    }
}
