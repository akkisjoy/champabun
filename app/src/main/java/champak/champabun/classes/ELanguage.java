package champak.champabun.classes;


import champak.champabun.R;

public enum ELanguage {
    EN(0), FR(1), DE(2), IT(3), ES(4), PT_PT(5), PT_BR(6), RU(7), JA(8), TR(9);

    private int languageCode;

    private ELanguage(int langCode) {
        languageCode = langCode;
    }

    public int getLanguageCode() {
        return languageCode;
    }

    public int getLanguageName() {
        switch (languageCode) {
            case 0:
                return R.string.lang_en;

            case 1:
                return R.string.lang_fr;

            case 2:
                return R.string.lang_de;

            case 3:
                return R.string.lang_it;

            case 4:
                return R.string.lang_es;

            case 5:
                return R.string.lang_pt_rPT;

            case 6:
                return R.string.lang_pt_rBR;

            case 7:
                return R.string.lang_ru;

            case 8:
                return R.string.lang_ja;

            case 9:
                return R.string.lang_tr;

            default:
                return R.string.lang_en;
        }
    }

    public String getLocale() {
        switch (languageCode) {
            case 0:
                return "en";

            case 1:
                return "fr";

            case 2:
                return "de";

            case 3:
                return "it";

            case 4:
                return "es";

            case 5:
                return "pt_PT";

            case 6:
                return "pt";

            case 7:
                return "ru";

            case 8:
                return "ja";

            case 9:
                return "tr";

            default:
                return "en";
        }
    }

    public static ELanguage GetLanguage(int code) {
        for (ELanguage lang : values()) {
            if (lang.getLanguageCode() == code) {
                return lang;
            }
        }
        return null;
    }
}
