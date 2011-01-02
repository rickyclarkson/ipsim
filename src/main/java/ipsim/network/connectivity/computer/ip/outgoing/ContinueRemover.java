package ipsim.network.connectivity.computer.ip.outgoing;

import ipsim.network.connectivity.IncomingPacketListener;
import ipsim.network.connectivity.computer.Computer;

final class ContinueRemover implements Runnable {
    private final IncomingPacketListener listener;

    private final Computer computer;

    ContinueRemover(final IncomingPacketListener listener, final Computer computer) {
        this.listener = listener;
        this.computer = computer;
    }

    @Override
    public void run() {
        if (computer.getIncomingPacketListeners().contains(listener))
            computer.getIncomingPacketListeners().remove(listener);
    }
}