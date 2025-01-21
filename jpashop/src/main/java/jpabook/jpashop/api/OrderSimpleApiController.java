package jpabook.jpashop.api;


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDtoRepository;
import jpabook.jpashop.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryDtoRepository orderSimpleQueryDtoRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> orderV1(){
        List<Order> all = orderRepository.findAll(new OrderSearch());//엔티티를 직접 노출
        for(Order order:all){
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress();
        }
        return all;
    }//jackson 라이브러리는 기본적으로 이 프록시 객체를 json으로 어떻게 생성해야 하는지 모름 예외 발생
     //Hibernate5JakartaModule을 스프링 빈으로 등록하면 해결(스프링 부트 사용중). 보통 엔티티 직접 사용안하니 그냥 DTO로 변환해서씀

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2(){
        List<Order> all = orderRepository.findAll(new OrderSearch());
        List<SimpleOrderDto> result = all.stream().map(o->new SimpleOrderDto(o)).collect(toList());
        return result;
    }
    //엔티티를 DTO로 변환하는 일반적인 방법이다.
    //쿼리가 총 1 + N + N번 실행된다. order 조회 1번,order -> member 지연 로딩 조회 N 번,order -> delivery 지연 로딩 조회 N 번

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3(){
        List<Order> all = orderRepository.findAllWithMemberDelivery();//엔티티를 페치 조인(fetch join)을 사용해서 쿼리 1번에 조회
        List<SimpleOrderDto> result = all.stream().map(o->new SimpleOrderDto(o)).collect(toList());
        return result;
    }//페치조인은 해당 객체의 연관관계도 불러오기때문에 지연로딩 X

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4(){
        return orderSimpleQueryDtoRepository.findOrderDtos();

    }

    @Data
    static class SimpleOrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getMember().getAddress();
        }

    }

}
