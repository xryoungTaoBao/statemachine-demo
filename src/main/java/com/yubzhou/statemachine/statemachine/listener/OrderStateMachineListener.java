package com.yubzhou.statemachine.statemachine.listener;

import com.yubzhou.statemachine.statemachine.enums.OrderEvent;
import com.yubzhou.statemachine.statemachine.enums.OrderState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

/**
 * Listener that logs all state machine lifecycle events for observability.
 */
@Slf4j
@Component
public class OrderStateMachineListener extends StateMachineListenerAdapter<OrderState, OrderEvent> {

    @Override
    public void stateChanged(State<OrderState, OrderEvent> from, State<OrderState, OrderEvent> to) {
        String fromName = from != null ? from.getId().name() : "INITIAL";
        String toName   = to   != null ? to.getId().name()   : "NULL";
        log.debug("State changed: {} -> {}", fromName, toName);
    }

    @Override
    public void transitionStarted(Transition<OrderState, OrderEvent> transition) {
        if (transition.getSource() != null && transition.getTarget() != null) {
            log.debug("Transition started: {} -> {} via {}",
                    transition.getSource().getId(),
                    transition.getTarget().getId(),
                    transition.getTrigger() != null ? transition.getTrigger().getEvent() : "N/A");
        }
    }

    @Override
    public void transitionEnded(Transition<OrderState, OrderEvent> transition) {
        if (transition.getSource() != null && transition.getTarget() != null) {
            log.debug("Transition ended: {} -> {}",
                    transition.getSource().getId(),
                    transition.getTarget().getId());
        }
    }
}
