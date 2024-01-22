package ma.digiup.assignement.repository;

import ma.digiup.assignement.domain.AuditDeposit;
import ma.digiup.assignement.domain.AuditTransfer;
import ma.digiup.assignement.domain.MoneyDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface moneyDepositRepository extends JpaRepository<MoneyDeposit, Long> {
}
