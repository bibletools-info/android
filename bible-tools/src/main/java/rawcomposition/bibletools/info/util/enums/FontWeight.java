package rawcomposition.bibletools.info.util.enums;

/**
 * Created by tinashe on 2015/04/10.
 */
public enum FontWeight {

    LIGHT("light", "Lato-Light.ttf"),
    REGULAR("regular", "Lato-Regular.ttf"),
    MEDIUM("medium", "Lato-Medium.ttf"),
    HEAVY("heavy", "Lato-Heavy.ttf"),
    ITALIC("italic", "Lato-Italic.ttf");

    private String name;
    private String fileName;

    FontWeight(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }
}
