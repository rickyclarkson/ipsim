package ipsim.gui.components;

import anylayout.AnyLayout;
import anylayout.extras.PercentConstraints;
import anylayout.extras.PercentConstraintsUtility;
import fj.P;
import fj.P2;
import ipsim.gui.UserMessages;
import ipsim.gui.event.CommandUtility;
import ipsim.network.InvalidNetMaskException;
import ipsim.network.Network;
import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.NetBlock;
import ipsim.network.ethernet.NetBlockUtility;
import ipsim.swing.CustomJOptionPane;
import ipsim.swing.IPAddressTextField;
import ipsim.swing.SubnetMaskTextField;
import ipsim.util.Collections;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import static anylayout.extras.ConstraintUtility.typicalDefaultConstraint;
import static ipsim.Caster.equalT;
import static ipsim.Global.getNetworkContext;
import static ipsim.Global.global;
import static ipsim.lang.Runnables.throwRuntimeException;
import static ipsim.network.ethernet.NetMaskUtility.createNetMaskFromPrefixLength;
import static ipsim.network.ethernet.NetMaskUtility.getPrefixLength;
import static ipsim.swing.NetBlockTextFieldUtility.createNetBlockTextField;
import static ipsim.util.Collections.arrayList;
import static ipsim.util.Printf.sprintf;

