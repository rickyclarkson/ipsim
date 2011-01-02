package ipsim.swing;

import fj.F;
import fj.Function;
import fj.data.Either;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetBlockUtility;
import ipsim.network.ethernet.NetBlockUtility.ParseFailure;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public final class NetBlockValidator implements DocumentValidator {
    private NetBlock block;

    public NetBlockValidator(final NetBlock block) {
        this.block = block;
    }

    @Override
    public boolean isValid(final Document document) {
        final String string;

        try {
            string = document.getText(0, document.getLength());
        } catch (final BadLocationException exception) {
            throw new RuntimeException(exception);
        }

        final Either<NetBlock, ParseFailure> either = NetBlockUtility.createNetBlock(string);
        final F<NetBlock, Boolean> constant = Function.constant(true);
        final F<ParseFailure, Boolean> constant2 = Function.constant(false);
        final boolean result = either.either(constant, constant2);

        if (result)
            block = either.left().value();

        return result;
    }

    public void setNetBlock(final NetBlock netBlock) {
        this.block = netBlock;
    }

    public NetBlock getNetBlock() {
        return block;
    }
}