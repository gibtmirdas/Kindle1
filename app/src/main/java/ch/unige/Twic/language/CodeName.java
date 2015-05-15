package ch.unige.Twic.language;

/**
 * This object store the information of a "language" node contained in the XML file "languagelist" available at the address: "http://latlapps.unige.ch/Twicff?act=ll"
 *
 * Fields available:
 * <ul>
 *     <li>code: ISO 639-1 code name for the current language</li>
 *     <li>name: Name of the language to be displayed</li>
 * </ul>
 */
public class CodeName {
    private String code;
    private String name;

    public CodeName(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