public final class ScrapbookDialogUtility
{
	public static JPanel createScrapbook()
	{
		final JPanel panel=new JPanel();

		final PercentConstraints constraints=PercentConstraintsUtility.newInstance(panel);
		AnyLayout.useAnyLayout(panel,0.5f,0.5f,constraints.getSizeCalculator(),typicalDefaultConstraint(throwRuntimeException));

		final NetBlockTextField networkNumberField=createNetBlockTextField();

		JLabel label=new JLabel("Network Number");
		constraints.add(label,2,2,28,5,false,false);
		constraints.add(networkNumberField.getComponent(),2,8,28,10,false,false);

		final SubnetMaskTextField netMaskField=new SubnetMaskTextField();

		label=new JLabel("Netmask");
		constraints.add(label,2,15,28,5,false,false);
		constraints.add(netMaskField,2,20,28,20,false,false);

		final List<ScrapbookElement> elements=arrayList();

		final JButton clearNumbers=new JButton("Clear Numbers");
		constraints.add(clearNumbers,2,80,25,10,false,false);

		clearNumbers.addActionListener(new ActionListener()
		{
			@Override
            public void actionPerformed(final ActionEvent event)
			{
				if (JOptionPane.YES_OPTION==CustomJOptionPane.showYesNoCancelDialog(global.get().frame, "Do you really want to clear all the numbers?", "Clear Numbers?"))
					return;

				networkNumberField.getComponent().setText("");
				netMaskField.setText("");

				for (final ScrapbookElement element: elements)
				{
					final JTextField textField=element.getSubnetTextField().getComponent();

					textField.setText("");
					element.getNetMaskTextField().setText("");

					for (final IPAddressTextField field: element.getIPAddressTextFields())
						field.textField.setText("");
				}
			}
		});

		final JButton checkNumbers=new JButton("Check Numbers");

		constraints.add(checkNumbers,2,70,25,10,false,false);

		checkNumbers.addActionListener(new ActionListener()
		{
			public P2<String,String> checkNumbers()
			{
				final StringBuilder description=new StringBuilder();
				int checked=0;
				int errors=0;

				final P2<String,String> errorMessage= P.p("one or more fields have invalid data.", "One or more fields have invalid data.");

				if (!(0==networkNumberField.getComponent().getText().length()))
				{
					if (!networkNumberField.isValid())
						return errorMessage;

					final NetBlock block=networkNumberField.netBlock()._1();

					if (!(0==netMaskField.getText().length()))
						if (netMaskField.isValidText())
						{
							if (!equalT(block.netMask,netMaskField.getNetMask()))
								try
								{
									description.append(sprintf("The netmask %s does not correspond with the network %s\n", NetMask.asString(netMaskField.getNetMask().rawValue),NetBlockUtility.asStringContainingSlash(block)));
								}
								catch (final InvalidNetMaskException exception)
								{
									description.append("One of the subnet masks is invalid\n");
								}
						}
						else
							return errorMessage;

					checked++;

					for (final ScrapbookElement element: elements)
					{
						final NetBlockTextField subnetTextField=element.getSubnetTextField();

						if (0==subnetTextField.getComponent().getText().length())
							continue;

						if (!subnetTextField.isValid())
							return errorMessage;

						final NetBlock subnet=subnetTextField.netBlock()._1();

						final IPAddress netNum=new IPAddress(subnet.networkNumber.rawValue&subnet.netMask.rawValue);

						try
						{
							checked++;
							if (!block.networkContains(netNum)||getPrefixLength(block.netMask)>=getPrefixLength(subnet.netMask))
							{
								errors++;
								description.append("The subnet ");
								description.append(subnetTextField.getComponent().getText());

								description.append(" is not a subnet of the network ");

								description.append(networkNumberField.getComponent().getText());
								description.append('\n');
							}
						}
						catch (final InvalidNetMaskException exception)
						{
							description.append("One of the subnet masks is invalid\n");
						}

						description.append(checkScrapbookIPs(block,element.getIPAddressTextFields()));
					}
				}

				for (final ScrapbookElement element: elements)
				{
					final NetBlockTextField subnetTextField=element.getSubnetTextField();

					if (0==subnetTextField.getComponent().getText().length())
						continue;

					if (!subnetTextField.isValid())
						return errorMessage;

					final NetBlock subnet=subnetTextField.netBlock()._1();

					description.append(checkScrapbookIPs(subnet,element.getIPAddressTextFields()));

					checked++;

					if (!(0==element.getNetMaskTextField().getText().length()))
					{
						if (!element.getNetMaskTextField().isValidText())
							return errorMessage;

						if (equalT(element.getNetMaskTextField().getNetMask(), subnet.netMask))
						{
							errors++;
							try
							{
								description.append(sprintf("The netmask %s does not correspond with the subnet %s\n", NetMask.asString(element.getNetMaskTextField().getNetMask().rawValue),NetBlockUtility.asStringContainingSlash(subnet)));
							}
							catch (final InvalidNetMaskException exception)
							{
								description.append("One of the subnet masks is invalid\n");
							}
						}
					}
				}

				for (final ScrapbookElement element1: elements)
				{
					if (0==element1.getSubnetTextField().getComponent().getText().length())
						continue;

					final NetBlock netblock1=element1.getSubnetTextField().netBlock()._1();

					for (final ScrapbookElement element2: elements)
					{
						if (0==element2.getSubnetTextField().getComponent().getText().length())
							continue;

						final NetBlock netblock2=element2.getSubnetTextField().netBlock()._1();

						if (equalT(element1,element2))
							continue;

						checked++;

						if (element1.getSubnetTextField().netBlock()._1().networkContains(element2.getSubnetTextField().netBlock()._1().networkNumber))
						{
							errors++;

							try
							{
								description.append(sprintf("The network %s contains the network %s, therefore they are not disjoint and cannot be used as separate networks.\n",NetBlockUtility.asStringContainingSlash(netblock1),NetBlockUtility.asStringContainingSlash(netblock2)));
							}
							catch (final InvalidNetMaskException exception)
							{
								description.append("One of the subnet masks is invalid\n");
							}
						}
					}
				}

				if (!(0==netMaskField.getText().length()) &&!netMaskField.isValidText())
					return errorMessage;

				if (0==description.length())
					description.append("All the numbers are ok");

				return P.p("checked "+checked+" numbers, "+errors+" errors found",description.toString());
			}

			public String checkScrapbookIPs(final NetBlock netBlock,final Iterable<IPAddressTextField> fields)
			{
				final StringBuilder answer=new StringBuilder();

				for (final IPAddressTextField ipAddressTextField: fields)
				{
					if (0==ipAddressTextField.textField.getText().length())
						continue;

					final IPAddress ipAddress=ipAddressTextField.getIPAddress();

					final String withSlash;
					try
					{
						withSlash=NetBlockUtility.asStringContainingSlash(netBlock);
					}
					catch (final InvalidNetMaskException exception)
					{
						answer.append("One of the subnet masks is invalid\n");
						continue;
					}

					if (!netBlock.networkContains(ipAddress))
						answer.append(sprintf("The IP address %s is not in the network %s\n",ipAddress.asString(),withSlash));

					final int rawIP=ipAddress.rawValue;

					if (rawIP==netBlock.networkNumber.rawValue)
						answer.append(sprintf("The IP address %s has all 0s as the host number, and cannot be used as an IP address on the network %s\n",ipAddress.asString(),withSlash));

					final int allOnes=createNetMaskFromPrefixLength(32).rawValue;

					if (rawIP==(rawIP|netBlock.netMask.rawValue^allOnes))
						answer.append(sprintf("The IP address %s has all 1s as the host number, and is the broadcast address for the network %s\n",ipAddress.asString(),withSlash));
				}

				return answer.toString();
			}

			@Override
            public void actionPerformed(final ActionEvent event)
			{
				final P2<String,String> results=checkNumbers();
				final Network network=getNetworkContext().network;
				network.log=Collections.add(network.log,CommandUtility.scrapbookChecked(results._1()));
				UserMessages.message(results._2());
			}
		});

		for (int a=0;a<5;a++)
		{
			final ScrapbookElement element=ScrapbookElementUtility.createElement();
			elements.add(element);

			element.getPanel().setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			element.getPanel().setOpaque(false);

			constraints.add(element.getPanel(),30,20*a,70,20,true,true);
		}

		final List<Component> list=arrayList();
		list.add(networkNumberField.getComponent());
		list.add(netMaskField);
		list.add(checkNumbers);
		list.add(clearNumbers);

		for (final ScrapbookElement element: elements)
		{
			// element.getPanel().setFocusCycleRoot(false);
			list.add(element.getSubnetTextField().getComponent());
			list.add(element.getNetMaskTextField());

			for (final IPAddressTextField field: element.getIPAddressTextFields())
				list.add(field.textField);
		}

		final FocusTraversalPolicy policy=new FocusTraversalPolicy()
		{
			@Override
			public Component getComponentAfter(final Container aContainer, final Component aComponent)
			{
				final int index=list.indexOf(aComponent);

				return list.get((index+1)%list.size());
			}

			@Override
			public Component getComponentBefore(final Container aContainer, final Component aComponent)
			{
				int index=list.indexOf(aComponent);

				index=0==index ? list.size()-1 : index;

				return list.get(index);
			}

			@Override
			public Component getFirstComponent(final Container aContainer)
			{
				return networkNumberField.getComponent();
			}

			@Override
			public Component getLastComponent(final Container aContainer)
			{
				return list.get(list.size()-1);
			}

			@Override
			public Component getDefaultComponent(final Container aContainer)
			{
				return networkNumberField.getComponent();
			}
		};

		panel.setFocusTraversalPolicy(policy);

		for (final ScrapbookElement element: elements)
		{
			final JPanel elementPanel=element.getPanel();
			elementPanel.setFocusTraversalPolicy(policy);
			elementPanel.setFocusTraversalPolicyProvider(true);
		}

		panel.setFocusTraversalPolicyProvider(true);

		return panel;
	}
}