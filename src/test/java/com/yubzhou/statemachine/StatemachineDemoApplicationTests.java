package com.yubzhou.statemachine;

import com.yubzhou.statemachine.statemachine.enums.OrderEvent;
import com.yubzhou.statemachine.statemachine.enums.OrderState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 状态机应用集成测试
 */
@SpringBootTest(
    classes = StatemachineTestConfig.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE
)
class StatemachineDemoApplicationTests {

    @Autowired
    private StateMachineFactory<OrderState, OrderEvent> stateMachineFactory;

    @Test
    void contextLoads() {
        assertThat(stateMachineFactory).isNotNull();
    }

    @Test
    void testStateMachineCreation() {
        StateMachine<OrderState, OrderEvent> sm = stateMachineFactory.getStateMachine("test-order-1");
        sm.startReactively().block();
        assertThat(sm.getState().getId()).isEqualTo(OrderState.CREATED);
        sm.stopReactively().block();
    }

    @Test
    void testSubmitTransition() {
        StateMachine<OrderState, OrderEvent> sm = stateMachineFactory.getStateMachine("test-order-2");
        sm.startReactively().block();

        // CREATED -> PENDING via SUBMIT
        sm.sendEvent(reactor.core.publisher.Mono.just(
            org.springframework.messaging.support.MessageBuilder.withPayload(OrderEvent.SUBMIT).build()
        )).blockLast();

        assertThat(sm.getState().getId()).isEqualTo(OrderState.PENDING);
        sm.stopReactively().block();
    }
}
