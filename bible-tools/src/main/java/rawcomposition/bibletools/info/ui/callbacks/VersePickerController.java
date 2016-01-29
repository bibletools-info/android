package rawcomposition.bibletools.info.ui.callbacks;

import java.util.List;

/**
 * Created by tinashe on 2016/01/28.
 */
public interface VersePickerController {

    void onBookSelected(int book);

    void onChapterSelected(int chapter);

    void onVerseSelected(int verse);

    List<String> getBooks();

    List<String> getChapters();

    List<String> getVerses();
}
