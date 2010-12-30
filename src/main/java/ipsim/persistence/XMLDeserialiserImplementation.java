package ipsim.persistence;

import com.rickyclarkson.xml.DOMSimple;
import com.rickyclarkson.xml.XMLUtility;
import fj.F;
import ipsim.Caster;
import ipsim.ExceptionHandler;
import ipsim.util.Collections;
import java.util.Collection;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

import static com.rickyclarkson.xml.DOMSimple.getChildNodes;

public final class XMLDeserialiserImplementation implements XMLDeserialiser
{
	private final Map<Integer,Object> objectsRead=Collections.hashMap();

	private final String input;
	private final DOMSimple domSimple;

	public XMLDeserialiserImplementation(final DOMSimple domSimple,final String input)
	{
		this.input=input;
		this.domSimple=domSimple;
	}

	@Override
    public <T> T readObject(final SerialisationDelegate<T> delegate, final F<Object, T> caster)
	{
		return readObject(domSimple.getChildElementNode(XMLUtility.xmlToDom(input),"object"),delegate,caster);
	}

	private <T> T readObject(final Node node, final SerialisationDelegate<T> delegate, final F<Object,T> caster)
	{
		final String idString=domSimple.getAttribute(node,"id");

		final F<String,T> function=new F<String,T>()
		{
			@Override
            @NotNull
			public T f(@NotNull final String idString2)
			{
				final int id=Integer.parseInt(idString2);

				final Object result=objectsRead.get(id);

				if (result!=null)
					return caster.f(result);

				T object=delegate.construct();

				objectsRead.put(id,object);
				object=delegate.readXML(XMLDeserialiserImplementation.this,node,object);
				objectsRead.put(id,object);

				return object;
			}
		};

		return function.f(idString);
	}

	@Override
    @Nullable
	public String readAttribute(final Node node,final String name)
	{
		return readAttributeImpl(node,name);
	}

	@Nullable
	private String readAttributeImpl(final Node node,final String name)
	{
		final Node[] nodes=getChildNodes(node,"attribute");

		for (final Node element: nodes)
		{
			final String value=domSimple.getAttribute(element, "value");

			final String attrName=domSimple.getAttribute(element, "name");
			if (attrName!=null && Caster.equalT(attrName,name))
				return value;
		}

		return null;
	}

	@Override
    public String[] getObjectNames(final Node node)
	{
		final Node[] nodes=getChildNodes(node,"object");

		final Collection<String> list=Collections.arrayList();

		for (final Node element: nodes)
		{
			final String name=domSimple.getAttribute(element,"name");

			list.add(name);
		}

		final String[] array=new String[list.size()];

		list.toArray(array);

		return array;
	}

	@Override
    @Nullable
	public <T> T readObject(final Node node,final String name,final SerialisationDelegate<T> delegate, final F<Object,T> caster)
	{
		final Node[] nodes=getChildNodes(node,"object");

		for (final Node node2 : nodes)
		{
			//if something, keep the same
			//if nothing, and DOMSimple.getAttribute(nodes[a],"name") is name, change to that.

			if (Caster.equalT(domSimple.getAttribute(node2, "name"), name))
				return readObject(node2,delegate,caster);
		}

		return null;
	}

	@Override
    public String typeOfChild(final Node node,final String name)
	{
		for (final Node node2: getChildNodes(node,"object"))
			if (Caster.equalT(domSimple.getAttribute(node2,"name"),name))
				return domSimple.getAttribute(node2,"serialiser");

		return ExceptionHandler.impossible();
	}
}