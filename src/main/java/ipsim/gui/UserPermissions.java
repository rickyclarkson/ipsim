package ipsim.gui;

import fj.P;
import fj.P2;

public enum UserPermissions {
    FREEFORM {
        @Override
        public boolean allowMultipleNetworks() {
            return true;
        }

        @Override
        public boolean allowClearingLog() {
            return true;
        }

        @Override
        public boolean allowDownloadingNewConfig() {
            return true;
        }

        @Override
        public P2<Boolean, String> allowFullTests() {
            return P.p(true, "");
        }

        @Override
        public boolean allowBreakingNetwork() {
            return true;
        }
    },
    FREEFORM_WITH_BREAKS {
        @Override
        public boolean allowClearingLog() {
            return true;
        }

        @Override
        public boolean allowDownloadingNewConfig() {
            return true;
        }

        @Override
        public P2<Boolean, String> allowFullTests() {
            return P.p(false, "Not allowed when 'Break Network' has been used");
        }

        @Override
        public boolean allowBreakingNetwork() {
            return true;
        }

        @Override
        public boolean allowMultipleNetworks() {
            return true;
        }
    },
    PRACTICE_TEST {
        @Override
        public boolean allowMultipleNetworks() {
            return true;
        }

        @Override
        public boolean allowClearingLog() {
            return true;
        }

        @Override
        public boolean allowDownloadingNewConfig() {
            return true;
        }

        @Override
        public P2<Boolean, String> allowFullTests() {
            return P.p(true, "");
        }

        @Override
        public boolean allowBreakingNetwork() {
            return false;
        }
    },
    PRACTICE_TEST_SIMULATING_ACTUAL_TEST {
        @Override
        public boolean allowMultipleNetworks() {
            return false;
        }

        @Override
        public boolean allowClearingLog() {
            return false;
        }

        @Override
        public boolean allowDownloadingNewConfig() {
            return false;
        }

        @Override
        public P2<Boolean, String> allowFullTests() {
            return P.p(false, "Not allowed during a practice test (duplicating test conditions)");
        }

        @Override
        public boolean allowBreakingNetwork() {
            return false;
        }
    },
    ACTUAL_TEST {
        @Override
        public boolean allowMultipleNetworks() {
            return false;
        }

        @Override
        public boolean allowClearingLog() {
            return false;
        }

        @Override
        public boolean allowDownloadingNewConfig() {
            return false;
        }

        @Override
        public P2<Boolean, String> allowFullTests() {
            return P.p(false, "Not allowed during a test");
        }

        @Override
        public boolean allowBreakingNetwork() {
            return false;
        }
    };

    public abstract boolean allowClearingLog();

    public abstract boolean allowDownloadingNewConfig();

    public abstract P2<Boolean, String> allowFullTests();

    public abstract boolean allowBreakingNetwork();

    public abstract boolean allowMultipleNetworks();
}