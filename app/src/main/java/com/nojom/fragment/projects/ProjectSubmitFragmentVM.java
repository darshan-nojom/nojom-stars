package com.nojom.fragment.projects;

import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.adapter.RecyclerviewAdapter;
import com.nojom.adapter.SubmitFilesAdapter;
import com.nojom.api.APIRequest;
import com.nojom.databinding.FragmentProjectSubmitBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ProjectByID;
import com.nojom.model.requestmodel.CommonRequest;
import com.nojom.ui.projects.ProjectDetailsActivity;
import com.nojom.ui.projects.SubmitJobActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

import static com.nojom.util.Constants.API_DELETE_FILES;
import static com.nojom.util.Constants.API_SEND_FILE_MAIL;

class ProjectSubmitFragmentVM extends AndroidViewModel implements RecyclerviewAdapter.OnViewBindListner, APIRequest.APIRequestListener {
    private FragmentProjectSubmitBinding binding;
    private BaseFragment fragment;
    private ProjectByID projectData;
    private List<ProjectByID.Files> filesList;
    private RecyclerviewAdapter fileAdapter;
    private int selectedPos = -1;

    ProjectSubmitFragmentVM(Application application, FragmentProjectSubmitBinding projectSubmitBinding, BaseFragment projectSubmitFragment) {
        super(application);
        binding = projectSubmitBinding;
        fragment = projectSubmitFragment;
        initData();
    }

