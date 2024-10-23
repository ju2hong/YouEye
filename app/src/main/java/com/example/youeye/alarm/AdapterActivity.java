// AdapterActivity.java
package com.example.youeye.alarm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.youeye.R;

import java.util.ArrayList;

public class AdapterActivity extends BaseAdapter {
    private ArrayList<Time> listviewItems;

    // 생성자에서 리스트를 받도록 수정
    public AdapterActivity(ArrayList<Time> listviewItems) {
        this.listviewItems = listviewItems;
    }

    @Override
    public int getCount() {
        return listviewItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listviewItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_theme, parent, false);
            holder = new ViewHolder();
            holder.am_pm = convertView.findViewById(R.id.am_pm);
            holder.hourText = convertView.findViewById(R.id.textTime1);
            holder.minuteText = convertView.findViewById(R.id.textTime2);
            holder.month = convertView.findViewById(R.id.time_month);
            holder.day = convertView.findViewById(R.id.time_day);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Time time = listviewItems.get(position);
        holder.am_pm.setText(time.getAm_pm());
        holder.hourText.setText(time.getHour() + "시");
        holder.minuteText.setText(time.getMinute() + "분");
        holder.month.setText(time.getMonth() + "월");
        holder.day.setText(time.getDay() + "일");

        return convertView;
    }

    public void addItem(int hour, int minute, String am_pm, String month, String day) {
        Time time = new Time();
        time.setHour(hour);
        time.setMinute(minute);
        time.setAm_pm(am_pm);
        time.setMonth(month);
        time.setDay(day);
        listviewItems.add(time);
        notifyDataSetChanged();
    }

    // 특정 위치의 아이템을 삭제하는 메서드 추가
    public void removeItem(int position) {
        if (position >= 0 && position < listviewItems.size()) {
            listviewItems.remove(position);
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        TextView am_pm, hourText, minuteText, month, day;
    }
}
