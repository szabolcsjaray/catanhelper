package com.jeec.game;

public enum ColorCode {
    RED("Piros", "#c6040e", true),
    BLUE("K\u00e9k", "rgb(12, 75, 176)", true),
    ORANGE("Narancs", "#f7741d"),
    WHITE("Feh\u00e9r", "#FFFFFF");

    private String colorStr;
    private String colorName;
    private boolean whiteText;

    public String getColorName() {
        return this.colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    private ColorCode(String colorName, String colorStr) {
        this.colorStr = colorStr;
        this.colorName = colorName;
        this.setWhiteText(false);
    }

    private ColorCode(String colorName, String colorStr, boolean whiteText) {
        this.colorStr = colorStr;
        this.colorName = colorName;
        this.setWhiteText(whiteText);
    }

    public String getColorStr() {
        return this.colorStr;
    }

    public static ColorCode findColor(String colorName2) {
        for (ColorCode colorCode : ColorCode.values()) {
            if (colorCode.getColorName().equals(colorName2)) {
                return colorCode;
            }
            if (!colorCode.name().equals(colorName2)) continue;
            return colorCode;
        }
        return null;
    }

    public boolean isWhiteText() {
        return whiteText;
    }

    public void setWhiteText(boolean whiteText) {
        this.whiteText = whiteText;
    }
}
