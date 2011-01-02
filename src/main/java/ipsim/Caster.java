package ipsim;

import fj.F;
import fj.Function;
import fj.data.Either;
import ipsim.lang.Runnables;
import ipsim.network.Problem;
import ipsim.network.connectivity.cable.CableType;
import ipsim.network.connectivity.ethernet.MacAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.NetBlock;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Caster
{
	public static boolean isHTMLFrameHyperlinkEvent(final HyperlinkEvent event)
	{
		return event instanceof HTMLFrameHyperlinkEvent;
	}

	public static Element asElement(final Node next)
	{
		return (Element)next;
	}

	public static JEditorPane asJEditorPane(final Object source)
	{
		return (JEditorPane)source;
	}

	public static Graphics2D asGraphics2D(final Graphics graphics)
	{
		return (Graphics2D)graphics;
	}

	public static HTMLFrameHyperlinkEvent asHTMLFrameHyperlinkEvent(final HyperlinkEvent event)
	{
		return (HTMLFrameHyperlinkEvent)event;
	}

	public static HTMLDocument asHTMLDocument(final Document document)
	{
		return (HTMLDocument)document;
	}

	public static JScrollPane asJScrollPane(final Container container)
	{
		return (JScrollPane)container;
	}

	public static Problem asProblem(final Object o)
	{
		return (Problem)o;
	}

	public static NetBlock asNetBlock(final Object other)
	{
		return (NetBlock)other;
	}

	public static boolean isElement(final Node node)
	{
		return node instanceof Element;
	}

	public static <T> F<T, Boolean> equalT(@NotNull final T first)
	{
		return new F<T, Boolean>()
		{
			@Override
            public Boolean f(final T second)
			{
				return first.equals(second);
			}
		};
	}

	public static <A,B extends Exception> A asFirst(final Either<A,B> either)
	{
		return either.either(Function.<A>identity(),Runnables.<B,A>wrapAndThrow());
	}

	public static IPAddress asIPAddress(final Object object)
	{
		return (IPAddress)object;
	}

	public static boolean isIPAddress(final Object object)
	{
		return object instanceof IPAddress;
	}

	public static boolean isMacAddress(final Object object)
	{
		return object instanceof MacAddress;
	}

	public static MacAddress asMacAddress(final Object object)
	{
		return (MacAddress)object;
	}

	public static boolean isNetMask(final Object object)
	{
		return object instanceof NetMask;
	}

	public static NetMask asNetMask(final Object object)
	{
		return (NetMask)object;
	}

	public static <T,R extends T> boolean equalT(@NotNull final T first,@NotNull final R second)
	{
		return first.equals(second);
	}

	public static CableType asCableType(final Object o)
	{
		return (CableType)o;
	}

	public static @NotNull <T> T asNotNull(final @Nullable T t)
	{
		return t==null ? ExceptionHandler.<T>impossible() : t;
	}

	public static <T> F<Object, T> asFunction(final Class<T> aClass)
	{
		return new F<Object, T>()
		{
			@Override
            @NotNull
			public T f(@NotNull final Object o)
			{
				return aClass.cast(o);
			}
		};
	}
}