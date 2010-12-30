package ipsim.network;

import fj.F;
import fj.Function;
import fpeas.sideeffect.SideEffect;
import fpeas.sideeffect.SideEffectUtility;
import ipsim.Caster;
import ipsim.Global;
import ipsim.NetworkContext;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.gui.PositionUtility;
import ipsim.io.IOUtility;
import ipsim.lang.Assertion;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketSourceAndIndex;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.hub.Hub;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.persistence.SerialisationDelegate;
import ipsim.persistence.XMLDeserialiser;
import ipsim.persistence.XMLDeserialiserUtility;
import ipsim.persistence.XMLSerialiser;
import ipsim.tree.Trees;
import ipsim.util.Collections;
import ipsim.webinterface.WebInterface;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ipsim.Caster.equalT;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asCable;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asCard;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.asComputer;
import static ipsim.network.ethernet.ComputerUtility.cardsWithDrivers;
import static ipsim.persistence.delegates.NetworkDelegate.networkDelegate;
import static ipsim.util.Collections.add;
import static ipsim.util.Collections.hashSet;

public class NetworkUtility
{
	public static void loadFromFile(final Network network, final File file)
	{
		loadFromFile(network, file, SideEffectUtility.<IOException>throwRuntimeException());
	}

	public static void loadFromString(final Network network, final String xmlString)
	{
		network.topLevelComponents.clear();

		final XMLDeserialiser deserialiser=XMLDeserialiserUtility.createXMLDeserialiser(network.domSimple, xmlString);

		network.computerIDs.clear();

		deserialiser.readObject(networkDelegate(network), Caster.asFunction(Network.class));
	}

	public static void placeholderForWhenNetworkChanges(final Problem problem)
	{
		if (problem==null)
				Global.global.get().statusBar.setText("");
			else
				Global.global.get().statusBar.setText(problem.asString());
	}

	public static void saveToFile(final Network network, @NotNull final File filename) throws IOException
	{
		Assertion.assertNotNull(filename);

		if (filename.getName().startsWith("@"))
		{
			final StringWriter stringWriter=new StringWriter();

			final boolean tempModified=network.modified;
			saveToWriter(network, stringWriter);
			network.modified=tempModified;

			try
			{
				final String output=WebInterface.putNamedConfiguration(filename.getName(), stringWriter.toString());
				if (!output.startsWith("102"))
					NetworkContext.errors(output);
			}
			catch (final IOException exception)
			{
				NetworkContext.errors(exception.getMessage());
			}
		}
		else
		{
			final Writer bufferedWriter=new BufferedWriter(new FileWriter(filename));
			try
			{
				saveToWriter(network, bufferedWriter);
			}
			finally
			{
				try
				{
					bufferedWriter.close();
				}
				catch (IOException exception)
				{
					exception.printStackTrace();
				}
			}
		}
	}

	public static void saveToWriter(final Network network, final Writer writer)
	{
		saveObjectToWriter(writer, network, "network", networkDelegate(network));
		network.modified=false;
	}

	public static String saveToString(final Network network)
	{
		final StringWriter writer=new StringWriter();
		saveToWriter(network, writer);
		return writer.toString();
	}

	public static Iterable<PacketSource> getDepthFirstIterable(final Network network)
	{
		return Trees.getDepthFirstIterable(Trees.nodify(network, Collections.map(PositionUtility.getRootComponents(network), PacketSourceAndIndex.getPacketSource)));
	}

	public static int getNumberOfSubnets(final Network network)
	{
		final Iterable<Computer> computers=getAllComputers(network);

		final Collection<Integer> subnets=hashSet();

		for (final Computer computer : computers)
			for (final CardDrivers cardWithDrivers: cardsWithDrivers(computer))
			{
				final int mask=cardWithDrivers.netMask.get().rawValue;
				final int ip=cardWithDrivers.ipAddress.get().rawValue;

				subnets.add(mask&ip);
			}

		return subnets.size();
	}

	public static Collection<Computer> getComputersByIP(final Network network, final IPAddress ipAddress)
	{
		final Collection<Computer> computers=new HashSet<Computer>();

		for (final PacketSource component : getDepthFirstIterable(network))
		{
			@Nullable
			final Computer computer=asComputer(component);

			if (computer!=null)
			{
				for (final CardDrivers card: cardsWithDrivers(computer))
					if (equalT(card.ipAddress.get(), ipAddress))
						computers.add(computer);
			}
		}

		return computers;
	}

