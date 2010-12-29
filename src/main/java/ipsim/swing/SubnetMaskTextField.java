package ipsim.swing;

import ipsim.network.connectivity.ip.IPAddress;
import ipsim.network.connectivity.ip.NetMask;
import ipsim.network.ethernet.NetMaskUtility;
import ipsim.network.ip.IPAddressUtility;
import ipsim.textmetrics.TextMetrics;

import javax.swing.*;
import java.awt.*;

public final class SubnetMaskTextField extends JTextField
{
	private static final long serialVersionUID=-1975041645986840656L;

	private transient final IPAddressValidator validator;

	public SubnetMaskTextField()
	{
		validator=new IPAddressValidator(IPAddressUtility.zero);

		final ValidatingDocumentListener listener=new ValidatingDocumentListener(this, getBackground(), Color.pink, validator);

		getDocument().addDocumentListener(listener);
	}

	public NetMask getNetMask()
	{
		return NetMaskUtility.getNetMask(validator.getAddress().rawValue);
	}

	public void setNetMask(final NetMask address)
	{
		validator.setIPAddress(new IPAddress(address.rawValue));

		if (0==address.rawValue)
			setText("");
		else
			setText(NetMask.asString(address.rawValue));
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(TextMetrics.getWidth(getFont(),"999.999.999.999/99"),(int)super.getPreferredSize().getHeight());
	}

	public boolean isValidText()
	{
		return validator.isValid(getDocument());
	}
}