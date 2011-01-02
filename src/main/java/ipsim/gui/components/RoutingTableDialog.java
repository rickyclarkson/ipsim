package ipsim.gui.components;

import javax.swing.JDialog;

public interface RoutingTableDialog {
    Runnable populateElements();

    JDialog getJDialog();

    void editButtonClicked();

    void deleteButtonClicked();
}