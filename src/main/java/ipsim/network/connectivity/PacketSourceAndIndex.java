package ipsim.network.connectivity;

import fpeas.function.Function;
import fpeas.predicate.Predicate;
import org.jetbrains.annotations.NotNull;

public class PacketSourceAndIndex
{
	public final PacketSource packetSource;
	public final int index;

	public static final Function<PacketSourceAndIndex, PacketSource> getPacketSource=new Function<PacketSourceAndIndex, PacketSource>()
	{
		@Override
        @NotNull
		public PacketSource run(@NotNull final PacketSourceAndIndex packetSourceAndIndex)
		{
			return packetSourceAndIndex.packetSource;
		}
	};

	public PacketSourceAndIndex(final PacketSource packetSource, final int index)
	{
		this.packetSource=packetSource;
		this.index=index;
	}

	public static Predicate<PacketSourceAndIndex> packetSourceIs(final PacketSource packetSource)
	{
		return new Predicate<PacketSourceAndIndex>()
		{
			@Override
            public boolean invoke(final PacketSourceAndIndex packetSourceAndIndex)
			{
				return packetSourceAndIndex.packetSource.equals(packetSource);
			}
		};
	}

	public static Predicate<PacketSourceAndIndex> indexIs(final int index)
	{
		return new Predicate<PacketSourceAndIndex>()
		{
			@Override
            public boolean invoke(final PacketSourceAndIndex packetSourceAndIndex)
			{
				return packetSourceAndIndex.index==index;
			}
		};
	}
}