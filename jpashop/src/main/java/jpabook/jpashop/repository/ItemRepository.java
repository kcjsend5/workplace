package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor// 생성자를 만들고 final이 붙은 필드를 생성자에 추가: 엔티티 매니저도 자동으로 주입함
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item){
        if(item.getId() == null){
            em.persist(item);
        } else{
            em.merge(item);
        }
    }

    public Item findone(Long itemId){
        return em.find(Item.class,itemId);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i",Item.class).getResultList();
    }//페이징 API: 한번에 모든 데이터를 전송하는게 아닌 여러개의 페이지로 나누어 데이터를 전송하는것
     //스프링 JPA 페이징 API:
     //setFirstResult(int startPosition) : 조회 시작 위치(0부터 시작) setMaxResults(int maxResult) : 조회할 데이터 수
}
