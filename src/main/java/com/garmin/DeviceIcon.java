package com.garmin;

/**
 * Created by gwicks on 11/04/2018.
 */

public enum DeviceIcon {

    DEFAULT("default", "default-device"),
    VIVOFIT("vívofit", "vivofit"),
    VIVOFIT_2("vívofit 2", "vivofit-2"),
    VIVOFIT_3("vívofit 3", "vivofit-3-white"),
    VIVOFIT_JR("vívofit jr.", "vivofit-3"),
    VIVOSMART_HR("vívosmart HR", "vivosmart-hr"),
    VIVOACTIVE_HR("vívoactive HR", "vivoactivehr"),
    VIVOSPORT("vívosport", "vivosport"),
    VIVOACTIVE_3("vívoactive 3", "default-device"),
    VIVOMOVE_HR("vívomove HR", "default-device"),
    FENIX_5("fenix5", "fenix-5"),
    FENIX_5S("fenix 5s", "fenix-5s-turquoise");

    private final String displayName;
    private final String icon;

    DeviceIcon(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public static String deviceIcon(String deviceName) {

        for (DeviceIcon deviceIcon : values()) {
            if (deviceIcon.displayName.equalsIgnoreCase(deviceName)) {
                return deviceIcon.icon;
            }
        }

        return DEFAULT.icon;
    }
}
