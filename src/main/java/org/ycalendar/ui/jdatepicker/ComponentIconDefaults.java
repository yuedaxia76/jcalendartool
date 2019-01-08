package org.ycalendar.ui.jdatepicker;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.ycalendar.ui.jdatepicker.graphics.JNextIcon;
import org.ycalendar.ui.jdatepicker.graphics.JPreviousIcon;
import org.ycalendar.ui.jdatepicker.graphics.JtodayIcon;

public final class ComponentIconDefaults {

    private static final Logger log = LoggerFactory.getLogger(ComponentIconDefaults.class);
    private static ComponentIconDefaults instance;

    public static ComponentIconDefaults getInstance() {
        if (instance == null) {
            instance = new ComponentIconDefaults();
        }
        return instance;
    }

    public enum Key {
        // TODO
    }

    private static final String CLEAR = "icons/clear.png";

    private Icon clearIcon;
    private Icon nextMonthIconEnabled;
    private Icon nextYearIconEnabled;
    private Icon previousMonthIconEnabled;
    private Icon previousYearIconEnabled;
    private Icon nextMonthIconDisabled;
    private Icon nextYearIconDisabled;
    private Icon previousMonthIconDisabled;
    private Icon previousYearIconDisabled;
    private Icon popupButtonIcon;
    private Icon todayButtonIconEnable;
     private Icon todayButtonIconDisable;

    private ComponentIconDefaults() {
        // TODO consider making all the icons vector images which will scale
        try {
            clearIcon = loadIcon(CLEAR);
            nextMonthIconEnabled = new JNextIcon(4, 7, false, true);
            nextYearIconEnabled = new JNextIcon(8, 7, true, true);
            previousMonthIconEnabled = new JPreviousIcon(4, 7, false, true);
            previousYearIconEnabled = new JPreviousIcon(8, 7, true, true);
            nextMonthIconDisabled = new JNextIcon(4, 7, false, false);
            nextYearIconDisabled = new JNextIcon(8, 7, true, false);
            previousMonthIconDisabled = new JPreviousIcon(4, 7, false, false);
            previousYearIconDisabled = new JPreviousIcon(8, 7, true, false);
            todayButtonIconEnable=new JtodayIcon(7,7,true);
            todayButtonIconDisable=new JtodayIcon(7,7,false);
            popupButtonIcon = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Icon loadIcon(String path) throws IOException {

        try (InputStream stream = ComponentIconDefaults.class.getResourceAsStream(path);) {
            if (stream != null) {
                BufferedImage image = ImageIO.read(stream);
                return new ImageIcon(image);
            }else{
                log.warn( "Resource {} not found", path);
                return null;
            }

        }
    }

    public Icon getClearIcon() {
        return clearIcon;
    }

    public void setClearIcon(Icon clearIcon) {
        this.clearIcon = clearIcon;
    }

    public Icon getNextMonthIconEnabled() {
        return nextMonthIconEnabled;
    }
    public Icon getTodayIconEnabled() {
        return todayButtonIconEnable;
    }
    
    public Icon getTodayIconDisable() {
        return todayButtonIconDisable;
    }    
    public void setNextMonthIconEnabled(Icon nextMonthIconEnabled) {
        this.nextMonthIconEnabled = nextMonthIconEnabled;
    }

    public Icon getNextMonthIconDisabled() {
        return nextMonthIconDisabled;
    }

    public void setNextMonthIconDisabled(Icon nextMonthIconDisabled) {
        this.nextMonthIconDisabled = nextMonthIconDisabled;
    }

    public Icon getNextYearIconEnabled() {
        return nextYearIconEnabled;
    }

    public void setNextYearIconEnabled(Icon nextYearIconEnabled) {
        this.nextYearIconEnabled = nextYearIconEnabled;
    }

    public Icon getNextYearIconDisabled() {
        return nextYearIconDisabled;
    }

    public void setNextYearIconDisabled(Icon nextYearIconDisabled) {
        this.nextYearIconDisabled = nextYearIconDisabled;
    }

    public Icon getPreviousMonthIconEnabled() {
        return previousMonthIconEnabled;
    }

    public void setPreviousMonthIconEnabled(Icon previousMonthIconEnabled) {
        this.previousMonthIconEnabled = previousMonthIconEnabled;
    }

    public Icon getPreviousMonthIconDisabled() {
        return previousMonthIconDisabled;
    }

    public void setPreviousMonthIconDisabled(Icon previousMonthIconDisabled) {
        this.previousMonthIconDisabled = previousMonthIconDisabled;
    }

    public Icon getPreviousYearIconEnabled() {
        return previousYearIconEnabled;
    }

    public void setPreviousYearIconEnabled(Icon previousYearIconEnabled) {
        this.previousYearIconEnabled = previousYearIconEnabled;
    }

    public Icon getPreviousYearIconDisabled() {
        return previousYearIconDisabled;
    }

    public void setPreviousYearIconDisabled(Icon previousYearIconDisabled) {
        this.previousYearIconDisabled = previousYearIconDisabled;
    }

    public Icon getPopupButtonIcon() {
        return popupButtonIcon;
    }

    public void setPopupButtonIcon(Icon popupButtonIcon) {
        this.popupButtonIcon = popupButtonIcon;
    }

}
