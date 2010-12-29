package com.rickyclarkson.xml;

import fpeas.lazy.Lazy;
import static ipsim.Caster.asElement;
import static ipsim.Caster.equalT;
import static ipsim.Caster.isElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public final class DOMSimple
{
	private final Lazy<Element> expectElement;
	private final Lazy<String> expectString;

	public DOMSimple(final Lazy<String> expectString,final Lazy<Element> expectElement)
	{
		this.expectString=expectString;
		this.expectElement=expectElement;
	}

	public static Node[] getChildNodes(final Node root,final String name)
	{
		final List<Node> nodes=new ArrayList<Node>();

		final NodeList children=root.getChildNodes();

		for (int a=0;a<children.getLength();a++)
		{
			final Node node=children.item(a);

			if (equalT(node.getNodeName(),name))
				nodes.add(node);
		}

		final Node[] answer=new Node[nodes.size()];

		nodes.toArray(answer);

		return answer;
	}

	public Element getChildElementNode(final Node root,final String name)
	{
		final NodeList children=root.getChildNodes();

		for (int a=0;a<children.getLength();a++)
		{
			final Node node=children.item(a);

			if (isElement(node)&&equalT(node.getNodeName(),name))
				return asElement(node);
		}

		return expectElement.invoke();
	}

	public String getAttribute(final Node node,final String name)
	{
		if (!node.hasAttributes())
			throw new IllegalArgumentException();

		final Node item=node.getAttributes().getNamedItem(name);

		if (item==null)
			return expectString.invoke();

		return item.getNodeValue();
	}
}