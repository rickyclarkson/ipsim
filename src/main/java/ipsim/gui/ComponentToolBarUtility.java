package ipsim.gui;

import anylayout.AnyLayout;
import anylayout.Constraint;
import anylayout.LayoutContext;
import anylayout.SizeCalculator;
import static anylayout.extras.ConstraintBuilder.after;
import static anylayout.extras.ConstraintBuilder.buildConstraint;
import static anylayout.extras.ConstraintBuilder.fill;
import static anylayout.extras.SizeCalculatorUtility.getPreferredHeight;
import static anylayout.extras.SizeCalculatorUtility.getPreferredWidth;
import fpeas.function.Function;
import fpeas.function.FunctionUtility;
import static ipsim.gui.NetworkComponentIconMouseListenerUtility.createNetworkComponentIconMouseListener;
import ipsim.gui.components.ComputerHandler;
import ipsim.gui.components.EthernetCableIcon;
import ipsim.gui.components.EthernetCardHandler;
import ipsim.gui.components.HubHandler;
import ipsim.network.connectivity.card.Card;
import ipsim.network.connectivity.computer.Computer;
import ipsim.network.connectivity.hub.Hub;
import static ipsim.swing.DragNDropIconCreator.newInstance;
import static ipsim.util.Collections.max;
import static ipsim.util.Collections.sum;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;

public final class ComponentToolBarUtility
{
	public static Container newBar()
	{
		final JPanel panel=new JPanel();
		final JLabel label=new JLabel("<html><body><h3>Components:</h3></body></html>");
		final JToggleButton cable=EthernetCableIcon.newButton();

		final Container computer=newInstance(ComputerHandler.icon,"Computer");
		final Container card=newInstance(EthernetCardHandler.icon,"Ethernet Card");
		final Container hub=newInstance(HubHandler.icon,"Hub");

		AnyLayout.useAnyLayout(panel,0.5f,0.5f,new SizeCalculator()
		{
			@Override
            public int getHeight()
			{
				return sum(getPreferredHeight(),label,computer,cable,card,hub);
			}

			@Override
            public int getWidth()
			{
				return max(getPreferredWidth(),label,computer,cable,card,hub);
			}
		},new Function<Component,Constraint>()
		{
			@Override
            @NotNull
			public Constraint run(@NotNull final Component component)
			{
				throw new IllegalStateException();
			}
		});

		final Function<LayoutContext,Integer> maxPreferredHeight=new Function<LayoutContext,Integer>()
		{
			@Override
            @NotNull
			public Integer run(@NotNull final LayoutContext layoutContext)
			{
				final Component[] components={label,computer,cable,card,hub};

				return Math.min(layoutContext.getParentSize()/components.length,max(getPreferredHeight(),components));
			}
		};

		final Function<LayoutContext,Integer> zero=FunctionUtility.constant(0);

		panel.add(label,buildConstraint().setLeft(zero).setTop(zero).setWidth(fill()).setHeight(maxPreferredHeight));
		panel.add(cable,buildConstraint().setLeft(zero).setTop(after(label)).setWidth(fill()).setHeight(maxPreferredHeight));
		panel.add(computer,buildConstraint().setLeft(zero).setTop(after(cable)).setWidth(fill()).setHeight(maxPreferredHeight));
		panel.add(card,buildConstraint().setLeft(zero).setTop(after(computer)).setWidth(fill()).setHeight(maxPreferredHeight));
		panel.add(hub,buildConstraint().setLeft(zero).setTop(after(card)).setWidth(fill()).setHeight(maxPreferredHeight));

		final MouseInputListener computerListener=createNetworkComponentIconMouseListener(Computer.class);

		final MouseInputListener cardListener=createNetworkComponentIconMouseListener(Card.class);

		final MouseInputListener hubListener=createNetworkComponentIconMouseListener(Hub.class);

		computer.addMouseListener(computerListener);
		computer.addMouseMotionListener(computerListener);

		card.addMouseListener(cardListener);
		card.addMouseMotionListener(cardListener);

		hub.addMouseListener(hubListener);
		hub.addMouseMotionListener(hubListener);
		hub.setPreferredSize(card.getPreferredSize());

		return panel;
	}
}