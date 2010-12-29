package com.rickyclarkson.xml;

import static ipsim.ExceptionHandler.expectDocument;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public final class XMLUtility
{
	public static Document xmlToDom(final String input)
	{
		final DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		factory.setValidating(false);

		final DocumentBuilder builder;

		try
		{
			builder=factory.newDocumentBuilder();

			final ErrorHandler errorHandler=new ErrorHandler()
			{
				@Override
                public void warning(final SAXParseException exception)
				{
				}

				@Override
                public void error(final SAXParseException exception)
				{
					warning(exception);
				}

				@Override
                public void fatalError(final SAXParseException exception)
				{
					warning(exception);
				}
			};

			builder.setErrorHandler(errorHandler);
		}
		catch (final ParserConfigurationException exception)
		{
			throw new RuntimeException(exception);
		}

		final Document document;

		try
		{
			document=builder.parse(new InputSource(new StringReader(input)));
		}
		catch (final SAXException exception)
		{
			return expectDocument("Malformed XML document");
		}
		catch (final IOException exception)
		{
			return expectDocument("Error in reading file");
		}

		return document;
	}
}