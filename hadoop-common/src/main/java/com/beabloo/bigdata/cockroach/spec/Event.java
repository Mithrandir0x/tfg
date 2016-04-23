package com.beabloo.bigdata.cockroach.spec;

public enum Event {

    UNKNOWN(""),
    IMPRESSION("1"),
    CLICK("2"),
    PRESENCE("3"),
    VIEW("4"),
    OTS("5"),
    PAGE_IMPRESION("6"),
    INTERACTIVE_IMPRESION("7");

    private String id;

    Event(String id) {
        this.id = id;
    }

    public static Event getEvent(String id) {
        for ( Event e : values() ) {
            if ( e.id.equalsIgnoreCase(id) ){
                return e;
            }
        }

        return null;
    }

}
