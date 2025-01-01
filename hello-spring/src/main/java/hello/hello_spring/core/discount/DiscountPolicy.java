package hello.hello_spring.core.discount;

import hello.hello_spring.core.member.Member;

public interface DiscountPolicy {
    /**
     * @return 할인 대상 금액
     */
    int discount(Member member, int price);

}