	public static final F<Network, Iterable<Hub>> getAllHubs=new F<Network, Iterable<Hub>>()
	{
		@Override
        @NotNull
		public Iterable<Hub> f(@NotNull final Network network)
		{
			return getAllHubs(network);
		}
	};

	public static List<Hub> getAllHubs(final Network network)
	{
		final Iterable<PacketSource> components=getDepthFirstIterable(network);

		final List<Hub> hubs=new ArrayList<Hub>();

		for (final PacketSource component : components)
		{
			@Nullable final Hub hub=PacketSourceUtility.asHub(component);

			if (hub!=null)
				hubs.add(hub);
		}

		return hubs;
	}

	public static void loadFromFile(final Network network, final File file, final SideEffect<IOException> ioException)
	{
		try
		{
			IOUtility.readWholeResource(file.toURI().toURL()).either(new F<String, Runnable>()
			{
				@Override
                @NotNull
				public Runnable f(@NotNull final String input)
				{
					return new Runnable()
					{
						@Override
                        public void run()
						{
							loadFromString(network, input);
						}
					};
				}
			}, new F<IOException, Runnable>()
			{
				@Override
                @NotNull
				public Runnable f(@NotNull final IOException exception)
				{
					return new Runnable()
					{
						@Override
                        public void run()
						{
							ioException.run(exception);
						}
					};
				}
			}).run();
		}
		catch (final MalformedURLException exception)
		{
			throw new RuntimeException(exception);
		}
	}

	public static final F<Network, Iterable<Computer>> getAllComputers=new F<Network, Iterable<Computer>>()
	{
		@Override
        @NotNull
		public Iterable<Computer> f(@NotNull final Network network)
		{
			return getAllComputers(network);
		}
	};

	public static Collection<Computer> getAllComputers(final Network network, final F<Computer, Boolean> condition)
	{
		final Iterable<PacketSource> allComponents=getDepthFirstIterable(network);

		final Collection<Computer> computers=new HashSet<Computer>();

		for (final PacketSource component : allComponents)
		{
			@Nullable
			final Computer computer=asComputer(component);

			if (computer!=null && condition.f(computer))
				computers.add(computer);
		}

		return computers;
	}

	public static final F<Network, Iterable<Card>> getAllCards=new F<Network, Iterable<Card>>()
	{
		@Override
        @NotNull
		public Iterable<Card> f(@NotNull final Network context)
		{
			return getAllCards(context);
		}
	};

	public static Collection<Card> getAllCards(final Network network)
	{
		final Iterable<PacketSource> allComponents=getDepthFirstIterable(network);

		final Collection<Card> cards=new HashSet<Card>();

		for (final PacketSource component : allComponents)
		{
			final Card card=asCard(component);

			if (card!=null)
				cards.add(card);
		}

		return cards;
	}

	public static Collection<CardDrivers> getAllCardsWithDrivers(final Network network)
	{
		final Iterable<PacketSource> allComponents=getDepthFirstIterable(network);

		final Collection<CardDrivers> cards=new HashSet<CardDrivers>();

		for (final PacketSource component : allComponents)
		{
			@Nullable final Card card=asCard(component);

			if (card!=null)
			{
				@Nullable
				final CardDrivers withDrivers=card.withDrivers;

				if (withDrivers!=null)
					add(cards).run(withDrivers);
			}
		}

		return cards;
	}

	public static F<Network, Iterable<Cable>> getAllCables()
	{
		return new F<Network, Iterable<Cable>>()
		{
			@Override
            @NotNull
			public Iterable<Cable> f(@NotNull final Network context)
			{
				return getAllCables(context);
			}
		};
	}

	public static Collection<Cable> getAllCables(final Network network)
	{
		final F<Cable, Boolean> filter= Function.constant(true);
		return getAllCables(network, filter);
	}

	public static Collection<Cable> getAllCables(final Network network, final F<Cable, Boolean> filter)
	{
		final Iterable<PacketSource> allComponents=getDepthFirstIterable(network);

		final Collection<Cable> cables=hashSet();

		for (final PacketSource component : allComponents)
		{
			@Nullable final Cable cable=asCable(component);

			if (cable!=null && filter.f(cable))
				cables.add(cable);
		}

		return cables;
	}

	public static <T> void saveObjectToWriter(final Writer writer, final T object, final String name, final SerialisationDelegate<T> delegate)
	{
		final XMLSerialiser serialiser=new XMLSerialiser(writer);

		serialiser.writeObject(object, name, delegate);

		serialiser.close();
	}

	public static Collection<Computer> getAllComputers(final Network network)
	{
		final F<Computer, Boolean> condition=Function.constant(true);
		return getAllComputers(network, condition);
	}
}