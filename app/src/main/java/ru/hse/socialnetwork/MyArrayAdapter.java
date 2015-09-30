package ru.hse.socialnetwork;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] names;

    public MyArrayAdapter(Activity context, String[] names) {
        super(context, R.layout.row, names);
        this.context = context;
        this.names = names;
    }

    // Класс для сохранения во внешний класс и для ограничения доступа
    // из потомков класса
    static class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ViewHolder буферизирует оценку различных полей шаблона элемента

        ViewHolder holder;
        // Очищает сущетсвующий шаблон, если параметр задан
        // Работает только если базовый шаблон для всех классов один и тот же
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.row, null, true);
            holder = new ViewHolder();
            holder.textView = (TextView) rowView.findViewById(R.id.label);
            holder.imageView = (ImageView) rowView.findViewById(R.id.icon);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.textView.setText(names[position]);
        // Изменение иконки для Windows и iPhone
        String s = names[position];
        switch(names[position])
        {
            case "Ilya":
                holder.imageView.setImageResource(R.drawable.ilya);
                break;
            case "Polly":
                holder.imageView.setImageResource(R.drawable.polly);
                break;
            case "Anton":
                holder.imageView.setImageResource(R.drawable.anton);
                break;
            case "Ksenia":
                holder.imageView.setImageResource(R.drawable.ksenia);
                break;
            default:
                holder.imageView.setImageResource(R.drawable.andr);
                break;
        }
        return rowView;
    }
}
