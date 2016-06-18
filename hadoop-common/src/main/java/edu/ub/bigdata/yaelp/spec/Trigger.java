package edu.ub.bigdata.yaelp.spec;

public enum Trigger {

    UNKNOWN(""),
    IMPRESSION("1"),
    CLICK("2"),
    PRESENCE("3"),
    VIEW("4"),
    OTS("5"),
    PAGE_IMPRESION("6"),
    INTERACTIVE_IMPRESION("7");

    private String id;

    Trigger(String id) {
        this.id = id;
    }

    public static Trigger getTrigger(String id) {
        for ( Trigger e : values() ) {
            if ( e.id.equalsIgnoreCase(id) ){
                return e;
            }
        }

        return null;
    }

}
