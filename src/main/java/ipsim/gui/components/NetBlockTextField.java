package ipsim.gui.components;

import fj.P1;
import ipsim.network.ethernet.NetBlock;
import javax.swing.JTextField;

public interface NetBlockTextField
{
	P1<NetBlock> netBlock();
	boolean isValid();

	JTextField getComponent();
}