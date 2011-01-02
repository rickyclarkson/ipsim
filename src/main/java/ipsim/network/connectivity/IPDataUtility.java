package ipsim.network.connectivity;

import ipsim.network.connectivity.icmp.IcmpData;

public class IPDataUtility {
    public static String asString(final IcmpData data) {
        return data.accept(new IPDataVisitor2<String>() {
            @Override
            public String visitRequest() {
                return "Request";
            }

            @Override
            public String visitReply() {
                return "Reply";
            }

            @Override
            public String visitNetUnreachable() {
                return "Net Unreachable";
            }

            @Override
            public String visitHostUnreachable() {
                return "Host Unreachable";
            }

            @Override
            public String visitTimeToLiveExceeded() {
                return "Time to Live Exceeded";
            }
        });
    }
}