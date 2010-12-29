package ipsim.persistence.delegates;

import fpeas.function.Function;
import ipsim.Caster;
import ipsim.Globals;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import static ipsim.gui.PositionUtility.hasParent;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import ipsim.network.Problem;
import ipsim.network.connectivity.PacketSource;
import ipsim.persistence.SerialisationDelegate;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLSerialiser;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;

import java.util.List;

public class NetworkDelegate
{
	public static SerialisationDelegate<Network> networkDelegate(final Network network)
	{
		return new SerialisationDelegate<Network>()
		{
			@Override
            public void writeXML(final XMLSerialiser serialiser, final Network object)
			{
				serialiser.writeAttribute("version", Globals.fileFormatVersion);

				if (network.problem!=null)
					serialiser.writeObject(network.problem, "problem", ProblemDelegate.problemDelegate);

				int a=0;

				final Iterable<PacketSource> components=NetworkUtility.getDepthFirstIterable(network);

				for (final PacketSource component : components)
				{
					if (hasParent(network, component, 0))
						continue;

					if (PacketSourceUtility.isCard(component) && hasParent(network, component, 1))
						continue;

					PacketSourceUtility.writePacketSource(serialiser, component, "child "+a, network);
					a++;
				}

				serialiser.writeObject(network.log, "log", LogDelegate.logDelegate(network));
			}

			@Override
            public Network readXML(final XMLDeserialiser deserialiser, final Node node, final Network object)
			{
				for (final String name : deserialiser.getObjectNames(node))
					if (name.startsWith("child "))
						PacketSourceUtility.readFromDeserialiser(deserialiser,node, name,network);

				network.log=deserialiser.readObject(node, "log",LogDelegate.logDelegate(network), new Function<Object, List<? extends String>>()
				{
					@Override
                    @NotNull
					public List<? extends String> run(@NotNull final Object o)
					{
						return (List<? extends String>)o;
					}
				});

				network.problem=deserialiser.readObject(node, "problem",ProblemDelegate.problemDelegate, Caster.asFunction(Problem.class));
				network.modified=false;

				return network;
			}

			@Override
            public Network construct()
			{
				return network;
			}

			@Override
            public String getIdentifier()
			{
				return "ipsim.persistence.delegates.NetworkDelegate";
			}
		};
	}
}