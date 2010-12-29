package ipsim.gui.components;

import fpeas.lazy.Lazy;
import ipsim.network.ethernet.NetBlock;

import javax.swing.*;

public interface NetBlockTextField
{
	Lazy<NetBlock> netBlock();
	boolean isValid();

	JTextField getComponent();
}