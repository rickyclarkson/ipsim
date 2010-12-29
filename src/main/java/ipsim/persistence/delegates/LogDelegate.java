package ipsim.persistence.delegates;

import static ipsim.Caster.asFunction;
import fpeas.function.Function;
import ipsim.Caster;
import ipsim.network.Network;
import ipsim.persistence.SerialisationDelegate;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLSerialiser;
import static ipsim.persistence.delegates.DefaultCommandDelegate.defaultCommandDelegate;
import ipsim.util.Collections;
import static ipsim.util.Collections.arrayListCopy;
import org.w3c.dom.Node;

import java.util.List;

public final class LogDelegate
{
	public static SerialisationDelegate<List<? extends String>> logDelegate(final Network network)
	{
		return new SerialisationDelegate<List<? extends String>>()
		{
			@Override
            public void writeXML(final XMLSerialiser serialiser, final List<? extends String> log)
			{
				int a=0;

				for (final String entry : log)
				{
					serialiser.writeObject(new StringBuffer(entry), "entry "+a, defaultCommandDelegate);
					a++;
				}
			}

			@Override
            public List<? extends String> readXML(final XMLDeserialiser deserialiser, final Node node, List<? extends String> log)
			{
				log.clear();

				final String[] names=deserialiser.getObjectNames(node);

				for (final String name : names)
					if (name.startsWith("entry "))
					{
						final Function<List<? extends String>, List<String>> clone=arrayListCopy();
						log=Collections.add(clone,log,Caster.asNotNull(deserialiser.readObject(node, name,DefaultCommandDelegate.defaultCommandDelegate, asFunction(StringBuffer.class))).toString());
					}

				return log;
			}

			@Override
            public List<? extends String> construct()
			{
				return network.log;
			}

			@Override
            public String getIdentifier()
			{
				return "ipsim.persistence.delegates.LogDelegate";
			}
		};
	}
}