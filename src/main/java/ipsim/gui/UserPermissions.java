package ipsim.gui;

import fpeas.pair.Pair;
import static fpeas.pair.PairUtility.pair;

public enum UserPermissions
{
	FREEFORM
			{
				@Override
				public boolean allowMultipleNetworks()
				{
					return true;
				}

				@Override
				public boolean allowClearingLog()
				{
					return true;
				}

				@Override
				public boolean allowDownloadingNewConfig()
				{
					return true;
				}

				@Override
				public Pair<Boolean, String> allowFullTests()
				{
					return pair(true, "");
				}

				@Override
				public boolean allowBreakingNetwork()
				{
					return true;
				}
			},
	FREEFORM_WITH_BREAKS
			{

				@Override
				public boolean allowClearingLog()
				{
					return true;
				}

				@Override
				public boolean allowDownloadingNewConfig()
				{
					return true;
				}

				@Override
				public Pair<Boolean, String> allowFullTests()
				{
					return pair(false, "Not allowed when 'Break Network' has been used");
				}

				@Override
				public boolean allowBreakingNetwork()
				{
					return true;
				}

				@Override
				public boolean allowMultipleNetworks()
			{
				return true;
			}
			},
	PRACTICE_TEST
			{
				@Override
				public boolean allowMultipleNetworks()
			{
				return true;
			}

				@Override
				public boolean allowClearingLog()
				{
					return true;
				}

				@Override
				public boolean allowDownloadingNewConfig()
				{
					return true;
				}

				@Override
				public Pair<Boolean, String> allowFullTests()
				{
					return pair(true, "");
				}

				@Override
				public boolean allowBreakingNetwork()
				{
					return false;
				}
			},
	PRACTICE_TEST_SIMULATING_ACTUAL_TEST
			{
				@Override
				public boolean allowMultipleNetworks()
			{
				return false;
			}

				@Override
				public boolean allowClearingLog()
				{
					return false;
				}

				@Override
				public boolean allowDownloadingNewConfig()
				{
					return false;
				}

				@Override
				public Pair<Boolean, String> allowFullTests()
				{
					return pair(false, "Not allowed during a practice test (duplicating test conditions)");
				}

				@Override
				public boolean allowBreakingNetwork()
				{
					return false;
				}
			},
	ACTUAL_TEST
			{
				@Override
				public boolean allowMultipleNetworks()
			{
				return false;
			}

				@Override
				public boolean allowClearingLog()
				{
					return false;
				}

				@Override
				public boolean allowDownloadingNewConfig()
				{
					return false;
				}

				@Override
				public Pair<Boolean, String> allowFullTests()
				{
					return pair(false, "Not allowed during a test");
				}

				@Override
				public boolean allowBreakingNetwork()
				{
					return false;
				}
			};

	public abstract boolean allowClearingLog();

	public abstract boolean allowDownloadingNewConfig();

	public abstract Pair<Boolean, String> allowFullTests();

	public abstract boolean allowBreakingNetwork();

	public abstract boolean allowMultipleNetworks();
}