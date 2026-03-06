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

/**
 * Default implementation of {@link OrderStateMachineService}.
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
            sm.getExtendedState().getVariables().put(StateMachineContextKeys.REMARK,        request != null ? request.getRemark()       : null);
            if (request != null) {
                if (request.getTrackingNo()    != null) sm.getExtendedState().getVariables().put(StateMachineContextKeys.TRACKING_NO,    request.getTrackingNo());
                if (request.getCarrier()       != null) sm.getExtendedState().getVariables().put(StateMachineContextKeys.CARRIER,         request.getCarrier());
                if (request.getPaymentMethod() != null) sm.getExtendedState().getVariables().put(StateMachineContextKeys.PAYMENT_METHOD,  request.getPaymentMethod());
                if (request.getRefundReason()  != null) sm.getExtendedState().getVariables().put(StateMachineContextKeys.REFUND_REASON,   request.getRefundReason());
            }

            Message<OrderEvent> message = MessageBuilder.withPayload(event).build();
            org.springframework.statemachine.StateMachineEventResult<OrderState, OrderEvent> result =
                    sm.sendEvent(Mono.just(message)).blockFirst();
            boolean accepted = result != null &&
                    result.getResultType() == org.springframework.statemachine.StateMachineEventResult.ResultType.ACCEPTED;

            OrderState stateAfter = sm.getState().getId();
            long duration = System.currentTimeMillis() - start;

            if (accepted && !stateAfter.equals(stateBefore)) {
                saveHistory(order, stateBefore, stateAfter, event, request);
                saveEventLog(order, event, stateBefore, stateAfter, "ACCEPTED", null, null, duration);
                log.info("Order {} transitioned {} -> {} via {}", order.getOrderNo(), stateBefore, stateAfter, event);
            } else {
                saveEventLog(order, event, stateBefore, null, "DENIED",
                        "EVENT_REJECTED", "Event rejected or no state change", duration);
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
        OrderStateHistory history = new OrderStateHistory()
                .setOrderId(order.getId())
                .setOrderNo(order.getOrderNo())
                .setFromState(from.name())
                .setToState(to.name())
                .setEvent(event.name())
                .setEventType("NORMAL")
                .setOperatorId(request != null ? request.getOperatorId() : null)
                .setOperatorName(request != null ? request.getOperatorName() : null)
                .setRemark(request != null ? request.getRemark() : null);
        historyService.saveHistory(history);
    }

    private void saveEventLog(Order order, OrderEvent event, OrderState before, OrderState after,
                              String result, String errorCode, String errorMsg, long durationMs) {
        OrderEventLog log = new OrderEventLog()
                .setOrderId(order.getId())
                .setOrderNo(order.getOrderNo())
                .setEvent(event.name())
                .setCurrentState(before.name())
                .setTargetState(after != null ? after.name() : null)
                .setResult(result)
                .setErrorCode(errorCode)
                .setErrorMessage(errorMsg)
                .setExecutionTimeMs(durationMs);
        eventLogService.saveLog(log);
    }
}
