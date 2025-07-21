package com.example.gift_api_remaster.repository;

import com.example.gift_api_remaster.model.Child;
import com.example.gift_api_remaster.model.dto.ChildDto;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface ChildRepository extends JpaRepository<Child, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional(readOnly = false)
    Optional<Child> findWithLockingById(Long id);

    @Query("SELECT c.id FROM Child c")
    Page<Long> findAllIds(Pageable pageable);

    @Query("""
                SELECT new com.example.gift_api_remaster.model.dto.ChildDto(
                    c.id, c.name, c.surname, c.birthday, COUNT(g)
                  )
                 FROM Child c
                    LEFT JOIN c.gifts g
                        group by c.id, c.name, c.surname, c.birthday
            """)
    Page<ChildDto> findAllAsDto(Pageable pageable);

    @Query("SELECT c FROM Child c LEFT JOIN FETCH c.gifts WHERE c.id IN (:ids) ")
    List<Child> findAllWithGiftsByIdIn(@Param("ids") List<Long> ids);

}
