package ipsim.network.connectivity.icmp.ttl;

import ipsim.network.connectivity.icmp.IcmpData;
import ipsim.network.connectivity.ip.IPDataVisitor;
import ipsim.network.connectivity.IPDataVisitor2;

public enum TimeExceededData implements IcmpData
{
	TIME_TO_LIVE_EXCEEDED
	{
		@Override
        public <T> T accept(final IPDataVisitor2<T> visitor)
		{
			return visitor.visitTimeToLiveExceeded();
		}
	};

	@Override
    public void accept(final IPDataVisitor visitor)
	{
		visitor.visit(this);
	}
}