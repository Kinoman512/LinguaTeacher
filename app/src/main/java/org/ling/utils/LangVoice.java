package org.ling.utils;

/**
 * Created by Dmitry on 03.07.2016.
 */
public enum LangVoice {


    de_DE1("de-DE","de","Male" ,"Microsoft Server Speech Text to Speech Voice (de-DE, Hedda)"),
    de_DE2("de-DE","de","Female" ,"Microsoft Server Speech Text to Speech Voice (de-DE, Stefan, Apollo)"),

    en_Au("en-AU","en","Female" ,"Microsoft Server Speech Text to Speech Voice (en-AU, Catherine)"),

    en_CA("en-CA","en","Female" ,"Microsoft Server Speech Text to Speech Voice (en-CA, Linda)"),
    en_GB2("en-GB","en","Female" ,"Microsoft Server Speech Text to Speech Voice (en-GB, Susan, Apollo)"),
    en_GB("en-GB","en","Male" ,"Microsoft Server Speech Text to Speech Voice (en-GB, George, Apollo)"),
    en_IN("en-IN","en","Male" ,"Microsoft Server Speech Text to Speech Voice (en-IN, Ravi, Apollo)"),
    en_US2("en-US","en","Female" ,"Microsoft Server Speech Text to Speech Voice (en-US, ZiraRUS)"),
    en_US("en-US","en","Male" ,"Microsoft Server Speech Text to Speech Voice (en-US, BenjaminRUS)"),

    es_ES2("es-ES","es","Female" ,"Microsoft Server Speech Text to Speech Voice (es-ES, Laura, Apollo)"),
    es_ES(" es-ES","es","Male" ,"Microsoft Server Speech Text to Speech Voice (es-ES, Pablo, Apollo)"),

    es_MX("es-MX","es","Male" ,"Microsoft Server Speech Text to Speech Voice (es-MX, Raul, Apollo)"),

    fr_CA("fr-CACA","fr","Female" ,"Microsoft Server Speech Text to Speech Voice (fr-CA, Caroline)"),

    fr_FR2("fr-FR","fr","Female" ,"Microsoft Server Speech Text to Speech Voice (fr-FR, Julie, Apollo)"),
    fr_FR("fr-FR","fr","Male" ,"Microsoft Server Speech Text to Speech Voice (fr-FR, Paul, Apollo)"),

    it_IT("it-IT","it","Male" ,"Microsoft Server Speech Text to Speech Voice (it-IT, Cosimo, Apollo)"),
    ja_JP2("ja-JP","jp","Female" ,"Microsoft Server Speech Text to Speech Voice (ja-JP, Ayumi, Apollo)"),
    ja_JP("ja-JP","jp","Male" ,"Microsoft Server Speech Text to Speech Voice (ja-JP, Ichiro, Apollo)"),

    pt_BR("pt-BR","pt","Male" ,"Microsoft Server Speech Text to Speech Voice (pt-BR, Daniel, Apollo)"),

    ru_RU2("ru-RU","ru","Female" ,"Microsoft Server Speech Text to Speech Voice (pt-BR, Daniel, Apollo)"),
    ru_RU("ru-RU","ru","Male" ,"Microsoft Server Speech Text to Speech Voice (ru-RU, Pavel, Apollo)"),

    zh_CN2("zh-CN","zn","Female" ,"Microsoft Server Speech Text to Speech Voice (zh-CN, HuihuiRUS)"),
    zh_CN3(" zh-CN","zn","Female" ,"Microsoft Server Speech Text to Speech Voice (zh-CN, Yaoyao, Apollo)"),
    zh_CN(" zh-CN","zn","Male" ,"Microsoft Server Speech Text to Speech Voice (zh-CN, Kangkang, Apollo)"),

    zh_HK2("zh-HK","zn","Female" ,"Microsoft Server Speech Text to Speech Voice (zh-HK, Tracy, Apollo)"),
    zh_HK("zh-HK","zn","Male" ,"Microsoft Server Speech Text to Speech Voice (zh-HK, Danny, Apollo)"),
    zh_TW2("zh-TW","zn","Female" ,"Microsoft Server Speech Text to Speech Voice (zh-TW, Yating, Apollo)"),
    zh_TW1("zh-TW","zn","Male" ,"Microsoft Server Speech Text to Speech Voice (zh-TW, Zhiwei, Apollo)"),;

    private String codeVoice;
    private String codeLang;
    private String gender;
    private String description;

    public static LangVoice findByLang(String desc){
        for(LangVoice v : values()){
            if( v.getCodeLang().equals(desc)){
                return v;
            }
        }
        return null;
    }

    public String getCodeVoice() {
        return codeVoice;
    }

    public void setCodeVoice(String codeVoice) {
        this.codeVoice = codeVoice;
    }

    public String getCodeLang() {
        return codeLang;
    }

    public void setCodeLang(String codeLang) {
        this.codeLang = codeLang;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    LangVoice(String codeVoice, String codeLang, String gender,  String description  ) {
        this.description = description;
        this.gender = gender;
        this.codeLang = codeLang;
        this.codeVoice = codeVoice;
    }
}
