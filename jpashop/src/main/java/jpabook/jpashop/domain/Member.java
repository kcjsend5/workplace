package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "mamber_id")// @Column에 제약조건 추가 가능
    private Long id;

    private String name;

    @Embedded// 임베디드 타입
    private Address address;

    @OneToMany(mappedBy = "member") //외래키를 가지지 않는다는걸 알림
    private List<Order> orders = new ArrayList<>();
}
