package com.Preprocessing;

import org.jsoup.Jsoup;

public class EscapeSpecialCharacters {

    /**
     * this method escape special characters
     */
    public String escapeHtmlSpecialCharacters(String s) {
        s = Jsoup.parse(s).text();
        s = s.replaceAll("[^\\w+!., ]", "");

        return s;
    }

}
