package ipsim.gui;

import ipsim.Global;
import ipsim.NetworkContext;
import ipsim.image.ImageLoader;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import static ipsim.Caster.equalT;

//todo make the components shrink to fit the space
public class StandardToolBarUtility {
    public static Container createStandardToolBar() {
        final JPanel tinyIcons = new JPanel(new GridLayout(1, 7));

        class CustomFunction {
            public void createTinyIcon(final String imageLocation, final Runnable runnable, final String text) {
                final ActionListener listener = new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        runnable.run();
                    }
                };

                final JButton button = new JButton("<html><center>" + text + "</center></html>");
                if (!equalT("", imageLocation))
                    button.setIcon(ImageLoader.loadImage(StandardToolBarUtility.class.getResource(imageLocation)));

                button.setHorizontalTextPosition(SwingConstants.CENTER);
                button.setVerticalTextPosition(SwingConstants.BOTTOM);

                button.setOpaque(true);
                button.addActionListener(listener);

                tinyIcons.add(button);
            }
        }

        final CustomFunction createTinyIcon = new CustomFunction();

        createTinyIcon.createTinyIcon("/images/file_new.png", MenuHandler.networkNew(), "New");
        createTinyIcon.createTinyIcon("/images/file_open.png", MenuHandler.fileOpen(), "Open");
        createTinyIcon.createTinyIcon("/images/file_save.png", MenuHandler.fileSave(), "Save");
        createTinyIcon.createTinyIcon("/images/view_zoom_in.png", MenuHandler.zoomIn(), "Zoom In");
        createTinyIcon.createTinyIcon("/images/view_zoom_out.png", MenuHandler.zoomOut(), "Zoom Out");
        createTinyIcon.createTinyIcon("/images/view_zoom_auto.png", MenuHandler.zoomToFit(), "Zoom Auto");

        createTinyIcon.createTinyIcon("", new Runnable() {
            @Override
            public void run() {
                if (!Global.getNetworkContext().userPermissions.allowDownloadingNewConfig()) {
                    NetworkContext.errors("Cannot download a new configuration during a test!");
                    return;
                }

                if (Global.getNetworkContext().network.modified && !MenuHandler.networkModifiedDialog())
                    return;

                MenuHandler.downloadConfiguration().run();
            }
        }, "<html>Download<br>Config</html>");

        final JPanel tinyPanel = new JPanel(new BorderLayout());
        tinyPanel.add(tinyIcons, BorderLayout.WEST);

        return tinyPanel;
    }
}