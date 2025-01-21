package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static jpabook.jpashop.domain.QMember.member;
import static jpabook.jpashop.domain.QOrder.order;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long orderId){
        return em.find(Order.class,orderId);
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        QOrder order = QOrder.order;
        QMember member = QMember.member;
        return query
                .select(order)
                .from(order)
                .join(order.member, member)
                .where(statusEq(orderSearch.getOrderStatus()),
                        nameLike(orderSearch.getMemberName()))
                .limit(1000)
                .fetch();
    }
    private BooleanExpression statusEq(OrderStatus statusCond) {
        if (statusCond == null) {
            return null;
        }
        return order.status.eq(statusCond);
    }
    private BooleanExpression nameLike(String nameCond) {
        if (!StringUtils.hasText(nameCond)) {
            return null;
        }
        return member.name.like(nameCond);
    }


    public List<Order> findAllWithItem(){
        return em.createQuery("select distinct o from Order o"+"join fetch o.member m"+"join fetch o.delivery d"+"join fetch o.o.orderItems oi"+"join fetch oi.item i",Order.class).getResultList();
    }//distinct:중복 제거

    public List<Order> findAllWithMemberDelivery(){
        return em.createQuery("select o from Order o"+"join fetch o.member m"+"join fetch o.delivery d",Order.class).getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit){
        return em.createQuery("select o from Order o"+"join fetch o.member m"+"join fetch o.delivery d",Order.class).setFirstResult(offset).setMaxResults(limit).getResultList();
    }//페이징 setFirstResult(offset):조회 시작 위치 setMaxResults(limit):조회할 데이터 수
}
