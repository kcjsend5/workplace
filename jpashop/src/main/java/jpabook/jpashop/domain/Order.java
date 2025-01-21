package jpabook.jpashop.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)//지연로딩 Member객체가 실제 사용될때 조회하도록 늦춤. 즉시 로딩은 FetchType.EAGER
    @JoinColumn(name = "member_id")//외래키를 가지는걸 알림
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)//cascade: 종속성: 특정 엔티티에 대해 특정한 작업을 수행하면 관련된 엔티티에도 동일한 작업을 수행한다는 의미
    private List<OrderItem> orderItems = new ArrayList<>();//주문이 삭제되면 주문한 제품 또한 삭제되어야 하기 때문에 종속성 설정

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Delivery delivery;

    private LocalDateTime orderDate;//@Temporal(날짜 타입을 매핑할때 사용),LocalDate, LocalDateTime을 사용할 때는 생략 가능

    @Enumerated(EnumType.ORDINAL)// ORDINAL(순서로 구분)은 예기치 못한 오류 발생 가능. STRING(문자열로 구분)은 공간을 크게 차지한다
    private OrderStatus status;

    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //비즈니스 로직: 엔티티가 비즈니스 로직을 가지는 것을 도메인 모델 패턴이라고 함. 반대로 서비스에 비즈니스 로직이 몰린걸 트랜젝션 스크립트 패턴이라고 함. 각각 장단점 존재
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){//...의 뜻은 여러개의 객체를 넣거나 아무런 객체를 안넣을수 있다는 뜻

        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem: orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송된 상품은 취소가 불가능 합니다");
        }
        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem: orderItems){
            orderItem.cancel();
        }
    }


    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem: orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

}
