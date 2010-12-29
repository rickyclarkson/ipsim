package ipsim.network.connectivity.computer;

import fpeas.function.Function;
import ipsim.network.connectivity.ethernet.MacAddress;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.System.currentTimeMillis;

public final class ArpEntry
{
	@Nullable
	public final MacAddress macAddress;
	public final long timeToDie;
	public static final Function<ArpEntry, String> asString=new Function<ArpEntry, String>()
	{
		@Override
        @NotNull
		public String run(@NotNull final ArpEntry arpEntry)
		{
			return arpEntry.toString();
		}
	};

	@Override
	public String toString()
	{
		final String messagePart=dead() ? "expired" : "expires in "+timeToLive()+" seconds";

		return macAddress==null ? "incomplete ARP entry - "+messagePart : new Function<MacAddress, String>()
		{
			@Override
            @NotNull
			public String run(@NotNull final MacAddress macAddress)
			{
				final StringBuffer buffer=new StringBuffer(Integer.toHexString(macAddress.rawValue));
				while (buffer.length()<12)
					buffer.insert(0, "0");

				for (int a=2;a<buffer.length();a+=3)
					buffer.insert(a, "-");

				return buffer+"; "+messagePart;
			}
		}.run(macAddress);
	}

	public ArpEntry(@Nullable final MacAddress macAddress, final int timeToLive)
	{
		this.macAddress=macAddress;
		timeToDie=currentTimeMillis()+1000*timeToLive;
	}

	public boolean dead()
	{
		return System.currentTimeMillis()>timeToDie;
	}

	public long timeToLive()
	{
		return (timeToDie-currentTimeMillis())/1000;
	}
}