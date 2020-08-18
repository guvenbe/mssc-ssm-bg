package guru.springframework.msscssmbg.config.actions;

import guru.springframework.msscssmbg.domain.PaymentEvent;
import guru.springframework.msscssmbg.domain.PaymentState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

/**
 * Created by jt on 2019-08-18.
 */
@Component
public class PreAuthApprovedAction implements Action<PaymentState, PaymentEvent> {
    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        System.out.println("Sending Notification of PreAuth Approved");
    }
}