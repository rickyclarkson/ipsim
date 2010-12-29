package ipsim.swing;

import com.rickyclarkson.java.lang.Throwables;
import static ipsim.Global.getNetworkContext;
import ipsim.network.NetworkUtility;
import ipsim.webinterface.WebInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class ExceptionUploadListener implements ActionListener
{
	private final Throwable exception;

	public ExceptionUploadListener(final Throwable exception)
	{
		this.exception=exception;
	}

	@Override
    public void actionPerformed(final ActionEvent event)
	{
		WebInterface.putException(Throwables.toString(exception), NetworkUtility.saveToString(getNetworkContext().network));
	}
}