package ipsim.network;

import fpeas.sideeffect.SideEffect;
import static ipsim.ExceptionHandler.impossible;
import ipsim.gui.components.PacketSourceVisitor2;
import ipsim.network.connectivity.cable.Cable;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.hub.Hub;
import org.jetbrains.annotations.NotNull;

public class PacketSourceVisitorUtility
{
	public static final PacketSourceVisitor2 impossible=new PacketSourceVisitor2()
	{
		@Override
        public void visit(@NotNull final Card card)
		{
			impossible();
		}

		@Override
        public void visit(@NotNull final Computer computer)
		{
			impossible();
		}

		@Override
        public void visit(@NotNull final Cable cable)
		{
			impossible();
		}

		@Override
        public void visit(@NotNull final Hub hub)
		{
			impossible();
		}
	};

	public static PacketSourceVisitor2 visitCard(final PacketSourceVisitor2 base, final SideEffect<Card> effect)
	{
		return new PacketSourceVisitor2()
		{
			@Override
            public void visit(@NotNull final Card card)
			{
				effect.run(card);
			}

			@Override
            public void visit(@NotNull final Computer computer)
			{
				base.visit(computer);
			}

			@Override
            public void visit(@NotNull final Cable cable)
			{
				base.visit(cable);
			}

			@Override
            public void visit(@NotNull final Hub hub)
			{
				base.visit(hub);
			}
		};
	}
}
