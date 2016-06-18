package edu.ub.bigdata.yaelp.spec;


public enum Environment {

    UNKNOWN,
    DS,
    MOBILE,
    WIFI,
    TRILATERATION,
    BLUETOOTH,
    RECOGNITION,
    INTERACTIVE;

    public static Environment getEnvironment(String name) {
        for ( Environment p : values() ) {
            if ( p.name().equalsIgnoreCase(name) ){
                return p;
            }
        }

        return null;
    }

}
