package com.example.gift_api_remaster.repository;

import com.example.gift_api_remaster.model.Child;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface ChildRepository extends JpaRepository<Child, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional(readOnly = false)
    Optional<Child> findWithLockingById(Long id);
}
