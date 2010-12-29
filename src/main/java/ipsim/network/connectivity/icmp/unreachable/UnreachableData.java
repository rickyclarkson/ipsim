package ipsim.network.connectivity.icmp.unreachable;

import ipsim.network.connectivity.icmp.IcmpData;
import ipsim.network.connectivity.ip.IPDataVisitor;
import ipsim.network.connectivity.IPDataVisitor2;

public enum UnreachableData implements IcmpData
{
	NET_UNREACHABLE
	{
		@Override
        public <T> T accept(final IPDataVisitor2<T> visitor)
		{
			return visitor.visitNetUnreachable();
		}
	},
	HOST_UNREACHABLE
	{
		@Override
        public <T> T accept(final IPDataVisitor2<T> visitor)
		{
			return visitor.visitHostUnreachable();
		}
	};
	
	@Override
    public void accept(final IPDataVisitor visitor)
	{
		visitor.visit(this);
	}
}