package ipsim.network.connectivity.cable;

import ipsim.lang.Stringable;
import ipsim.network.connectivity.PacketSource;

import static ipsim.connectivity.hub.incoming.PacketSourceUtility.isCard;
import static ipsim.connectivity.hub.incoming.PacketSourceUtility.isHub;
import static ipsim.lang.Randoms.randomOneOf;

public enum CableType implements Stringable
{
	CROSSOVER
	{
		@Override
        public String asString()
		{
			return "Crossover";
		}

		@Override
		public String toString()
		{
			return asString();
		}

		@Override
		public CableType another()
		{
			return randomOneOf(BROKEN, STRAIGHT_THROUGH);
		}

		@Override
		public boolean canTransferPackets(final PacketSource oneEnd, final PacketSource other)
		{
			return isHub.invoke(oneEnd) && isHub.invoke(other) || isCard(oneEnd) && isCard(other);
		}
	},BROKEN
	{
		@Override
		public boolean canTransferPackets(final PacketSource oneEnd, final PacketSource other)
		{
			return false;
		}

		@Override
        public String asString()
		{
			return "Broken";
		}

		@Override
		public String toString()
		{
			return asString();
		}

		@Override
		public CableType another()
		{
			return randomOneOf(CROSSOVER, STRAIGHT_THROUGH);
		}
	},STRAIGHT_THROUGH
	{
		@Override
        public String asString()
		{
			return "Straight through";
		}

		@Override
		public String toString()
		{
			return asString();
		}

		@Override
		public CableType another()
		{
			return randomOneOf(CROSSOVER, BROKEN);
		}

		@Override
		public boolean canTransferPackets(final PacketSource oneEnd, final PacketSource other)
		{
			return isCard(oneEnd) && isHub.invoke(other) || isCard(other) && isHub.invoke(oneEnd);
		}
	};

	public abstract CableType another();
    //public abstract String asString();
    
	public abstract boolean canTransferPackets(final PacketSource oneEnd, final PacketSource other);
}