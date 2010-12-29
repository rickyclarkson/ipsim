package ipsim.network.connectivity.ip;

import org.jetbrains.annotations.NotNull;

public class SourceIPAddress
{
	private final IPAddress address;

	public SourceIPAddress(@NotNull final IPAddress address)
	{
		this.address=address;
	}

	public IPAddress getIPAddress()
	{
		return address;
	}

	public String asString()
	{
		return address.asString();
	}

	@Override
	public String toString()
	{
		return asString();
	}
}