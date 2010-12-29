package ipsim.lang;

import java.io.IOException;

public final class CheckedIllegalStateException extends Exception
{
	private static final long serialVersionUID=-3551664599585322674L;
	
	public CheckedIllegalStateException()
	{
	}
	
	public CheckedIllegalStateException(final String message)
	{
		super(message);
	}

	public CheckedIllegalStateException(final IOException exception)
        {
	        super(exception);
        }
}