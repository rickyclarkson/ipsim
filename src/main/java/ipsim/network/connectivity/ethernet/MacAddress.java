package ipsim.network.connectivity.ethernet;

import ipsim.Caster;

public final class MacAddress
{
	public final int rawValue;

	public MacAddress(final int rawValue)
	{
		this.rawValue=rawValue;
	}

	@Override
	public String toString()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(final Object object)
	{
		if (!Caster.isMacAddress(object))
			return false;

		return rawValue==Caster.asMacAddress(object).rawValue;
	}

	@Override
	public int hashCode()
	{
		return 67832+19*rawValue;
	}
}