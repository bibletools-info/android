package rawcomposition.bibletools.info.data.model

class ReferencesRequest {

    var book: Int = 0

    var chapter: Int = 0

    var verse: Int = 0

    constructor() {}

    constructor(book: Int, chapter: Int, verse: Int) {
        this.book = book
        this.chapter = chapter
        this.verse = verse
    }
}
