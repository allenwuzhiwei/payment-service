package com.nusiss.paymentservice.repository;

import com.nusiss.paymentservice.entity.MoneyAccount;
import org.springframework.data.jpa.repository.JpaRepository;



public interface MoneyAccountRepository extends JpaRepository<MoneyAccount, Long> {
    /*
     根据用户ID和账户类型查询
     @param userId 用户ID
     @param accountType 账户类型
     @return MoneyAccount
     */
    MoneyAccount findByUserIdAndAccountType(Long userId, String accountType);

}
