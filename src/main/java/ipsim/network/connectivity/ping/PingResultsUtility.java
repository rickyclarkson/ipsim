package ipsim.network.connectivity.ping;

import ipsim.network.connectivity.ip.SourceIPAddress;

public final class PingResultsUtility
{
	public static PingResults timedOut(final SourceIPAddress reporter)
	{
		return new PingResults()
		{
			@Override
            public String asString()
			{
				return reporter.asString()+" reports Request timed out";
			}

			@Override
            public boolean pingReplyReceived()
			{
				return false;
			}

			@Override
            public SourceIPAddress getReplyingHost()
			{
				return reporter;
			}

			@Override
            public boolean ttlExpired()
			{
				return false;
			}

			@Override
            public boolean hostUnreachable()
			{
				return false;
			}

			@Override
            public boolean timedOut()
			{
				return true;
			}
		};
	}

	public static PingResults hostUnreachable(final SourceIPAddress reporter)
	{
		return new PingResults()
		{
			@Override
            public String asString()
			{
				return reporter.asString()+" reports Host unreachable";
			}

			@Override
            public boolean pingReplyReceived()
			{
				return false;
			}

			@Override
            public boolean hostUnreachable()
			{
				return true;
			}

			@Override
            public SourceIPAddress getReplyingHost()
			{
				return reporter;
			}

			@Override
            public boolean ttlExpired()
			{
				return false;
			}

			@Override
            public boolean timedOut()
			{
				return false;
			}
		};
	}

	public static PingResults ttlExpired(final SourceIPAddress reporter)
	{
		return new PingResults()
		{
			@Override
            public String asString()
			{
				return reporter.asString()+" reports TTL expired";
			}

			@Override
            public boolean pingReplyReceived()
			{
				return false;
			}

			@Override
            public boolean hostUnreachable()
			{
				return false;
			}

			@Override
            public SourceIPAddress getReplyingHost()
			{
				return reporter;
			}

			@Override
            public boolean ttlExpired()
			{
				return true;
			}

			@Override
            public boolean timedOut()
			{
				return false;
			}
		};
	}

	public static PingResults pingReplyReceived(final SourceIPAddress source)
	{
		return new PingResults()
		{
			@Override
            public String asString()
			{
				return "Reply received from "+source.asString();
			}

			@Override
            public boolean pingReplyReceived()
			{
				return true;
			}

			@Override
            public boolean hostUnreachable()
			{
				return false;
			}

			@Override
            public SourceIPAddress getReplyingHost()
			{
				return source;
			}

			@Override
            public boolean ttlExpired()
			{
				return false;
			}

			@Override
            public boolean timedOut()
			{
				return false;
			}
		};
	}

	public static PingResults netUnreachable(final SourceIPAddress gatewayIPAddress)
	{
		return new PingResults()
		{
			@Override
            public String asString()
			{
				return gatewayIPAddress.asString()+" reports Net Unreachable";
			}

			@Override
            public boolean pingReplyReceived()
			{
				return false;
			}

			@Override
            public SourceIPAddress getReplyingHost()
			{
				return gatewayIPAddress;
			}

			@Override
            public boolean ttlExpired()
			{
				return false;
			}

			@Override
            public boolean hostUnreachable()
			{
				return false;
			}

			@Override
            public boolean timedOut()
			{
				return false;
			}
		};
	}
}