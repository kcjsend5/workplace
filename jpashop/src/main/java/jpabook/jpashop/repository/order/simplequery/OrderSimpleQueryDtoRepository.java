package jpabook.jpashop.repository.order.simplequery;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryDtoRepository {

    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos(){
        return em.createQuery("select jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name,o.orderDate, o.status, d.address)" +
                "  from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderSimpleQueryDto.class).getResultList();
    }
}
