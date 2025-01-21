package jpabook.jpashop.api;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;


    @GetMapping("/api/v1/orders")
    public List<Order> orderV1(){//엔티티를 직접 노출함으로 좋은 방법은 아님
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for(Order order: all){
            order.getMember().getName();//Lazy 강제 초기화
            order.getDelivery().getAddress();//Lazy 강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().map(o->o.getItem().getName());//Lazy 강제 초기화
        }
        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2(){//지연 로딩으로 너무 많은 SQL 실행
        List<Order> all = orderRepository.findAll(new OrderSearch());
        List<OrderDto> result = all.stream().map(o->new OrderDto(o)).collect(toList());
        return result;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3(){//페치 조인으로 SQL이 1번만 실행됨 단점: 페이징 불가능
        List<Order> all = orderRepository.findAllWithItem();
        List<OrderDto> result = all.stream().map(o->new OrderDto(o)).collect(toList());
        return result;
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderV3_page(@RequestParam(value = "offset",defaultValue = "0") int offset, @RequestParam(value = "limit",defaultValue = "100") int limit){
        //V3.1 엔티티를 조회해서 DTO로 변환 페이징 고려
        //ToOne 관계만 우선 모두 페치 조인으로 최적화
        //컬렉션 관계는 application.yml의 hibernate.default_batch_fetch_size, @BatchSize로 최적화
        //옵션을 사용하면 컬렉션이나, 프록시 객체를 한꺼번에 설정한 size 만큼 IN 쿼리로 조회한다.
        List<Order> all = orderRepository.findAllWithMemberDelivery(offset,limit);
        List<OrderDto> result = all.stream().map(o->new OrderDto(o)).collect(toList());
        return result;
    }//개별로 설정시 @BatchSize 사용 (컬렉션은 컬렉션 필드에, 엔티티는 엔티티 클래스에 적용)

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }



    @Data
    static class OrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order){
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getMember().getAddress();
            orderItems = order.getOrderItems().stream().map(orderItem->new OrderItemDto(orderItem)).collect(toList());
        }//뷰로 넘어가기 때문에 OrderItemDto를 사용
    }

    static class OrderItemDto{

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem){
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }

    }

}
