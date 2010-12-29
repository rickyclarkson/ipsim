package ipsim;

import com.rickyclarkson.java.lang.Throwables;
import fpeas.lazy.Lazy;
import ipsim.gui.ExceptionReportDialogUtility;
import ipsim.lang.AssertionException;
import ipsim.lang.DynamicVariable;
import ipsim.network.connectivity.ip.NetMask;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.lang.Thread.UncaughtExceptionHandler;

public class ExceptionHandler
{
	public static UncaughtExceptionHandler createExceptionHandler()
	{
		return new UncaughtExceptionHandler()
		{
			@Override
            public void uncaughtException(final Thread thread,final Throwable exception)
			{
				Global.global.get().logger.severe(Throwables.toString(exception));
				ExceptionReportDialogUtility.handle(Global.global.get().frame,exception);
			}
		};
	}

	public static <T> T impossible()
	{
		throw new AssertionException();
	}

	public static NetMask expectNetMask(final Object s)
	{
		throw new RuntimeException(s+" is not a NetMask");
	}

	public static String expectString()
	{
		return expectString.get().invoke();
	}

	public static final DynamicVariable<Lazy<String>> expectString=new DynamicVariable<Lazy<String>>(new Lazy<String>()
	{
		@Override
        public String invoke()
		{
			throw new RuntimeException("A string was expected, but none available");
		}
	});

	//reason should disappear when a better handler is written.
	public static Document expectDocument(final String reason)
	{
		throw new RuntimeException(reason);
	}

	public static Element expectElement()
	{
		throw new RuntimeException();
	}

	public static Object objectExpected()
	{
		throw new UnsupportedOperationException();
	}

	public static <T> Lazy<T> impossibleRef()
	{
		return new Lazy<T>()
		{
			@Override
            public T invoke()
			{
				return ExceptionHandler.<T>impossible();
			}
		};
	}
}