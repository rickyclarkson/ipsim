package ipsim.network.connectivity;

import fj.Effect;
import ipsim.lang.Assertion;
import ipsim.util.Collections;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.jetbrains.annotations.NotNull;

public class PacketQueue
{
	public final Queue<Runnable> pendingRequests;

	public final List<Runnable> emptyQueueListeners;

	public PacketQueue()
	{
		pendingRequests=new LinkedList<Runnable>();
		emptyQueueListeners=new LinkedList<Runnable>();
	}

	public void enqueueOutgoingPacket(final Packet packet, final PacketSource source)
	{
		Assertion.assertNotNull(packet);
		final Listeners<OutgoingPacketListener> listeners=source.getOutgoingPacketListeners();
		listeners.visitAll(new Effect<OutgoingPacketListener>()
		{
			@Override
            public void e(final OutgoingPacketListener listener)
			{
				pendingRequests.add(new Runnable()
				{
					@Override
                    public void run()
					{
						if (listener.canHandle(packet, source))
							listener.packetOutgoing(packet, source);

						processed(this);

						handleIfEmpty();
					}
				});
			}
		});
	}

	public void enqueueIncomingPacket(@NotNull final Packet packet, @NotNull final PacketSource source, @NotNull final PacketSource destination)
	{
		destination.getIncomingPacketListeners().visitAll(new Effect<IncomingPacketListener>()
		{
			@Override
            public void e(final IncomingPacketListener listener)
			{
				pendingRequests.add(new Runnable()
				{
					@Override
                    public void run()
					{
						if (listener.canHandle(packet, source))
							listener.packetIncoming(packet, source, destination);

						pendingRequests.remove(this);

						handleIfEmpty();
					}
				});
			}
		});
	}

	void handleIfEmpty()
	{
		if (pendingRequests.isEmpty())
		{
			final Collection<Runnable> temp=Collections.hashSet();

			for (final Runnable runnable : emptyQueueListeners)
				temp.add(runnable);

			for (final Runnable runnable : temp)
			{
				emptyQueueListeners.remove(runnable);
				runnable.run();
			}
		}
	}

	public void addEmptyQueueListener(final Runnable runnable)
	{
		emptyQueueListeners.add(runnable);
	}

	public void processed(final Runnable runnable)
	{
		pendingRequests.remove(runnable);
	}

	public void processAll()
	{
		while (!pendingRequests.isEmpty())
		{
			final Runnable removed=pendingRequests.remove();
			removed.run();
		}

		while (!emptyQueueListeners.isEmpty())
			emptyQueueListeners.remove(0).run();
	}
}