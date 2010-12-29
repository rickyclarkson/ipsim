package ipsim.swing;

import java.awt.Component;

public interface HasComponent<T extends Component>
{
	T getComponent();
}