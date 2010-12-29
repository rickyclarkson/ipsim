package ipsim.network.breakage;

import static ipsim.util.Collections.mapWith;
import fpeas.function.Function;
import fpeas.maybe.Maybe;
import fpeas.maybe.MaybeUtility;
import static fpeas.maybe.MaybeUtility.just;
import static fpeas.maybe.MaybeUtility.nothing;
import fpeas.pair.Pair;
import static fpeas.pair.PairUtility.pair;
import fpeas.sideeffect.SideEffect;
import static fpeas.sideeffect.SideEffectUtility.doNothing;
import ipsim.Global;
import static ipsim.Global.getNetworkContext;
import ipsim.NetworkContext;
import static ipsim.NetworkContext.askUserForNumberOfFaults;
import static ipsim.NetworkContext.confirm;
import static ipsim.gui.PositionUtility.getPosition;
import static ipsim.gui.PositionUtility.setPosition;
import ipsim.gui.UserMessages;
import static ipsim.gui.UserPermissions.FREEFORM_WITH_BREAKS;
import ipsim.network.Network;
import ipsim.network.NetworkUtility;
import static ipsim.network.NetworkUtility.getAllCardsWithDrivers;
import static ipsim.network.NetworkUtility.getAllComputers;
import static ipsim.network.NetworkUtility.getAllHubs;
import ipsim.network.conformance.ConformanceTestsUtility;
import ipsim.network.connectivity.ConnectivityResults;
import ipsim.network.connectivity.ConnectivityTest;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.CardDrivers;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.Route;
import ipsim.network.connectivity.computer.RoutingTable;
import ipsim.network.connectivity.hub.Hub;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.ethernet.ComputerUtility;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetMaskUtility;
import static ipsim.network.ip.IPAddressUtility.randomIP;
import ipsim.swing.SwingWorker;
import static ipsim.util.Collections.arrayList;
import ipsim.util.Collections;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

public class BreakNetwork
{
	public static void breakNetwork()
	{
		if (!getNetworkContext().userPermissions.allowBreakingNetwork())
		{
			NetworkContext.errors("You are not allowed to break the network during a test");
			return;
		}

		if (getNetworkContext().network.modified)
		{
			if (!confirm("The network has been modified.  Continue anyway?"))
				return;
		}

		final int numberOfFaultsVar=askUserForNumberOfFaults();

		final ProgressMonitor[] monitor=new ProgressMonitor[]{new ProgressMonitor(Global.global.get().frame, "Breaking network                  ", "Breaking network                    ", 0, 100)};

		monitor[0].setMillisToPopup(0);
		monitor[0].setMillisToDecideToPopup(0);
		monitor[0].setProgress(1);

		new SwingWorker<Void,Object>()
		{
			@Override
			public Void doInBackground()
			{
				doIt();
				return null;
			}

			private void doIt()
			{
				getNetworkContext().networkView.ignorePaints.set(true);

				monitor[0].setNote("Testing connectivity");

				final ConnectivityResults results=ConnectivityTest.testConnectivity(Global.getNetworkContext().network,new SideEffect<String>()
				{
					@Override
                    public void run(final String s)
					{
						monitor[0].setNote(s);
					}
				},new SideEffect<Integer>()
				{
					@Override
                    public void run(final Integer integer)
					{
						monitor[0].setProgress(integer);
					}
				});

				if (results.getPercentConnected()!=100)
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						@Override
                        public void run()
						{
							JOptionPane.showMessageDialog(Global.global.get().frame, "The network must be 100% connected before it can be broken", "Error", JOptionPane.ERROR_MESSAGE);
						}
					});
					Global.getNetworkContext().networkView.ignorePaints.set(false);
					monitor[0].close();
					return;
				}

				final String savedNetwork=NetworkUtility.saveToString(Global.getNetworkContext().network);

				final Network network=Global.getNetworkContext().network;
				network.log=Collections.add(network.log,"Breaking network");

				monitor[0].close();

				monitor[0]=new ProgressMonitor(Global.global.get().frame, "Breaking network                  ", "Breaking network                    ", 0, 100);

				monitor[0].setMillisToPopup(0);
				monitor[0].setMillisToDecideToPopup(0);
				monitor[0].setProgress(1);

				monitor[0].setNote("Trying to break the network");
				monitor[0].setProgress(0);

				int totalFailures=0;
				for (int a=0;a<numberOfFaultsVar && totalFailures<50;a++)
				{
					monitor[0].setNote("Breaking network (try "+(totalFailures+1)+"/50)");
					final SideEffect<String> noLog=doNothing();
					final SideEffect<Integer> noProgress=doNothing();
					final int connectivityBefore=ConnectivityTest.testConnectivity(Global.getNetworkContext().network, noLog, noProgress).getPercentConnected();

					oneRandomBreakage(getNetworkContext().network);

					final int connectivityAfter=ConnectivityTest.testConnectivity(getNetworkContext().network,noLog,noProgress).getPercentConnected();

					if (connectivityAfter>=connectivityBefore)
					{
						a=-1;
						totalFailures++;

						NetworkUtility.loadFromString(getNetworkContext().network, savedNetwork);
					}

					monitor[0].setProgress(100*a/numberOfFaultsVar);
				}

				if (totalFailures>49)
					JOptionPane.showMessageDialog(Global.global.get().frame, "Failed to break network - try a larger network or less faults", "Error", JOptionPane.ERROR_MESSAGE);

				getNetworkContext().network.log.clear();
				monitor[0].close();
				UserMessages.message("Network broken, with "+numberOfFaultsVar+" faults");

				getNetworkContext().userPermissions=FREEFORM_WITH_BREAKS;

