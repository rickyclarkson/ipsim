package ipsim.network.connectivity.computer;

import ipsim.lang.Stringables;
import ipsim.network.Network;
import ipsim.network.connectivity.ethernet.MacAddress;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.util.Collections;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class ArpTable {
    private final Map<IPAddress, ArpEntry> map = Collections.hashMap();

    public void put(final IPAddress ipAddress, final MacAddress macAddress, final Network network) {
        map.put(ipAddress, new ArpEntry(macAddress, network.arpCacheTimeout));
    }

    public
    @Nullable
    MacAddress getMacAddress(final IPAddress gatewayIP) {
        final ArpEntry result = map.get(gatewayIP);

        if (result == null || result.dead())
            return null;

        return result.macAddress;
    }

    public String asString() {
        return Collections.asString(map.entrySet(), Stringables.<IPAddress>asString(), ArpEntry.asString, ": ", "\n");
    }

    public void clear() {
        map.clear();
    }

    public void putIncomplete(final IPAddress sourceIPAddress, final Network network) {
        map.put(sourceIPAddress, new ArpEntry(null, network.arpCacheTimeout));
    }

    public boolean hasEntryFor(final IPAddress ipAddress) {
        return map.containsKey(ipAddress) && !map.get(ipAddress).dead();
    }
}