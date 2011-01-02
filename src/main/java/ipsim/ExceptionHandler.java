package ipsim;

import com.rickyclarkson.java.lang.Throwables;
import fj.P1;
import ipsim.gui.ExceptionReportDialogUtility;
import ipsim.lang.AssertionException;
import ipsim.lang.DynamicVariable;
import ipsim.network.connectivity.ip.NetMask;
import java.lang.Thread.UncaughtExceptionHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
		return expectString.get()._1();
	}

	public static final DynamicVariable<P1<String>> expectString=new DynamicVariable<P1<String>>(new P1<String>()
	{
		@Override
        public String _1()
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

	public static <T> P1<T> impossibleRef()
	{
		return new P1<T>()
		{
			@Override
            public T _1()
			{
				return ExceptionHandler.<T>impossible();
			}
		};
	}
}