				getNetworkContext().networkView.ignorePaints.set(false);
			}
		}.execute();
	}

	private static void oneRandomBreakage(final Network network)
	{
		randomOneOf(turnAHubOff(network),turnPacketForwardingOffOnARouter(network), changeCableType(network),disconnectCable(network), swapIPsOnARouter(network), changeIP(network), changeNetMask(network), changeRoute(network), deleteRoute(network));
	}

	private static Runnable deleteRoute(final Network network)
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				MaybeUtility.run(randomOneOf(getAllComputers(network)),new SideEffect<Computer>()
				{
					@Override
                    public void run(final Computer computer)
					{
						final RoutingTable table=computer.routingTable;
						MaybeUtility.run(randomOneOf(table.routes()),new SideEffect<Route>()
						{
							@Override
                            public void run(final Route route)
							{
								table.remove(route);
							}
						});

					}
				});
			}
		};
	}

	private static Runnable changeRoute(final Network context)
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				MaybeUtility.run(randomOneOf(getAllComputers(context)),new SideEffect<Computer>()
				{
					@Override
                    public void run(final Computer computer)
					{
						final RoutingTable table=computer.routingTable;

						MaybeUtility.run(randomOneOf(table.routes()),new SideEffect<Route>()
						{
							@Override
                            public void run(final Route route)
							{
								computer.routingTable.replace(route,breakRoute(route));
							}
						});
					}
				});
			}
		};
	}

	private static Route breakRoute(final Route route)
	{
		if (Math.random()<0.5)
			return new Route(route.block, randomIP());

		if (Math.random()<0.5)
			return new Route(new NetBlock(route.block.networkNumber, NetMaskUtility.randomNetMask()), route.gateway);

		return new Route(new NetBlock(randomIP(), route.block.netMask), route.gateway);
	}

	private static Runnable changeNetMask(final Network network)
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				MaybeUtility.run(randomOneOf(getAllCardsWithDrivers(network)),new SideEffect<CardDrivers>()
				{
					@Override
                    public void run(final CardDrivers card)
					{
						card.netMask.set(NetMaskUtility.randomNetMask());
					}
				});
			}
		};
	}

	private static Runnable changeIP(final Network network)
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				MaybeUtility.run(randomOneOf(getAllCardsWithDrivers(network)),new SideEffect<CardDrivers>()
				{
					@Override
                    public void run(final CardDrivers card)
					{
						card.ipAddress.set(randomIP());
					}
				});
			}
		};
	}

	private static Runnable swapIPsOnARouter(final Network network)
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				MaybeUtility.run(randomOneOf(getAllComputers(network, ConformanceTestsUtility.isARouter())),new SideEffect<Computer>()
				{
					@Override
                    public void run(final Computer computer)
					{
						final List<CardDrivers> cards=ComputerUtility.cardsWithDrivers(computer);

						final Pair<CardDrivers, CardDrivers> pair=randomTwoOf(cards);

						final IPAddress tmp=pair.first().ipAddress.get();
						pair.first().ipAddress.set(pair.second().ipAddress.get());
						pair.second().ipAddress.set(tmp);
					}
				});
			}
		};
	}

	private static <T> Pair<T,T> randomTwoOf(final List<T> list)
	{
		final int first=(int)(Math.random()*(double)list.size());

		int second=(int)(Math.random()*(double)(list.size()-1));

		if (second>=first)
			second++;

		return pair(list.get(first),list.get(second));
	}

	private static Runnable disconnectCable(final Network network)
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				final Function<Cable,Boolean> connected=new Function<Cable, Boolean>()
				{
					@Override
                    @NotNull
					public Boolean run(@NotNull final Cable cable)
					{
						return cable.canTransferPackets(network);
					}
				};

				MaybeUtility.run(randomOneOf(NetworkUtility.getAllCables(network,connected)),new SideEffect<Cable>()
				{
					@Override
                    public void run(final Cable cable)
					{
						final int end=(int)(Math.random()*2.0);
						setPosition(network,cable, mapWith(end, getPosition(network,cable,end)));
					}
				});
			}
		};
	}

	private static Runnable changeCableType(final Network network)
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				MaybeUtility.run(randomOneOf(NetworkUtility.getAllCables(network)),new SideEffect<Cable>()
				{
					@Override
                    public void run(final Cable cable)
					{
						cable.setCableType(cable.getCableType().another());
					}
				});
			}
		};
	}

	private static Runnable turnPacketForwardingOffOnARouter(final Network network)
	{
		return new Runnable()
		{
			@Override
            public void run()
			{

				MaybeUtility.run(randomOneOf(getAllComputers(network)),new SideEffect<Computer>()
				{
					@Override
                    public void run(final Computer computer)
					{
						if (ConformanceTestsUtility.isARouter().run(computer))
							computer.ipForwardingEnabled=false;
					}
				});
			}
		};
	}

	private static Runnable turnAHubOff(final Network network)
	{
		return new Runnable()
		{
			@Override
            public void run()
			{
				MaybeUtility.run(randomOneOf(getAllHubs(network)),new SideEffect<Hub>()
				{
					@Override
                    public void run(final Hub hub)
					{
						if (hub.isPowerOn())
							hub.setPower(false);
					}
				});
			}
		};
	}

	private static <T> Maybe<T> randomOneOf(final List<T> list)
	{
		if (list.isEmpty())
			return nothing();

		return just(list.get((int)(Math.random()*list.size())));
	}

	private static <T> Maybe<T> randomOneOf(final Collection<T> collection)
	{
		return randomOneOf(arrayList(collection));
	}

	private static void randomOneOf(final Runnable... runnables)
	{
		runnables[((int)(Math.random()*runnables.length))].run();
	}
}