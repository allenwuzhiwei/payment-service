package com.nusiss.paymentservice.repository;

import com.nusiss.paymentservice.entity.MoneyAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoneyAccountRepository extends JpaRepository<MoneyAccount, Long> {
    Optional<MoneyAccount> findByUserIdAndCurrency(Long userId, String currency);
}
