package com.nojom.ui.gigs;

import static com.nojom.util.Constants.API_DELETE_CONTRACT_JOB;
import static com.nojom.util.Constants.API_EMAIL_CONTRACT_JOB;

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
import com.nojom.adapter.SubmitGigFilesAdapter;
import com.nojom.api.APIRequest;
import com.nojom.databinding.FragmentProjectSubmitBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ContractDetails;
import com.nojom.model.ProjectByID;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

class ContractSubmitFragmentVM extends AndroidViewModel implements RecyclerviewAdapter.OnViewBindListner, APIRequest.JWTRequestResponseListener {
    private FragmentProjectSubmitBinding binding;
    private BaseFragment fragment;
    private ContractDetails projectData;
    private List<ProjectByID.Files> filesList;
    private RecyclerviewAdapter fileAdapter;
    private int selectedPos = -1;

    ContractSubmitFragmentVM(Application application, FragmentProjectSubmitBinding projectSubmitBinding, BaseFragment projectSubmitFragment) {
        super(application);
        binding = projectSubmitBinding;
        fragment = projectSubmitFragment;
        initData();
    }

    public void initData() {
        try {
            if (fragment.activity != null) {
                projectData = ((ContractDetailsActivity) fragment.activity).getProjectData();
            }

            if (projectData != null) {
                filesList = projectData.submittedFiles;

                if (projectData.gigStateID == (Constants.COMPLETED)) {
                    binding.tvSubmitJob.setVisibility(View.GONE);
                    binding.imgAdd.setVisibility(View.GONE);

                    if (filesList != null && filesList.size() == 0) {
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
//                if (projectData != null && projectData.jobPostBids != null) {

                if(projectData.refundStatus!=null&& (projectData.refundStatus.equals("0")
                        ||projectData.refundStatus.equals("1")))
                {
                    Toast.makeText(fragment.activity, fragment.getString(R.string.refund_status_message), Toast.LENGTH_LONG).show();
                    return;
                }

                Intent i = new Intent(fragment.activity, SubmitContractJobActivity.class);
                i.putExtra(Constants.PROJECT_BID_ID, projectData.id);
                i.putExtra("clientid", projectData.clientDetails.clientID);
                fragment.startActivity(i);
//                }
            });

            binding.tvSubmitJob.setOnClickListener(view -> {
//                if (projectData != null && projectData.jobPostBids != null) {

                if(projectData.refundStatus!=null&& (projectData.refundStatus.equals("0")
                        ||projectData.refundStatus.equals("1")))
                {
                    Toast.makeText(fragment.activity, fragment.getString(R.string.refund_status_message), Toast.LENGTH_LONG).show();
                    return;
                }

                Intent i = new Intent(fragment.activity, SubmitContractJobActivity.class);
                i.putExtra(Constants.PROJECT_BID_ID, projectData.id);
                i.putExtra("clientid", projectData.clientDetails.clientID);
                fragment.startActivity(i);
//                }
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
        if(fragment.activity.language.equals("ar")){
            tvDate.setText(Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", "dd MMM, yyyy h:mm a", item.timestamp));
        }else{
            tvDate.setText(Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", "MMM dd, yyyy h:mm a", item.timestamp));    
        }
        
        try {
            if (item.timer != null && item.timer.days != null
                    && item.timer.hours != null && item.timer.minutes != null
                    && item.timer.seconds != null) {
                String day = item.timer.days > 1 ? item.timer.days + " " + fragment.activity.getString(R.string.days) : item.timer.days + " " + fragment.activity.getString(R.string.day);
                String hours = item.timer.hours > 1 ? item.timer.hours + " " + fragment.activity.getString(R.string.hours) : item.timer.hours + " " + fragment.activity.getString(R.string.hour);
                String mins = item.timer.minutes > 1 ? item.timer.minutes + " " + fragment.getString(R.string.minutes) : item.timer.minutes + fragment.getString(R.string.minute);
                String sec = item.timer.seconds > 1 ? item.timer.seconds + " " + fragment.getString(R.string.seconds) : item.timer.seconds + " " + fragment.getString(R.string.second);
                if (item.timer.days > 0) {
                    tvTimeLeft.setText(String.format("%s %s " + fragment.getString(R.string.left), day, hours));
                } else if (item.timer.hours > 0) {
                    tvTimeLeft.setText(String.format("%s %s " + fragment.getString(R.string.left), hours, mins));
                } else if (item.timer.minutes > 0) {
                    tvTimeLeft.setText(String.format("%s %s " + fragment.getString(R.string.left), mins, sec));
                } else {
                    tvTimeLeft.setText(String.format("%s " + fragment.getString(R.string.left), sec));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvDesc.setText(item.description);

        imgDelete.setOnClickListener(view1 -> showDeleteDialog(item.id, position));

        tvClientEmail.setOnClickListener(view12 -> {
            selectedFileAdapterPos = position;
            emailFile(item.id);
        });

        SubmitGigFilesAdapter adapter = new SubmitGigFilesAdapter(fragment.activity, (ArrayList<ProjectByID.FileList>) item.fileList, projectData.agentSubmittedPath);
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

        HashMap<String, RequestBody> map = new HashMap<>();
        RequestBody contarctId = RequestBody.create(String.valueOf(projectData.id), MultipartBody.FORM);
        RequestBody fileId = RequestBody.create("" + id, MultipartBody.FORM);
        map.put("contractID", contarctId);
        map.put("agentFilesAttributesID", fileId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestBodyJWT(this, fragment.activity, API_EMAIL_CONTRACT_JOB, map);

    }

    private void deleteFile(int id, int position) {
        if (!fragment.activity.isNetworkConnected())
            return;

        selectedPos = position;

        HashMap<String, RequestBody> map = new HashMap<>();
        RequestBody contarctId = RequestBody.create(String.valueOf(projectData.id), MultipartBody.FORM);
        RequestBody fileId = RequestBody.create("" + id, MultipartBody.FORM);
        map.put("contractID", contarctId);
        map.put("agentFilesAttributesID", fileId);

        APIRequest apiRequest = new APIRequest();
        apiRequest.apiRequestBodyJWT(this, fragment.activity, API_DELETE_CONTRACT_JOB, map);
    }

    private void showDeleteDialog(int id, int position) {
        final Dialog dialog = new Dialog(fragment.activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_delete_project);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        tvMessage.setText(R.string.are_you_sure_want_to_delete_job);

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
    public void successResponseJWT(String responseBody, String url, String message, String data) {
        if (selectedPos != -1) {
            filesList.remove(selectedPos);
            fileAdapter.removeItem(selectedPos);
        }
        if (url.equalsIgnoreCase(API_EMAIL_CONTRACT_JOB)) {
            fragment.activity.toastMessage(message);
            notifyAdapter(false);
        }
        selectedPos = -1;
    }

    @Override
    public void failureResponseJWT(Throwable throwable, String url, String message) {
        selectedPos = -1;
        if (url.equalsIgnoreCase(API_EMAIL_CONTRACT_JOB)) {
            notifyAdapter(false);
        }
    }
}
