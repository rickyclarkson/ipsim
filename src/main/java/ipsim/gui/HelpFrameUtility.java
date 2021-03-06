package ipsim.gui;

import com.rickyclarkson.javax.swing.ScrollableEditorPaneUtility;
import fj.Effect;
import fj.F;
import fj.Function;
import fj.data.Either;
import ipsim.Caster;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.io.IOException;
import java.net.URL;
import java.util.Stack;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import org.jetbrains.annotations.NotNull;

import static ipsim.Caster.equalT;
import static ipsim.lang.Runnables.nothing;
import static ipsim.swing.Buttons.newButton;

public final class HelpFrameUtility {
    public static final URL helpRoot = HelpFrameUtility.class.getResource("/help/index.html");

    public static JFrame createHelpFrame() {
        final JFrame frame = new JFrame();
        frame.setSize(600, 600);
        frame.setTitle("IPSim Help");
        frame.setLayout(new BorderLayout());
        final JScrollPane pane = new JScrollPane();

        final Either<JEditorPane, IOException> htmlPane = ScrollableEditorPaneUtility.createScrollableEditorPane(pane, helpRoot);

        final F<IOException, Runnable> doNothing = Function.constant(nothing);

        htmlPane.either(new F<JEditorPane, Runnable>() {
            @Override
            @NotNull
            public Runnable f(@NotNull final JEditorPane pane2) {
                return new Runnable() {
                    @Override
                    public void run() {
                        pane2.setEditable(false);
                        pane2.setCaretPosition(0);
                        final Hyperactive hyperactive = new Hyperactive(pane2, helpRoot);
                        pane2.addHyperlinkListener(hyperactive);
                        pane2.setAutoscrolls(true);
                        pane.getViewport().add(pane2);

                        frame.add(pane, BorderLayout.CENTER);

                        doStuff(hyperactive, frame);
                    }
                };
            }
        }, doNothing).run();

        return frame;
    }

    private static void doStuff(final Hyperactive hyperactive, final Container parent) {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panel.add(newButton("Contents", new Runnable() {
            @Override
            public void run() {
                hyperactive.goHome();
            }
        }));

        panel.add(newButton("Back", new Runnable() {
            @Override
            public void run() {
                hyperactive.back();
            }
        }));

        parent.add(panel, BorderLayout.NORTH);
    }

    public static final Effect<Container> createPane = new Effect<Container>() {
        @Override
        public void e(final Container parent) {
            parent.setLayout(new BorderLayout());
            final JScrollPane pane = new JScrollPane();

            final Either<JEditorPane, IOException> htmlPane = ScrollableEditorPaneUtility.createScrollableEditorPane(pane, helpRoot);

            final F<IOException, Runnable> doNothing = Function.constant(nothing);

            htmlPane.either(new F<JEditorPane, Runnable>() {
                @Override
                @NotNull
                public Runnable f(@NotNull final JEditorPane pane2) {
                    return new Runnable() {
                        @Override
                        public void run() {
                            pane2.setEditable(false);
                            pane2.setCaretPosition(0);
                            final Hyperactive hyperactive = new Hyperactive(pane2, helpRoot);
                            pane2.addHyperlinkListener(hyperactive);
                            pane2.setAutoscrolls(true);
                            pane.getViewport().add(pane2);

                            parent.add(pane, BorderLayout.CENTER);

                            doStuff(hyperactive, parent);
                        }
                    };
                }
            }, doNothing).run();
        }
    };

}

/**
 * Code taken almost directly from Sun's Javadocs for JEditorPane.
 */
final class Hyperactive implements HyperlinkListener {
    public final Stack<URL> history = new Stack<URL>();

    public final JEditorPane editorPane;

    public final URL home;

    Hyperactive(final JEditorPane editorPane, final URL home) {
        this.editorPane = editorPane;
        this.home = home;
    }

    public void goHome() {
        try {
            editorPane.setPage(home);
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void back() {
        if (history.empty())
            return;

        try {
            editorPane.setPage(history.pop());
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void hyperlinkUpdate(final HyperlinkEvent event) {
        if (!equalT(event.getEventType(), HyperlinkEvent.EventType.ACTIVATED))
            return;

        final JEditorPane pane = (JEditorPane) event.getSource();

        if (Caster.isHTMLFrameHyperlinkEvent(event)) {
            final HTMLFrameHyperlinkEvent event2 = (HTMLFrameHyperlinkEvent) event;

            final HTMLDocument document = (HTMLDocument) pane.getDocument();

            document.processHTMLFrameHyperlinkEvent(event2);
        } else
            try {
                history.push(pane.getPage());
                pane.setPage(event.getURL());
            } catch (final IOException exception) {
                throw new RuntimeException(exception);
            }
    }
}