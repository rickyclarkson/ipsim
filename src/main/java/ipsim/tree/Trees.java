package ipsim.tree;

import ipsim.connectivity.hub.incoming.PacketSourceUtility;
import ipsim.network.Network;
import ipsim.network.connectivity.PacketSource;
import ipsim.network.connectivity.PacketSourceAndIndex;
import ipsim.util.Collections;
import java.util.Collection;
import java.util.List;

public final class Trees {
    private Trees() {
    }

    public static <T> Iterable<T> getDepthFirstIterable(final Iterable<TreeNode<T>> roots) {
        return new DepthFirstIterable<T>(roots);
    }

    public static Iterable<TreeNode<PacketSource>> nodify(final Network network, final Iterable<? extends PacketSource> components) {
        final List<TreeNode<PacketSource>> result = Collections.arrayList();

        final class NetworkNode implements TreeNode<PacketSource> {
            private final PacketSource component;

            NetworkNode(final PacketSource component) {
                this.component = component;
            }

            @Override
            public PacketSource getValue() {
                return component;
            }

            @Override
            public String asString() {
                return "NetworkNode[" + PacketSourceUtility.asString(network, component) + "]";
            }

            @Override
            public Iterable<TreeNode<PacketSource>> getChildNodes() {
                final Collection<PacketSourceAndIndex> children = component.children();
                final Iterable<PacketSource> packetSources = Collections.map(children, PacketSourceAndIndex.getPacketSource);
                return nodify(network, packetSources);
            }
        }

        for (final PacketSource component : components)
            result.add(new NetworkNode(component));

        return result;
    }
}
