package ipsim.connectivity.hub.incoming;

import fpeas.predicate.Predicate;
import static ipsim.Caster.asFunction;
import static ipsim.Caster.equalT;
import ipsim.awt.Point;
import ipsim.ExceptionHandler;
import static ipsim.gui.PositionUtility.getParent;
import static ipsim.gui.PositionUtility.getPosition;
import static ipsim.gui.PositionUtility.numPositions;
import static ipsim.gui.components.NetworkComponentUtility.pointsToStringWithoutDelimiters;
import ipsim.gui.components.PacketSourceVisitor2;
import ipsim.network.InvalidNetMaskException;
import ipsim.network.Network;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketSourceVisitor;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.hub.Hub;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.ComputerUtility;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLSerialiser;
import static ipsim.persistence.delegates.ComputerDelegate.computerDelegate;
import static ipsim.persistence.delegates.EthernetCableDelegate.cableDelegate;
import static ipsim.persistence.delegates.EthernetCardDelegate.cardDelegate;
import static ipsim.persistence.delegates.HubDelegate.hubDelegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Node;

public final class PacketSourceUtility
{
	public static final Predicate<PacketSource> isHub=new Predicate<PacketSource>()
	{
		@Override
        public boolean invoke(final PacketSource source)
		{
			return source.accept(new PacketSourceVisitor<Boolean>()
			{
				@Override
                public Boolean visit(final Card card)
				{
					return false;
				}

				@Override
                public Boolean visit(final Computer computer)
				{
					return false;
				}

				@Override
                public Boolean visit(final Cable cable)
				{
					return false;
				}

				@Override
                public Boolean visit(final Hub hub)
				{
					return true;
				}
			});
		}
	};

	public static boolean isCable(final PacketSource source)
	{
		return source.accept(new PacketSourceVisitor<Boolean>()
		{
			@Override
            public Boolean visit(final Card card)
			{
				return false;
			}

			@Override
            public Boolean visit(final Computer computer)
			{
				return false;
			}

			@Override
            public Boolean visit(final Cable cable)
			{
				return true;
			}

			@Override
            public Boolean visit(final Hub hub)
			{
				return false;
			}
		});
	}

	public static
	@Nullable
	Hub asHub(final PacketSource source)
	{
		return source.accept(new PacketSourceVisitor<Hub>()
		{
			@Override
            public Hub visit(final Card card)
			{
				return null;
			}

			@Override
            public Hub visit(final Computer computer)
			{
				return null;
			}

			@Override
            public Hub visit(final Cable cable)
			{
				return null;
			}

			@Override
            public Hub visit(final Hub hub)
			{
				return hub;
			}
		});
	}

	public static
	@Nullable
	Cable asCable(final PacketSource source)
	{
		return source.accept(new PacketSourceVisitor<Cable>()
		{
			@Override
            public Cable visit(final Card card)
			{
				return null;
			}

			@Override
            public Cable visit(final Computer computer)
			{
				return null;
			}

			@Override
            public Cable visit(final Cable cable)
			{
				return cable;
			}

			@Override
            public Cable visit(final Hub hub)
			{
				return null;
			}
		});
	}

	@Nullable
	public static Card asCard(final PacketSource source)
	{
		return source.accept(new PacketSourceVisitor<Card>()
		{
			@Override
            public Card visit(final Card card)
			{
				return card;
			}

			@Override
            public Card visit(final Computer computer)
			{
				return null;
			}

			@Override
            public Card visit(final Cable cable)
			{
				return null;
			}

			@Override
            public Card visit(final Hub hub)
			{
				return null;
			}
		});
	}

	@Nullable
	public static Computer asComputer(final PacketSource source)
	{
		return source.accept(new PacketSourceVisitor<Computer>()
		{
			@Override
            public Computer visit(final Card card)
			{
				return null;
			}

			@Override
            public Computer visit(final Computer computer)
			{
				return computer;
			}

			@Override
            public Computer visit(final Cable cable)
			{
				return null;
			}

			@Override
            public Computer visit(final Hub hub)
			{
				return null;
			}
		});
	}

