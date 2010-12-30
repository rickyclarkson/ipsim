package ipsim.persistence.delegates;

import fj.F;
import ipsim.Caster;
import ipsim.network.Network;
import ipsim.persistence.SerialisationDelegate;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLSerialiser;
import ipsim.util.Collections;
import java.util.List;
import org.w3c.dom.Node;

import static ipsim.Caster.asFunction;
import static ipsim.persistence.delegates.DefaultCommandDelegate.defaultCommandDelegate;
import static ipsim.util.Collections.arrayListCopy;

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
						final F<List<? extends String>, List<String>> clone=arrayListCopy();
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