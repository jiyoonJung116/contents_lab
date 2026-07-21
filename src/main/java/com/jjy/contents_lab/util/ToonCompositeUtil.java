package com.jjy.contents_lab.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;

public class ToonCompositeUtil {

    public static String createFinalToonCut(String bgBase64, String charBase64, String caption) {
        int defaultWidth = 600;
        int defaultHeight = 600;
        
        BufferedImage finalImage = new BufferedImage(defaultWidth, defaultHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = finalImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, defaultWidth, defaultHeight);

        if (charBase64 != null && !charBase64.trim().isEmpty()) {
            try {
                if (charBase64.contains(",")) {
                    charBase64 = charBase64.split(",")[1];
                }
                
                byte[] charBytes = Base64.getDecoder().decode(charBase64.trim());
                BufferedImage charImageRaw = ImageIO.read(new ByteArrayInputStream(charBytes));
                
                if (charImageRaw != null) {
                    int charWidth = (int) (defaultWidth * 0.85); 
                    int charHeight = (int) (charImageRaw.getHeight() * ((double) charWidth / charImageRaw.getWidth()));
                    int charX = (defaultWidth - charWidth) / 2;  
                    int charY = defaultHeight - charHeight - 10;
                    g2d.drawImage(charImageRaw, charX, charY, charWidth, charHeight, null);
                } 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (caption == null) caption = "";
        addSpeechBubble(g2d, caption, defaultWidth);

        g2d.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(finalImage, "jpeg", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            return "";
        }
    }

    
    // 말풍선 생성
    private static void addSpeechBubble(Graphics2D g2d, String caption, int width) {
        int fontSize = 24; 
        Font font = new Font("Malgun Gothic", Font.BOLD, fontSize);
        g2d.setFont(font);

        FontMetrics metrics = g2d.getFontMetrics(font);
        int textWidth = metrics.stringWidth(caption);
        int textHeight = metrics.getHeight();
        int paddingX = 40; 
        int paddingY = 25; 
        int bubbleWidth = textWidth + (paddingX * 2);
        int bubbleHeight = textHeight + (paddingY * 2);

        if (bubbleWidth < width * 0.6) bubbleWidth = (int) (width * 0.6);
        if (bubbleWidth > width - 40) bubbleWidth = width - 40;

        int bubbleX = (width - bubbleWidth) / 2;
        int bubbleY = 40; 

        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(bubbleX, bubbleY, bubbleWidth, bubbleHeight, 40, 40);
        g2d.setColor(Color.BLACK); 
        g2d.setStroke(new BasicStroke(3.0f)); 
        g2d.drawRoundRect(bubbleX, bubbleY, bubbleWidth, bubbleHeight, 40, 40);

        int[] xPoints = { (width / 2) - 10, (width / 2) + 10, (width / 2) };
        int[] yPoints = { bubbleY + bubbleHeight - 2, bubbleY + bubbleHeight - 2, bubbleY + bubbleHeight + 12 };
        g2d.setColor(Color.WHITE);
        g2d.fillPolygon(xPoints, yPoints, 3);
        g2d.setColor(Color.BLACK);
        g2d.drawLine(xPoints[0], yPoints[0], xPoints[2], yPoints[2]);
        g2d.drawLine(xPoints[1], yPoints[1], xPoints[2], yPoints[2]);

        int textX = bubbleX + ((bubbleWidth - textWidth) / 2);
        int textY = bubbleY + paddingY + metrics.getAscent();
        g2d.drawString(caption, textX, textY);
    }
}