package ipsim.persistence;

import com.rickyclarkson.xml.DOMSimple;

public class XMLDeserialiserUtility
{
	public static XMLDeserialiser createXMLDeserialiser(final DOMSimple domSimple,final String input)
	{
		return new XMLDeserialiserImplementation(domSimple,input);
	}
}