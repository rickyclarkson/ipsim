package ipsim.gui.components;

import anylayout.AnyLayout;
import static anylayout.extras.ConstraintUtility.typicalDefaultConstraint;
import anylayout.extras.PercentConstraints;
import anylayout.extras.PercentConstraintsUtility;
import static ipsim.lang.Runnables.throwRuntimeException;
import ipsim.swing.IPAddressTextField;
import ipsim.swing.IPAddressTextFieldUtility;
import ipsim.swing.LabelledTextField;
import ipsim.swing.LabelledTextFieldUtility;
import static ipsim.swing.LabelledTextFieldUtility.createLabelledTextField;
import static ipsim.swing.NetBlockTextFieldUtility.createNetBlockTextField;
import ipsim.swing.SubnetMaskTextField;
import static ipsim.util.Collections.arrayList;

import javax.swing.JPanel;
import java.util.List;

public final class ScrapbookElementUtility
{
	public static ScrapbookElement createElement()
	{
		final NetBlockTextField subnetNumberTextField=createNetBlockTextField();
		final LabelledTextField subnetNumber=createLabelledTextField("<html>Subnet<br>Number</html>", subnetNumberTextField.getComponent());

		final List<IPAddressTextField> ipAddressFields=arrayList();

		final JPanel panel=new JPanel();

		final PercentConstraints constraints=PercentConstraintsUtility.newInstance(panel);
		AnyLayout.useAnyLayout(panel,0.5f,0.5f,constraints.getSizeCalculator(),typicalDefaultConstraint(throwRuntimeException));

		constraints.add(subnetNumber.getPanel(),5,5,45,50,false,false);

		final SubnetMaskTextField netMaskTextField=new SubnetMaskTextField();
		final LabelledTextField netMask=createLabelledTextField("Netmask", netMaskTextField);

		constraints.add(netMask.getPanel(),5,55,70,40,false,false);

		for (int a=1;a<4;a++)
		{
			final IPAddressTextField ipAddressTextField=IPAddressTextFieldUtility.newInstance();

			final LabelledTextField ipAddress=LabelledTextFieldUtility.createLabelledTextField2("IP Address "+a,ipAddressTextField.textField);
			constraints.add(ipAddress.getPanel(),50,33*a-33+3,50,30,false,false);
			ipAddressFields.add(ipAddressTextField);
		}

		return new ScrapbookElement()
		{
			@Override
            public List<IPAddressTextField> getIPAddressTextFields()
			{
				return ipAddressFields;
			}

			@Override
            public NetBlockTextField getSubnetTextField()
			{
				return subnetNumberTextField;
			}

			@Override
            public JPanel getPanel()
			{
				return panel;
			}

			@Override
            public SubnetMaskTextField getNetMaskTextField()
			{
				return netMaskTextField;
			}
		};
	}
}