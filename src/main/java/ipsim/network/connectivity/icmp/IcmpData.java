package ipsim.network.connectivity.icmp;

import ipsim.network.connectivity.IPDataVisitor2;
import ipsim.network.connectivity.ip.IPDataVisitor;

public interface IcmpData {
    void accept(IPDataVisitor visitor);

    <T> T accept(IPDataVisitor2<T> visitor);
}