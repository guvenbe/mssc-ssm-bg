package guru.springframework.msscssmbg.repository;

import guru.springframework.msscssmbg.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
