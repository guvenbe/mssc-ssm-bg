package guru.springframework.msscssmbg.services;

import guru.springframework.msscssmbg.domain.Payment;
import guru.springframework.msscssmbg.domain.PaymentEvent;
import guru.springframework.msscssmbg.domain.PaymentState;
import guru.springframework.msscssmbg.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentserviceImpl implements Paymentservice {

    public static final String PAYMENT_ID_HEADER = "payment_id";

    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState,PaymentEvent> stateMachineFactory;
    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendevent(paymentId, sm, PaymentEvent.PRE_AUTHORIZE);
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendevent(paymentId, sm, PaymentEvent.AUTH_APROVED);
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendevent(paymentId, sm, PaymentEvent.AUTH_DECLINED);
        return null;
    }

    private void  sendevent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event){
        Message msg = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();
        sm.sendEvent(msg);
    }

    private StateMachine<PaymentState, PaymentEvent> build(Long patmentId){
        Payment payment = paymentRepository.getOne(patmentId);
        StateMachine<PaymentState, PaymentEvent> sm = stateMachineFactory.getStateMachine(Long.toString(payment.getId()));
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma ->{
                    sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(), null ,null, null));
                });
        sm.start();
        return sm;
    }
}
