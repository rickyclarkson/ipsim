package ipsim.swing;

import fpeas.either.Either;
import fpeas.either.EitherUtility;
import fpeas.function.Function;
import static fpeas.function.FunctionUtility.constant;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetBlockUtility;
import ipsim.network.ethernet.NetBlockUtility.ParseFailure;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public final class NetBlockValidator implements DocumentValidator
{
	private NetBlock block;

	public NetBlockValidator(final NetBlock block)
	{
		this.block=block;
	}

	@Override
    public boolean isValid(final Document document)
	{
		final String string;

		try
		{
			string=document.getText(0,document.getLength());
		}
		catch (final BadLocationException exception)
		{
			throw new RuntimeException(exception);
		}

		final Either<NetBlock,ParseFailure> either=NetBlockUtility.createNetBlock(string);
		final Function<NetBlock,Boolean> constant=constant(true);
		final Function<ParseFailure,Boolean> constant2=constant(false);
		final boolean result=either.visit(constant,constant2);

		if (result)
			block=EitherUtility.unsafeLeft(either);

		return result;
	}

	public void setNetBlock(final NetBlock netBlock)
	{
		this.block=netBlock;
	}

	public NetBlock getNetBlock()
	{
		return block;
	}
}