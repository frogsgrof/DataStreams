import java.io.File;

public class SearchManager {

    File file = null;
    StringBuilder search = new StringBuilder();

    public SearchManager() {

    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean updateQuery(String search) {
        if (search == null || search.isEmpty() || search.length() > 50) return false;
        this.search.replace(0, search.length(), search);
        return true;
    }

    public File getFile() {
        return file;
    }

    public String getSearch() {
        return search.toString();
    }
}
