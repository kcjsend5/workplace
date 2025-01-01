package hello.hello_spring.core.discount;

import hello.hello_spring.core.member.Grade;
import hello.hello_spring.core.member.Member;

public class FixDiscountPolicy implements DiscountPolicy{

    private int discountFixAmount = 1000;

    @Override
    public int discount(Member member, int price) {
        if(member.getGrade() == Grade.VIP)
        {
            return discountFixAmount;
        }
        else{
            return 0;
        }
    }
}
