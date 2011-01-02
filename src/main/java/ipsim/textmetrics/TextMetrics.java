package ipsim.textmetrics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import static ipsim.Caster.equalT;

public final class TextMetrics {
    private static final FontRenderContext renderContext = new FontRenderContext(null, true, true);

    public static int getWidth(final Font font, final String string) {
        final Rectangle2D stringBounds = font.getStringBounds(string, 0, string.length(), renderContext);

        return (int) stringBounds.getWidth();
    }

    public static int getHeight(final Font font, final String string) {
        final Rectangle2D stringBounds = font.getStringBounds(string, 0, string.length(), renderContext);

        return (int) stringBounds.getHeight();
    }

    public static void drawString(final Graphics graphics, final String string, final int x, final int y, final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment, final boolean withYellowBackground) {
        int resultX = x;
        int resultY = y;

        final Font font = graphics.getFont();

        final int stringWidth = getWidth(font, string);
        final int stringHeight = getHeight(font, string);

        if (equalT(horizontalAlignment, HorizontalAlignment.RIGHT))
            resultX = x - stringWidth;

        if (equalT(horizontalAlignment, HorizontalAlignment.CENTRE))
            resultX = x - stringWidth / 2;

        if (equalT(verticalAlignment, VerticalAlignment.BOTTOM))
            resultY = y - stringHeight;

        if (equalT(verticalAlignment, VerticalAlignment.CENTRE))
            resultY = y - stringHeight / 2;

        if (withYellowBackground) {
            final Color previous = graphics.getColor();
            graphics.setColor(Color.yellow);
            graphics.fillRect(resultX - 2, resultY, stringWidth + 4, stringHeight);
            graphics.setColor(previous);
        }

        graphics.drawString(string, resultX, resultY + stringHeight * 3 / 4);
    }
}