package jpabook.jpashop.service;

import jpabook.jpashop.domain.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor// 생성자를 만들고 final이 붙은 필드를 생성자에 추가: @Autowired는 생성자가 하나일때 자동으로 붙음으로 이 어노테이션만 붙이면 의존성 주입 완료,
@Transactional(readOnly = true)//트랜젝션에서 읽기 기능만 사용 가능하도록 허가: 성능향상, 클래스 단위 트랜젝션은 해당 클래스의 모든 메소드에 트랜젝션을 붙인것과 동일
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional//해당 어노테이션이 우선시 되므로 쓰기,수정,삭제 기능이 사용 가능함.
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long id){
        return itemRepository.findone(id);
    }

    @Transactional
    public void updateItem(Long id, String name, int price, int stockQuantity){
        Item item = itemRepository.findone(id);
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
    }
}
