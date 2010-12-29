package ipsim.network.connectivity.icmp.ping;

import ipsim.network.connectivity.icmp.IcmpData;
import ipsim.network.connectivity.ip.IPDataVisitor;
import ipsim.network.connectivity.IPDataVisitor2;

public enum PingData implements IcmpData
{
	REQUEST
	{
		@Override
        public <T> T accept(final IPDataVisitor2<T> visitor)
		{
			return visitor.visitRequest();
		}
	},
	REPLY
	{
		@Override
        public <T> T accept(final IPDataVisitor2<T> visitor)
		{
			return visitor.visitReply();
		}
	};

	@Override
    public void accept(final IPDataVisitor visitor)
	{
		visitor.visit(this);
	}
}