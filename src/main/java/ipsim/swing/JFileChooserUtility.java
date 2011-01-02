package ipsim.swing;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class JFileChooserUtility {
    public static int showSaveDialog(final JFileChooser fileChooser, final JFrame realFrame) {
        return fileChooser.showSaveDialog(realFrame);
    }
}