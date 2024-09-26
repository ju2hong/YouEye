// AdapterActivity
package com.example.youeye.alarm;

    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.BaseAdapter;
    import android.widget.TextView;

    import com.example.youeye.R;

    import java.util.ArrayList;

    public class AdapterActivity extends BaseAdapter {
        public ArrayList<Time> listviewitem = new ArrayList<Time>();
        private ArrayList<Time> arrayList = listviewitem;

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            //convertView가 null인지 확인하고 null이면 새로운 뷰 생성 후 viewHolder를 초기화 환다.
            if (convertView == null) {
                holder = new ViewHolder();

                // 레이아웃 인플레이터를 사용하여 list_theme 레이아웃을 인플레이트 한다.
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_theme, parent, false);

                // convertView에서 텍스트뷰를 찾아 viewHolder 필드에 저장한다.
                TextView hourText = (TextView) convertView.findViewById(R.id.textTime1);
                TextView minuteText = (TextView) convertView.findViewById(R.id.textTime2);
                TextView am_pm = (TextView) convertView.findViewById(R.id.am_pm);
                TextView month = (TextView) convertView.findViewById(R.id.time_month);
                TextView day = (TextView) convertView.findViewById(R.id.time_day);

                // viewHolder에 텍스트뷰 설정한다.
                holder.hourText = hourText;
                holder.minuteText = minuteText;
                holder.am_pm = am_pm;
                holder.month = month;
                holder.day = day;

                convertView.setTag(holder);
            } else {

                // convertView가 null이 아니면 태그에서 viewHolder 객체를 가져온다.
                holder = (ViewHolder) convertView.getTag();
            }

            Time time = arrayList.get(position);
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
            listviewitem.add(time);
            notifyDataSetChanged();
        }

        // List 삭제 method
        public void removeItem(int position) {
            if (listviewitem.size() < 1) {

            } else {
                listviewitem.remove(position);
                notifyDataSetChanged();
            }
        }

        public void removeItem() {
            if (listviewitem.size() < 1) {

            } else {
                listviewitem.remove(listviewitem.size() - 1);
                notifyDataSetChanged();
            }
        }

        // 새로운 메서드 추가
        public void updateList() {
            arrayList = new ArrayList<>(listviewitem);
            notifyDataSetChanged();
        }

        static class ViewHolder {
            TextView hourText, minuteText, am_pm, month, day;
        }
    }
