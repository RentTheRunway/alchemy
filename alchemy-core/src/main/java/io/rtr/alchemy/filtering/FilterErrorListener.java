package io.rtr.alchemy.filtering;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.BitSet;

public class FilterErrorListener implements ANTLRErrorListener {
    @Override
    public void syntaxError(
            @NotNull Recognizer<?, ?> recognizer,
            @Nullable Object o,
            int i,
            int i2,
            @NotNull String s,
            @Nullable RecognitionException e) {
        throw new IllegalArgumentException(e);
    }

    @Override
    public void reportAmbiguity(
            @NotNull Parser parser,
            @NotNull DFA dfa,
            int i,
            int i2,
            boolean b,
            @Nullable BitSet bitSet,
            @NotNull ATNConfigSet atnConfigs) {
        throw new IllegalArgumentException("ambiguity");
    }

    @Override
    public void reportAttemptingFullContext(
            @NotNull Parser parser,
            @NotNull DFA dfa,
            int i,
            int i2,
            @Nullable BitSet bitSet,
            @NotNull ATNConfigSet atnConfigs) {
        throw new IllegalArgumentException("ambiguity");
    }

    @Override
    public void reportContextSensitivity(
            @NotNull Parser parser,
            @NotNull DFA dfa,
            int i,
            int i2,
            int i3,
            @NotNull ATNConfigSet atnConfigs) {
        throw new IllegalArgumentException("ambiguity");
    }
}
