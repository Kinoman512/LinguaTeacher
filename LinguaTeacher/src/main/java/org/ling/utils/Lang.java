package org.ling.utils;


import org.ling.model.iType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by Dmitry on 27.07.2016.
 */
public class Lang implements iType {


    Locale locale;



    static String[] locales = {
            "az", "mk",
            "sq", "mi"
            , "en", "mr"
            , "ar", "mn"
            , "hy", "de"
            , "af", "ne"
            , "eu", "no"
            , "ba", "pa"
            , "be", "fa"
            , "bn", "pl"
            , "bg", "pt"
            , "bs", "ro"
            , "cy", "ru"
            , "hu", "ceb"
            , "vi", "sr"
            , "ht", "si"
            , "gl", "sk"
            , "nl", "sl"
            , "el", "sw"
            , "ka", "su"
            , "gu", "tg"
            , "da", "th"
            , "he", "tl"
            , "yi", "ta"
            , "id", "tt"
            , "ga", "te"
            , "it", "tr"
            , "is", "udm"
            , "es",  "uz"
            , "kk", "uk"
            , "kn", "ur"
            , "ca", "fi"
            , "ky", "fr"
            , "zh", "hi"
            , "ko", "hr"
            , "la", "cs"
            , "lv", "sv"
            , "lt", "gd"
            , "mg", "et"
            , "ms", "eo"
            , "ml", "jv"
            , "mt", "ja"
    };

    static String[] convertedLocales = {
            "ja:jp",
            "sq:al",
            "fa:ir",
            "ceb:ph",
            "el:gr",
            "sw:tz",
            "ka:ge",
            "su:id",
            "ta:in",
            "te:in",
            "uk:ua",
            "kn:in",
            "zh:cn",
            "hi:in",
            "cs:cz",
            "jv:id",
            "mi:nz",
            "hy:am",
            "dk:da",
            "iw:il",
            "ji:il",
            "kk:kz",
            "ka:ge",
            "ur:in",
            "da:dk",
            "ko:kr",
            "ly:lv"

    };


    public static List<Lang> getAllLocale(){
        List<Lang> list = new ArrayList<>();
        for(int i = 0;i<locales.length;i++){
            Lang res = new Lang();
            String lang = locales[i];
            res.setLocale(stringToLocale(lang));
            list.add(res);
        };
        return  list;
    }

    @Override
    public String getName() {
        return locale.getDisplayName();
    }

    @Override
    public int getType() {
        return 1;
    }

    public String getCodeCountry() {
        String lang = locale.getLanguage();
        String e = lang;
        for (int i = 0; i < convertedLocales.length ; i++) {
            String str = convertedLocales[i].split(":")[0];

            if (str.equals(lang)) {
                e = convertedLocales[i].split(":")[1];
            }
        }
        return e;
    }
    public String getCodeLingua() {
        return locale.getLanguage();
    }

    public String getCodeLinguaByCountry(String lang) {
        String e = lang;
        for (int i = 0; i < convertedLocales.length ; i++) {
            String str = convertedLocales[i].split(":")[0];

            if (str.equals(lang)) {
                e = convertedLocales[i].split(":")[1];
            }
        }
        return e;
    }


    public String getDescription() {
        return locale.getDisplayName();
    }

    public static Locale stringToLocale(String s) {
        String l = "";
        String c = "";
        StringTokenizer tempStringTokenizer = new StringTokenizer(s,",");
        if(tempStringTokenizer.hasMoreTokens())
             l = String.valueOf(tempStringTokenizer.nextElement());
        if(tempStringTokenizer.hasMoreTokens())
             c = String.valueOf(tempStringTokenizer.nextElement());
        return new Locale(l,c);
    }




    public static Lang findByCodeCountry(String lang) {

        String e = lang;
        for (int i = 0; i < convertedLocales.length ; i++) {
            String str = convertedLocales[i].split(":")[1];
            if (str.equals(lang)) {
                e = convertedLocales[i].split(":")[0];
            }
        }
        Lang res = new Lang();
        res.setLocale(Locale.ENGLISH);

        for (int i = 0; i < locales.length; i++) {
            if (locales[i].equals(e)) {
                res.setLocale(stringToLocale(e));
                return res;
            }
        }
        return res;
    }

    public static Lang findByCodeLingua(String lang) {

        String e = lang;
        Lang res = new Lang();
        res.setLocale(Locale.ENGLISH);

        for (int i = 0; i < locales.length ; i++) {
            if (locales[i].equals(lang)) {
                res.setLocale(stringToLocale(e));
                return res;
            }
        }
        return res;
    }

    private void setLocale(Locale loc) {
        locale = loc;
    }

    public Locale getLocale() {
        return  locale;
    }


    public static void getDefault() {
        Locale natv = Locale.getDefault();

    }
}
