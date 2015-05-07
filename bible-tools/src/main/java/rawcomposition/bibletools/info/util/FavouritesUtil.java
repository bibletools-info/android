package rawcomposition.bibletools.info.util;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rawcomposition.bibletools.info.model.FavouriteVerse;
import rawcomposition.bibletools.info.util.enums.ViewType;

/**
 * Created by tinashe on 2015/03/05.
 */
public class FavouritesUtil {

    public static void addFavourite(Realm realm, final String code, final String text) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                FavouriteVerse verse = realm.createObject(FavouriteVerse.class);
                verse.setVerseCode(code);
                verse.setVerseText(text);
            }
        });
    }

    public static void unFavourite(Realm realm, String code) {
        final FavouriteVerse verse = realm.where(FavouriteVerse.class)
                .beginGroup()
                .equalTo("verseCode", code)
                .endGroup()
                .findFirst();

        if (verse != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    verse.removeFromRealm();
                }
            });
        }
    }

    public static boolean isInFavourites(Realm realm, String code) {
        FavouriteVerse verse = realm.where(FavouriteVerse.class)
                .beginGroup()
                .equalTo("verseCode", code)
                .endGroup()
                .findFirst();


        return verse != null;
    }

    public static List<FavouriteVerse> queryFavourites(Realm realm, String query) {

        return realm.where(FavouriteVerse.class)
                .beginGroup()
                .contains("verseCode", query, false)
                .endGroup()
                .findAll();
    }

    public static List<FavouriteVerse> getAllFavourites(Realm realm) {
        return realm.where(FavouriteVerse.class)
                .findAll();
    }

    public static List<FavouriteVerse> filterList(Context context, Realm realm, ViewType viewType) {
        List<FavouriteVerse> verses = getAllFavourites(realm);
        List<FavouriteVerse> filtered;

        switch (viewType) {
            case OLD_TESTAMENT:
                filtered = new ArrayList<>();
                for (FavouriteVerse verse : verses) {
                    if (!BibleQueryUtil.isNewTestament(context, verse.getVerseCode())) {
                        filtered.add(verse);
                    }
                }

                return filtered;
            case NEW_TESTAMENT:
                filtered = new ArrayList<>();
                for (FavouriteVerse verse : verses) {
                    if (BibleQueryUtil.isNewTestament(context, verse.getVerseCode())) {
                        filtered.add(verse);
                    }
                }

                return filtered;
            default:
                return verses;
        }
    }
}
