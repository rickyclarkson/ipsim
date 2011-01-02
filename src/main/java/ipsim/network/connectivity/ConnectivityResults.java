package ipsim.network.connectivity;

import ipsim.lang.Stringable;
import java.util.Collection;

public interface ConnectivityResults extends Stringable {
    int getPercentConnected();

    Collection<String> getOutputs();
}