package guru.springframework.msscssmbg.services;

import guru.springframework.msscssmbg.domain.Payment;
import guru.springframework.msscssmbg.domain.PaymentEvent;
import guru.springframework.msscssmbg.domain.PaymentState;
import guru.springframework.msscssmbg.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@SpringBootTest
class PaymentserviceImplTest {

    @Autowired
    PaymentService paymentservice;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(new BigDecimal(12.99)).build();
    }

    @Transactional
    @Test
    void preAuth() {
        Payment savedPayment = paymentservice.newPayment(payment);
        System.out.println("Should be NEW");
        System.out.println(savedPayment);
        StateMachine<PaymentState, PaymentEvent> sm = paymentservice.preAuth(savedPayment.getId());

        System.out.println("Should be PRE_AUTH or PRE_AUTH_DECLINED");
        Payment preAuthedPayment = paymentRepository.getOne(savedPayment.getId());
        System.out.println(sm.getState().getId());
        System.out.println(preAuthedPayment);
    }

    @Transactional
    @RepeatedTest(10)
    public void testAuth() {
        Payment savedPayment = paymentservice.newPayment(payment);
        StateMachine<PaymentState, PaymentEvent> preAuthSM = paymentservice.preAuth(savedPayment.getId());
        if (preAuthSM.getState().getId()==PaymentState.PRE_AUTH){
            StateMachine<PaymentState, PaymentEvent> authSM = paymentservice.authorizePayment(savedPayment.getId());
            System.out.println("Result of Auth: " + authSM.getState().getId());
        }else{
            System.out.println(("Payment fialed preAuth...."));
        }
    }
}