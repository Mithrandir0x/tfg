package com.beabloo.bigdata.cockroach.spec;


public enum Platform {

    UNKNOWN,
    DS,
    MOBILE,
    WIFI,
    TRILATERATION,
    BLUETOOTH,
    RECOGNITION,
    INTERACTIVE;

    public static Platform getPlatform(String name) {
        for ( Platform p : values() ) {
            if ( p.name().equalsIgnoreCase(name) ){
                return p;
            }
        }

        return null;
    }

}
