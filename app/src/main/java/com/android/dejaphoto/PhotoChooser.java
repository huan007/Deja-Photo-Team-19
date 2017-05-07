package com.android.dejaphoto;

public class PhotoChooser implements Chooser<String> {

    public String next() {
        return (true) ? dejaNext() : randomNext();
    }

    private String dejaNext() {
        return null;
    }

    private String randomNext() {
        return null;
    }

}
