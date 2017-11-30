package by.bstu.fit.oleggutsev.lab8_9android;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.StudentViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Student item);
    }


    private final int MENU_REMOVE = 0;
    private final int MENU_CHANGE = 1;

    private List<Student> mList;
    private final OnItemClickListener listener;
    private Context mContext;

    public StudentsAdapter(Context context, List<Student> list, OnItemClickListener listener) {
        mContext = context;
        mList = list;
        this.listener = listener;
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_list_item, parent, false);

        return new StudentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final StudentViewHolder holder, int position) {
        Student student = mList.get(position);

        holder.mName.setText(student.getName() + " " + student.getSurname());
        holder.mMark.setText(student.getMark());
        Glide.with(mContext).load(student.getPhotoUri()).into(holder.mPhoto);
        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(holder.getAdapterPosition(), MENU_REMOVE,  0, "Удалить");
            }
        });

        holder.bind(mList.get(position), listener);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void updateList(List<Student> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mMark;
        ImageView mPhoto;
        CardView mCardView;

        public StudentViewHolder(View view) {
            super(view);

            mCardView = view.findViewById(R.id.cardView);
            mPhoto = view.findViewById(R.id.photo_imageView);
            mMark = view.findViewById(R.id.mark_textView);
            mName = view.findViewById(R.id.name_textView);

        }


        public void bind(final Student item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StudentsAdapter.this.listener.onItemClick(item);
                }
            });
        }
    }
}
