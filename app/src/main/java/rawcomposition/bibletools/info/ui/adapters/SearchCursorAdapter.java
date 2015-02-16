package rawcomposition.bibletools.info.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import rawcomposition.bibletools.info.model.QueryObject;
import rawcomposition.bibletools.info.ui.MainActivity;

/**
 * Created by tinashe on 2015/02/16.
 */
public class SearchCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    private List<QueryObject> queryObjects;

    private Activity activity;

    public SearchCursorAdapter(Activity context, Cursor c, int flags, List<QueryObject> queryObjects) {
        super(context, c, flags);

        mInflater = LayoutInflater.from(context);
        this.queryObjects = queryObjects;
        this.activity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        String text = cursor.getString(1);

        TextView textView = (TextView)view.findViewById(android.R.id.text1);
        textView.setText(text);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                QueryObject object = getSelectedObject(cursor);

                if(object != null){

                    int bookCode = object.getBookNum();
                    int chap = object.getChapter();

                    ((MainActivity)activity)
                            .performQuery(bookCode + " " + chap + " " + object.getVerse());
                }


            }
        });
    }

    private QueryObject getSelectedObject(Cursor cursor){
        for(QueryObject object: queryObjects){
            if(object.getDisplayText().equals(cursor.getString(1))){
                return object;
            }
        }

        return null;
    }
}
