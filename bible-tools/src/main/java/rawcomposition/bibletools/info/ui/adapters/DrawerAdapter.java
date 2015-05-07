package rawcomposition.bibletools.info.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import rawcomposition.bibletools.info.R;

/**
 * Created by tinashe on 2015/02/19.
 */
public class DrawerAdapter extends BaseAdapter {

    private String[] mList;

    private LayoutInflater mInflater;

    public DrawerAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);

        this.mList = context.getResources().getStringArray(R.array.drawer_array);
    }

    @Override
    public int getCount() {
        return mList.length + 1;
    }

    @Override
    public String getItem(int position) {
        return mList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (position == 3) {
            return mInflater.inflate(R.layout.drawer_divider, parent, false);
        }

        if (position > 3) {
            TextView textView = (TextView) mInflater.inflate(R.layout.drawer_text_item, parent, false);
            textView.setText(getItem(position - 1));
            return textView;
        }

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.drawer_item, parent, false);
            holder.icon = (ImageView) convertView.findViewById(R.id.drawer_icon);
            holder.textView = (TextView) convertView.findViewById(R.id.drawer_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(getItem(position));
        switch (position) {
            case 0:
                holder.icon.setImageResource(R.drawable.ic_home);
                break;
            case 1:
                holder.icon.setImageResource(R.drawable.ic_favorite_grey);
                break;
            case 2:
                holder.icon.setImageResource(R.drawable.ic_history_grey);
                break;
        }
        return convertView;
    }

    class ViewHolder {
        ImageView icon;
        TextView textView;
    }
}
