package io.dume.dume.teacher.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.dume.dume.R;
import io.dume.dume.teacher.mentor_settings.academic.AcademicActivity;
import io.dume.dume.teacher.mentor_settings.academic.AcademicContract;
import io.dume.dume.teacher.mentor_settings.academic.AcademicModel;
import io.dume.dume.teacher.mentor_settings.basicinfo.EditAccount;
import io.dume.dume.teacher.mentor_settings.basicinfo.EditModel;
import io.dume.dume.teacher.pojo.Academic;

public class AcAdapter extends RecyclerView.Adapter<AcAdapter.AcademicVH> {
    private Activity activity;
    private List<Academic> list;
    private Context context;
    int[] imageIcons = {
            R.drawable.academic_school,
            R.drawable.academic_college,
            R.drawable.academic_undergraduate,
            R.drawable.academic_postgraduate
    };

    public AcAdapter(Context context, List<Academic> list) {
        this.list = list;
        this.context = context;
        this.activity = (Activity) context;
    }

    @NonNull
    @Override
    public AcademicVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new AcademicVH(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_ac_adaper_row, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AcademicVH academicVH, int i) {
        Academic current = list.get(i);

        academicVH.menu.setImageResource(R.drawable.ic_more_vert_black_24dp);
        //testing my code here
        switch (current.getLevel()){
            case "School/O Level":
                academicVH.levelImage.setImageResource(imageIcons[0]);
                break;
            case "College/A Level":
                academicVH.levelImage.setImageResource(imageIcons[1]);
                break;
            case "Under Graduation":
                academicVH.levelImage.setImageResource(imageIcons[2]);
                break;
            case "Post Graduation":
                academicVH.levelImage.setImageResource(imageIcons[3]);
                break;
        }
        academicVH.title.setText(current.getInstitute());
        academicVH.subTitle.setText(current.getDegree());

        academicVH.menu.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(context, view);
            popup.getMenuInflater().inflate(R.menu.menu_edit_remove, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                switch (id) {
                    case R.id.action_remove:
                        AcademicModel model = AcademicModel.getInstance();
                        model.removeFromDatabase(list.get(i).getDegree(), new AcademicContract.Model.ModelCallback() {
                            @Override
                            public void onStart() {
                                Toast.makeText(context, "Item Removed", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess() {
                                //AcademicActivity.isDeleted = true;
                            }

                            @Override
                            public void onFail(String error) {
                              
                            }
                        });
                        break;
                    case R.id.action_edit:
                        final Intent intent = new Intent(context, AcademicActivity.class);
                        intent.setAction("edit");
                        Bundle bundle = new Bundle();
                        Academic hereCurrent = list.get(i);
                        bundle.putString("level", hereCurrent.getLevel());
                        bundle.putString("institution",hereCurrent.getInstitute());
                        bundle.putString("degree", hereCurrent.getDegree());
                        bundle.putString("from_year", hereCurrent.getFrom());
                        bundle.putString("to_year", hereCurrent.getTo());
                        bundle.putString("result", hereCurrent.getResult());
                        bundle.putInt("resultType", hereCurrent.getResultType());
                        intent.putExtra("academic_data", bundle);
                        context.startActivity(intent);
                        break;
                }
                return true;
            });
            popup.show();

        });

    }

    public int getRecyItemCount(){
        return list.size();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class AcademicVH extends RecyclerView.ViewHolder {
        @BindView(R.id.text_one)
        TextView title;
        @BindView(R.id.text_two)
        TextView subTitle;
        @BindView(R.id.more_vertical_icon)
        ImageView menu;
        @BindView(R.id.auto_image_icon)
        carbon.widget.ImageView levelImage;

        public AcademicVH(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