    public void initData() {
        try {
            if (fragment.activity != null) {
                projectData = ((ProjectDetailsActivity) fragment.activity).getProjectData();
            }

            if (projectData != null) {
                filesList = projectData.submittedFiles;

                if (projectData.jobPostStateId == (Constants.COMPLETED)) {
                    binding.tvSubmitJob.setVisibility(View.GONE);
                    binding.imgAdd.setVisibility(View.GONE);

                    if (filesList == null || filesList.size() == 0) {
                        binding.tvPlaceholderMsg.setVisibility(View.VISIBLE);
                        binding.tvPlaceholderTitle.setVisibility(View.VISIBLE);
                    }
                }
            }

            binding.rvSubmitJobs.setLayoutManager(new LinearLayoutManager(fragment.activity));

            if (filesList != null) {
                fileAdapter = new RecyclerviewAdapter((ArrayList<?>) filesList, R.layout.item_files_desc, this);
                binding.rvSubmitJobs.setAdapter(fileAdapter);
            }

            binding.imgAdd.setOnClickListener(view -> {
                if (projectData != null && projectData.jobPostBids != null) {

                    if (projectData.refundStatus != null && (projectData.refundStatus.equals("0")
                            || projectData.refundStatus.equals("1") || projectData.refundStatus.equals("3"))) {
                        Toast.makeText(fragment.activity, fragment.getString(R.string.refund_status_message), Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent i = new Intent(fragment.activity, SubmitJobActivity.class);
                    i.putExtra(Constants.PROJECT_BID_ID, String.valueOf(projectData.jobPostBids.id));
                    fragment.startActivity(i);
                }
            });

            binding.tvSubmitJob.setOnClickListener(view -> {
                if (projectData != null && projectData.id != null) {

                    if (projectData.refundStatus != null && (projectData.refundStatus.equals("0")
                            || projectData.refundStatus.equals("1") || projectData.refundStatus.equals("3"))) {
                        Toast.makeText(fragment.activity, fragment.getString(R.string.refund_status_message), Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent i = new Intent(fragment.activity, SubmitJobActivity.class);
                    i.putExtra(Constants.PROJECT_BID_ID, String.valueOf(projectData.id));
                    fragment.startActivity(i);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isShowProgress;
    private int selectedFileAdapterPos;

    @Override
    public void bindView(View view, int position) {
        ProjectByID.Files item = filesList.get(position);

        TextView tvDate = view.findViewById(R.id.tv_date);
        TextView tvTimeLeft = view.findViewById(R.id.tv_time_left);
        ImageView imgDelete = view.findViewById(R.id.img_delete);
        TextView tvDesc = view.findViewById(R.id.tv_description);
        RecyclerView rvFiles = view.findViewById(R.id.rv_files);
        TextView tvClientEmail = view.findViewById(R.id.tv_client_email);
        CircularProgressBar progressBar = view.findViewById(R.id.progress_bar);

        if (isShowProgress) {
            progressBar.setVisibility(View.VISIBLE);
            tvClientEmail.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            tvClientEmail.setVisibility(View.VISIBLE);
        }

        rvFiles.setLayoutManager(new LinearLayoutManager(fragment.activity));

        tvDate.setText(Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", "MMM dd, yyyy h:mm a", item.timestamp));
        try {
            if (item.timer != null && item.timer.days != null
                    && item.timer.hours != null && item.timer.minutes != null
                    && item.timer.seconds != null) {
                String day = item.timer.days > 1 ? item.timer.days + " Days" : item.timer.days + " Day";
                String hours = item.timer.hours > 1 ? item.timer.hours + " Hours" : item.timer.hours + " Hour";
                String mins = item.timer.minutes > 1 ? item.timer.minutes + " Minutes" : item.timer.minutes + " Minute";
                String sec = item.timer.seconds > 1 ? item.timer.seconds + " Seconds" : item.timer.seconds + " Second";
                if (item.timer.days > 0) {
                    tvTimeLeft.setText(String.format("%s %s Left", day, hours));
                } else if (item.timer.hours > 0) {
                    tvTimeLeft.setText(String.format("%s %s Left", hours, mins));
                } else if (item.timer.minutes > 0) {
                    tvTimeLeft.setText(String.format("%s %s Left", mins, sec));
                } else {
                    tvTimeLeft.setText(String.format("%s Left", sec));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvDesc.setText(item.description);

        imgDelete.setOnClickListener(view1 -> {
            showDeleteDialog(item.id, position);
        });

        tvClientEmail.setOnClickListener(view12 -> {
            selectedFileAdapterPos = position;
            emailFile(item.id);
        });

        SubmitFilesAdapter adapter = new SubmitFilesAdapter(fragment.activity, (ArrayList<ProjectByID.FileList>) item.fileList);
        rvFiles.setAdapter(adapter);
    }

    public void notifyAdapter(boolean showProgress) {
        isShowProgress = showProgress;
        fragment.activity.disableEnableTouch(showProgress);
        if (fileAdapter != null) {
            fileAdapter.notifyItemChanged(selectedFileAdapterPos);
        }
    }

    private void emailFile(int id) {
        if (!fragment.activity.isNetworkConnected()) {
            notifyAdapter(false);
            return;
        }


        notifyAdapter(true);

        CommonRequest.SendFileMail sendFileMail = new CommonRequest.SendFileMail();
        sendFileMail.setFile_attribute_id(id);

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(fragment.activity, API_SEND_FILE_MAIL, sendFileMail.toString(), true, this);

    }

    private void deleteFile(int id, int position) {
        if (!fragment.activity.isNetworkConnected())
            return;

//        fragment.activity.showProgress();

        CommonRequest.DeleteFiles deleteFiles = new CommonRequest.DeleteFiles();
        deleteFiles.setFile_attribute_id(id);
        selectedPos = position;

        APIRequest apiRequest = new APIRequest();
        apiRequest.makeAPIRequest(fragment.activity, API_DELETE_FILES, deleteFiles.toString(), true, this);
    }

    private void showDeleteDialog(int id, int position) {
        final Dialog dialog = new Dialog(fragment.activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_delete_project);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        tvMessage.setText("Are you sure want to delete job?");

        tvCancel.setText(fragment.activity.getString(R.string.no));
        tvChatnow.setText(fragment.activity.getString(R.string.yes));
        tvCancel.setOnClickListener(v -> dialog.dismiss());

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            deleteFile(id, position);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onResponseSuccess(String decryptedData, String urlEndPoint, String msg) {
        if (selectedPos != -1) {
            filesList.remove(selectedPos);
            fileAdapter.removeItem(selectedPos);
        }
        if (urlEndPoint.equalsIgnoreCase(API_SEND_FILE_MAIL)) {
            fragment.activity.toastMessage(msg);
            notifyAdapter(false);
        }

//        fragment.activity.hideProgress();
        selectedPos = -1;
    }

    @Override
    public void onResponseError(Throwable t, String urlEndPoint, String message) {
//        fragment.activity.hideProgress();
        selectedPos = -1;
        if (urlEndPoint.equalsIgnoreCase(API_SEND_FILE_MAIL)) {
            notifyAdapter(false);
        }
    }
}
