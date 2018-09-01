package com.vimalselvam.cucumber.listener.state;

import java.util.function.Function;

public class ThreadLocalExtentState {

    private ThreadLocalExtentState() {}

    private static ThreadLocal<ExtentState> state = new InheritableThreadLocal<>();

    public static void modifyState(final Function<ExtentState, ExtentState> f) {
        ExtentState exState = state.get();
        if (exState == null) {
            exState = ExtentState.ExtentStateBuilder.anExtentState().build();
        }

        final ExtentState newState = f.apply(exState);
        state.set(newState);
    }

    public static ExtentState getState() {
        return state.get();
    }
}