	public static boolean isCard(final PacketSource source)
	{
		return asCard(source)!=null;
	}

	public static boolean isComputer(final PacketSource source)
	{
		return asComputer(source)!=null;
	}

	public static String asString(@NotNull final Network network, @NotNull final PacketSource source)
	{
		return source.accept(new PacketSourceVisitor<String>()
		{
			@Override
            public String visit(final Card card)
			{
				final StringBuilder answer=new StringBuilder("an Ethernet card");

				@Nullable
				final PacketSource parent=getParent(network, source, 0);

				if (parent!=null)
				{
					if (card.hasDeviceDrivers())
					{
						final CardDrivers cardWithDrivers=card.withDrivers;
						answer.append(" (");
						answer.append(cardWithDrivers.ipAddress.get());
						answer.append('/');
						try
						{
							answer.append(NetMaskUtility.getPrefixLength(cardWithDrivers.netMask.get()));
						}
						catch (InvalidNetMaskException e)
						{
							answer.append(NetMask.asString(cardWithDrivers.netMask.get().rawValue));
						}
						answer.append(')');
					}

					answer.append(" that is connected to ");

					answer.append(asString(network, parent));
				}
				else
				{
					answer.append(" (");

					answer.append(pointsToStringWithoutDelimiters(network, card));
					answer.append(')');
				}

				return answer.toString();
			}

			@Override
            public String visit(final Computer computer)
			{
				final String ipAddresses=ComputerUtility.ipAddressesToString(computer);

				final Point position;

				if (numPositions(computer)==0)
					position=null;
				else
					position=getPosition(network, computer, 0);

				final String computerPlusID="computer "+computer.computerID.asString();

				if (ipAddresses.length()==0)
				{
					if (position==null)
						return computerPlusID;

					return computerPlusID+" ("+pointsToStringWithoutDelimiters(network, computer)+')';
				}

				return computerPlusID+" ("+ipAddresses+')';
			}

			@Override
            public String visit(final Cable cable)
			{
				return "Ethernet cable number "+network.cableIDFor.run(cable)+' '+pointsToStringWithoutDelimiters(network, cable);
			}

			@Override
            public String visit(final Hub hub)
			{
				return "Hub number "+network.hubIDFor.run(hub)+" ("+pointsToStringWithoutDelimiters(network, hub)+')';
			}
		});
	}

	public static void writePacketSource(final XMLSerialiser serialiser, final PacketSource packetSource, final String name, final Network network)
	{
		packetSource.accept(new PacketSourceVisitor2()
		{
			@Override
            public void visit(@NotNull final Card card)
			{
				serialiser.writeObject(card, name, cardDelegate(network));
			}

			@Override
            public void visit(@NotNull final Computer computer)
			{
				serialiser.writeObject(computer, name, computerDelegate(network));
			}

			@Override
            public void visit(@NotNull final Cable cable)
			{
				serialiser.writeObject(cable, name, cableDelegate(network));
			}

			@Override
            public void visit(@NotNull final Hub hub)
			{
				serialiser.writeObject(hub, name, hubDelegate(network));
			}
		});
	}

	public static PacketSource readFromDeserialiser(final XMLDeserialiser deserialiser, final Node node, final String name,final Network network)
	{
		final String type=deserialiser.typeOfChild(node,name);

		if (equalT(hubDelegate(network).getIdentifier(),type))
			return deserialiser.readObject(node,name, hubDelegate(network), asFunction(Hub.class));

		if (equalT(computerDelegate(network).getIdentifier(),type))
			return deserialiser.readObject(node,name, computerDelegate(network),asFunction(Computer.class));

		if (equalT(cardDelegate(network).getIdentifier(),type))
			return deserialiser.readObject(node,name, cardDelegate(network),asFunction(Card.class));

		if (equalT(cableDelegate(network).getIdentifier(),type))
			return deserialiser.readObject(node,name, cableDelegate(network),asFunction(Cable.class));

		return ExceptionHandler.impossible();
	}
}