package ipsim.connectivity.computer.arp.outgoing;

import com.rickyclarkson.testsuite.UnitTest;
import ipsim.Caster;
import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.lang.CheckedIllegalStateException;
import ipsim.network.Network;
import ipsim.network.connectivity.OutgoingPacketListener;
import ipsim.network.connectivity.Packet;
import ipsim.network.connectivity.PacketQueue;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketUtility2;
import ipsim.network.connectivity.arp.ArpPacket;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.computer.ComputerFactory;
import ipsim.network.connectivity.ethernet.EthernetPacket;
import ipsim.network.connectivity.ethernet.MacAddress;
import ipsim.network.connectivity.ip.IPAddress;

import static ipsim.network.connectivity.PacketUtility.asEthernetPacket;

public class ComputerArpOutgoingTest implements UnitTest
{
	@Override
    public boolean invoke()
	{
		final Network network=new Network();

		final Computer computer=ComputerFactory.newComputer(network, 0,0);

		computer.computerID=network.generateComputerID();

		final PacketQueue queue=network.packetQueue;

		final StringBuilder answer=new StringBuilder();

		computer.getOutgoingPacketListeners().add(new OutgoingPacketListener()
		{
			@Override
            public void packetOutgoing(final Packet packet,final PacketSource source)
			{
				if (PacketUtility2.isEthernetPacket(packet)&& PacketSourceUtility.isComputer(source))
				{
					final EthernetPacket ethPacket;
					try
					{
						ethPacket=asEthernetPacket(packet);
					}
					catch (final CheckedIllegalStateException exception)
					{
						throw new RuntimeException(exception);
					}

					if (PacketUtility2.isArpPacket(ethPacket.data) &&null!=PacketUtility2.asArpPacket(ethPacket.data).toNull())
						answer.append("Pass");
				}
			}

			@Override
            public boolean canHandle(final Packet packet,final PacketSource source)
			{
				return true;
			}
		});

		final ArpPacket arpPacket=new ArpPacket(new IPAddress(0), new MacAddress(5), new IPAddress(0), new MacAddress(10), new Object());

		queue.enqueueOutgoingPacket(arpPacket,computer);

		queue.processAll();

		return Caster.equalT(answer.toString(),"Pass");
	}

	public String toString()
	{
		return "ComputerArpOutgoingTest";
	}
}