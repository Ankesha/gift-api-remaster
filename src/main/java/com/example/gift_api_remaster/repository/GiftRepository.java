package com.example.gift_api_remaster.repository;

import com.example.gift_api_remaster.model.Gift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface GiftRepository extends JpaRepository<Gift, Long> {

    List<Gift> findAllByChildId(long childId);

    Optional<Gift> findByIdAndChildId(long id, long childId);

    boolean existsByIdAndChildId(long id, long childId);


}
