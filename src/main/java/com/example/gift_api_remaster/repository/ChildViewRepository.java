package com.example.gift_api_remaster.repository;

import com.example.gift_api_remaster.model.ChildView;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ChildViewRepository extends JpaRepository<ChildView, Long>, JpaSpecificationExecutor<ChildView> {

    @Query("SELECT c FROM ChildView c WHERE c.id = :childId")
    ChildView findByChildId(long childId, Specification specification);

}
