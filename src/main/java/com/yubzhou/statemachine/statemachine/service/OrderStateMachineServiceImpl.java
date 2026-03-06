package com.yubzhou.statemachine.statemachine.service;

import com.yubzhou.statemachine.common.exception.BusinessException;
import com.yubzhou.statemachine.config.properties.StateMachineProperties;
import com.yubzhou.statemachine.order.dto.OrderEventRequest;
import com.yubzhou.statemachine.order.entity.Order;
import com.yubzhou.statemachine.order.entity.OrderEventLog;
import com.yubzhou.statemachine.order.entity.OrderStateHistory;
import com.yubzhou.statemachine.order.service.OrderEventLogService;
import com.yubzhou.statemachine.order.service.OrderStateHistoryService;
import com.yubzhou.statemachine.statemachine.constant.StateMachineContextKeys;
import com.yubzhou.statemachine.statemachine.enums.OrderEvent;
import com.yubzhou.statemachine.statemachine.enums.OrderState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Default implementation of {@link OrderStateMachineService}.
 * Creates or restores a state machine per order, sends the event, persists
 * a state-history record and an event-log entry, then releases the machine.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStateMachineServiceImpl implements OrderStateMachineService {

    private final StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;
    private final OrderStateHistoryService historyService;
    private final OrderEventLogService eventLogService;
    private final StateMachineProperties smProperties;

    @Override
    public void sendEvent(Order order, OrderEvent event, OrderEventRequest request) {
        long start = System.currentTimeMillis();
        OrderState stateBefore = OrderState.valueOf(order.getState());
        StateMachine<OrderState, OrderEvent> sm = acquireStateMachine(order.getId(), stateBefore);

        try {
            sm.getExtendedState().getVariables().put(StateMachineContextKeys.ORDER_ID,      order.getId());
            sm.getExtendedState().getVariables().put(StateMachineContextKeys.ORDER_NO,      order.getOrderNo());
            sm.getExtendedState().getVariables().put(StateMachineContextKeys.USER_ID,       order.getUserId());
            sm.getExtendedState().getVariables().put(StateMachineContextKeys.OPERATOR_ID,   request != null ? request.getOperatorId()   : null);
            sm.getExtendedState().getVariables().put(StateMachineContextKeys.OPERATOR_TYPE, request != null ? request.getOperatorType() : "SYSTEM");
            sm.getExtendedState().getVariables().put(StateMachineContextKeys.REMARK,        request != null ? request.getRemark()       : null);
            if (request != null) {
                if (request.getTrackingNo()    != null) sm.getExtendedState().getVariables().put(StateMachineContextKeys.TRACKING_NO,    request.getTrackingNo());
                if (request.getCarrier()       != null) sm.getExtendedState().getVariables().put(StateMachineContextKeys.CARRIER,         request.getCarrier());
                if (request.getPaymentMethod() != null) sm.getExtendedState().getVariables().put(StateMachineContextKeys.PAYMENT_METHOD,  request.getPaymentMethod());
                if (request.getRefundReason()  != null) sm.getExtendedState().getVariables().put(StateMachineContextKeys.REFUND_REASON,   request.getRefundReason());
            }

            Message<OrderEvent> message = MessageBuilder.withPayload(event).build();
            Boolean accepted = sm.sendEvent(Mono.just(message)).blockFirst();

            OrderState stateAfter = sm.getState().getId();
            long duration = System.currentTimeMillis() - start;

            if (Boolean.TRUE.equals(accepted) && !stateAfter.equals(stateBefore)) {
                saveHistory(order, stateBefore, stateAfter, event, request);
                saveEventLog(order, event, stateBefore, stateAfter, true, null, duration, request);
                log.info("Order {} transitioned {} -> {} via {}", order.getOrderNo(), stateBefore, stateAfter, event);
            } else {
                saveEventLog(order, event, stateBefore, null, false,
                        "Event rejected or no state change", duration, request);
                throw new BusinessException("Event " + event + " rejected in state " + stateBefore);
            }
        } finally {
            releaseStateMachine(order.getId(), sm);
        }
    }

    @Override
    public StateMachine<OrderState, OrderEvent> acquireStateMachine(Long orderId, OrderState currentState) {
        String machineId = smProperties.getMachineIdPrefix() + orderId;
        StateMachine<OrderState, OrderEvent> sm = stateMachineFactory.getStateMachine(machineId);
        sm.stopReactively().block();
        sm.getStateMachineAccessor()
          .doWithAllRegions(accessor -> accessor.resetStateMachineReactively(
              new org.springframework.statemachine.support.DefaultStateMachineContext<>(
                  currentState, null, null, null)).block());
        sm.startReactively().block();
        return sm;
    }

    @Override
    public void releaseStateMachine(Long orderId, StateMachine<OrderState, OrderEvent> stateMachine) {
        stateMachine.stopReactively().block();
    }

    // ------------------------------------------------------------------ helpers

    private void saveHistory(Order order, OrderState from, OrderState to,
                             OrderEvent event, OrderEventRequest request) {
        historyService.saveHistory(new OrderStateHistory()
                .setOrderId(order.getId())
                .setOrderNo(order.getOrderNo())
                .setFromState(from.name())
                .setToState(to.name())
                .setEvent(event.name())
                .setOperatorId(request != null ? request.getOperatorId() : null)
                .setOperatorType(request != null ? request.getOperatorType() : "SYSTEM")
                .setRemark(request != null ? request.getRemark() : null)
                .setCreateTime(LocalDateTime.now()));
    }

    private void saveEventLog(Order order, OrderEvent event, OrderState before, OrderState after,
                              boolean success, String errorMsg, long durationMs,
                              OrderEventRequest request) {
        eventLogService.saveLog(new OrderEventLog()
                .setOrderId(order.getId())
                .setOrderNo(order.getOrderNo())
                .setEvent(event.name())
                .setStateBefore(before.name())
                .setStateAfter(after != null ? after.name() : null)
                .setSuccess(success)
                .setErrorMsg(errorMsg)
                .setOperatorId(request != null ? request.getOperatorId() : null)
                .setOperatorType(request != null ? request.getOperatorType() : "SYSTEM")
                .setDurationMs(durationMs)
                .setCreateTime(LocalDateTime.now()));
    }
}
