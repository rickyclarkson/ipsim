package ipsim.gui.components;

import ipsim.swing.IPAddressTextField;
import ipsim.swing.SubnetMaskTextField;

import javax.swing.JPanel;

public interface ScrapbookElement
{
	NetBlockTextField getSubnetTextField();
	
	Iterable<IPAddressTextField> getIPAddressTextFields();
	JPanel getPanel();
	SubnetMaskTextField getNetMaskTextField();
}