package gui.main.frame;

import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;
import com.jtattoo.plaf.texture.TextureUtils;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 *
 * @author Michael Hagen
 */
public class TitleBorder implements Border {

    private static final Insets BORDER_INSETS = new Insets(24, 1, 1, 1);
    private String title = null;

    public TitleBorder(String aTitle) {
        title = aTitle;
    }

    private boolean isTextureLAF() {
        return "Texture".equals(javax.swing.UIManager.getLookAndFeel().getName());
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (isTextureLAF()) {
            paintTextureBorder(c, g, x, y, width, height);
        } else {
            paintDefaultBorder(c, g, x, y, width, height);
        }
    }

    public void paintDefaultBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2D = (Graphics2D) g;
        Color backColorLight = UIManager.getColor("activeCaptionLight");
        Color backColorDark = UIManager.getColor("activeCaptionDark");
        Color frameColor = ColorHelper.darker(UIManager.getColor("controlShadow"), 20);
        if (backColorLight == null) {
            backColorLight = new Color(220, 220, 220);
        }
        if (backColorDark == null) {
            backColorDark = new Color(180, 180, 180);
        }
        if (frameColor == null) {
            frameColor = new Color(120, 120, 120);
        }

        g2D.setColor(frameColor);
        g2D.drawRect(x, y, width - 1, height - 1);
        g2D.setPaint(new GradientPaint(0, 0, backColorLight, 0, BORDER_INSETS.top, backColorDark));
        g2D.fillRect(x + 1, y + 1, width - 2, BORDER_INSETS.top - 1);
        g2D.setColor(frameColor);
        g2D.drawLine(x + 1, y + BORDER_INSETS.top - 1, x + width - 2, y + BORDER_INSETS.top - 1);

        Color foreColor = UIManager.getColor("activeCaptionText");
        paintTitle(c, g, foreColor, x, y, width, height);
    }

    public void paintTextureBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2D = (Graphics2D) g;
        Composite saveComposite = g2D.getComposite();
        TextureUtils.fillComponent(g, c, x, y, width, BORDER_INSETS.top, TextureUtils.ALTER_BACKGROUND_TEXTURE_TYPE);
        AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
        g2D.setComposite(alpha);
        Color frameColor = UIManager.getColor("Button.frame");
        g2D.setColor(frameColor);
        g2D.drawRect(x, y, width - 1, height - 1);
        g2D.drawLine(x + 1, y + BORDER_INSETS.top - 1, x + width - 2, y + BORDER_INSETS.top - 1);
        g2D.setColor(frameColor);
        g2D.setColor(Color.white);
        float a = 0.7f;
        for (int i = 0; i < 7; i++) {
            alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a);
            g2D.setComposite(alpha);
            g2D.drawLine(x + 1, y + 1 + i, x + width - 2, y + 1 + i);
            a -= 0.1f;
        }

        g2D.setComposite(saveComposite);

        Color foreColor = c.getForeground();
        paintTitle(c, g, foreColor, x, y, width, height);
    }

    public void paintTitle(Component c, Graphics g, Color foreColor, int x, int y, int width, int height) {
        if ((title != null) && (title.trim().length() > 0)) {
            Font font = UIManager.getFont("Label.font").deriveFont(Font.BOLD);
            if (font == null) {
                font = c.getFont();
            }
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics();
            String clippedTitle = JTattooUtilities.getClippedText(title, fm, width - 12);
            if (ColorHelper.getGrayValue(foreColor) > 128) {
                Graphics2D g2D = (Graphics2D) g;
                Composite savedComposit = g2D.getComposite();
                AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
                g2D.setComposite(alpha);
                g.setColor(Color.black);
                JTattooUtilities.drawString((JComponent) c, g, clippedTitle, x + 7, y + fm.getHeight() + 1);
                g2D.setComposite(savedComposit);
            }
            g.setColor(foreColor);
            JTattooUtilities.drawString((JComponent) c, g, clippedTitle, x + 6, y + fm.getHeight());
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        Font font = UIManager.getFont("Label.font");
        if (font != null) {
            BORDER_INSETS.top = font.getSize() * 2;
        }
        return BORDER_INSETS;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
