package com.nojom.adapter;


import static com.nojom.util.Constants.BANK_TRANSFER_REVIEW;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.nojom.R;
import com.nojom.databinding.ItemCampaignInprogressBinding;
import com.nojom.fragment.projects.CampaignListFragment;
import com.nojom.model.CampList;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CampaignAdapter2 extends RecyclerView.Adapter<CampaignAdapter2.SimpleViewHolder> {

    private final CampaignListFragment context;
    private List<CampList> projectsList;
    private final PrettyTime p = new PrettyTime();
    private final BaseActivity activity;
    private SwipeLayout swipeLayout;
    private int adapterPosition;
    private final OnClickJobListener onClickJobListener;

    public CampaignAdapter2(CampaignListFragment context, OnClickJobListener onClickJobListener) {
        this.context = context;
        this.onClickJobListener = onClickJobListener;
        activity = (BaseActivity) context.getContext();
    }

   /* @Override
    public void onExpertSuccess(ExpertDetail expertDetail) {
        Preferences.writeString(activity, Constants.PLATFORM_ID, expertDetail.serviceId + "");
        Preferences.writeString(activity, Constants.PLATFORM_NAME, expertDetail.serviceName + "");
        ArrayList<ExpertLawyers.Data> expertUsers = new ArrayList<>();
        expertUsers.add(new ExpertLawyers.Data(expertDetail.profileId, expertDetail.firstName + " " + expertDetail.lastName));
        Preferences.setExpertUsers(activity, expertUsers);
        activity.gotoMainActivity(Constants.TAB_POST_JOB);
        getData().get(adapterPosition).isShowProgress = false;
        notifyItemChanged(adapterPosition);
    }*/

    /*@Override
    public void onExpertFail() {
        Preferences.setExpertUsers(activity, null);
        getData().get(adapterPosition).isShowProgress = false;
        notifyItemChanged(adapterPosition);
    }*/

//    @Override
//    public void onPreExpert() {
//        Preferences.setExpertUsers(activity, null);
//    }

    public void doRefresh(List<CampList> projectsList) {
        int curSize = getItemCount();
        this.projectsList = projectsList;
        notifyItemRangeInserted(curSize, projectsList.size() - 1);
    }

    public void initList(List<CampList> projectsList) {
        this.projectsList = projectsList;
        notifyDataSetChanged();
    }

    public List<CampList> getData() {
        return projectsList;
    }

    @NotNull
    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemCampaignInprogressBinding itemProjectsListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_campaign_inprogress, parent, false);
        return new SimpleViewHolder(itemProjectsListBinding);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        CampList item = projectsList.get(position);

        if (item.isShowProgress) {
//            holder.binding.rlItemview.setBackgroundResource(R.drawable.transp_rounded_corner_10);
//            holder.binding.progressBar.setVisibility(View.VISIBLE);
        } else {
//            holder.binding.rlItemview.setBackgroundResource(R.drawable.white_rounded_corner_10);
//            holder.binding.progressBar.setVisibility(View.GONE);
//            item.isShowProgress = false;
        }

        Glide.with(activity).load(item.client_profile_picture).into(holder.binding.imgProfile);

        if (item.client_name != null) {
            holder.binding.tvReceiverName.setText(item.client_name.en + " " + item.client_name.ar);
        } else if (!TextUtils.isEmpty(item.jp_title)) {
            holder.binding.tvReceiverName.setText(item.jp_title);
        }

        if (item.campaignId != null) {
            holder.binding.tvDate.setText(String.format(Locale.US, "#" + activity.getString(R.string.job_id)));
            holder.binding.tvCampId.setText(String.format(Locale.US, ":%s", item.campaignId));
        }

        if (item.star_details != null) {
            holder.binding.txtAgents.setText(item.services.size() + " " + activity.getString(R.string.stars));
        }
        if (!TextUtils.isEmpty(item.campaignBrief)) {
            holder.binding.txtPaid.setText(item.campaignBrief);
        }
        holder.binding.progressBar.setProgress((int) item.progress);
        holder.binding.txtPercent.setText(item.progress + "%");


//        if (item.job.contains("gig")) {
        if (item.getActualPrice() != 0 && item.getActualPrice() > 0) {
            holder.binding.txtPrice.setText(activity.getCurrency().equals("SAR") ? Utils.decimalFormat(String.valueOf(item.getActualPrice())) + "" : Utils.decimalFormat(String.valueOf(item.getActualPrice())) + "");
        } else {

            if (item.client_rate_id == 0 && item.budget != null && item.budget != 0) {
                holder.binding.txtPrice.setText(activity.getCurrency().equals("SAR") ? Utils.decimalFormat(String.valueOf(item.budget)) + "" : Utils.decimalFormat(String.valueOf(item.budget)) + "");
            } else {
                if (item.cr_id != null && item.cr_id != -1) {
                    if (item.range_to != null && item.range_from != null) {
                        if (activity.getCurrency().equals("SAR")) {
                            holder.binding.txtPrice.setText(String.format(Locale.US, activity.getString(R.string.s_sar_s_sar), Utils.decimalFormat(String.valueOf(item.range_from)), Utils.decimalFormat(String.valueOf(item.range_to))));
                        } else {
                            holder.binding.txtPrice.setText(String.format(Locale.US, "$%s - $%s", Utils.decimalFormat(String.valueOf(item.range_from)), Utils.decimalFormat(String.valueOf(item.range_to))));
                        }
                    } else if (item.range_from != null) {
                        if (activity.getCurrency().equals("SAR")) {
                            holder.binding.txtPrice.setText(Utils.decimalFormat(String.valueOf(item.range_from)));
                        } else {
                            holder.binding.txtPrice.setText(String.format(Locale.US, "$%s", Utils.decimalFormat(String.valueOf(item.range_from))));
                        }
                    } else if (item.budget != null) {
                        holder.binding.txtPrice.setText(activity.getCurrency().equals("SAR") ? Utils.decimalFormat(String.valueOf(item.budget)) + "" : "$" + Utils.decimalFormat(String.valueOf(item.budget)) + "");
                    }
                } else {
                    holder.binding.txtPrice.setText(activity.getString(R.string._0));
                }
            }
        }
        Date date1 = Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", item.timestamp);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dfFinal2;
        if (activity.language.equals("ar")) {
            dfFinal2 = new SimpleDateFormat("dd MMM,yyyy");
        } else {
            dfFinal2 = new SimpleDateFormat("MMM dd,yyyy");
        }


        if (date1 != null) {
            if (activity.printDifference(date1, date).equalsIgnoreCase("0")) {
                String result = p.format(Utils.changeDateFormat("yyyy-MM-dd'T'hh:mm:ss", item.timestamp));
                holder.binding.txtDate.setText("Due Date: " + result);
            } else {
                String finalDate = dfFinal2.format(date1);
                holder.binding.txtDate.setText("Due Date: " + finalDate);
            }
        }

        if (item.jps_id != null) {
            switch (item.jps_id) {
                case Constants.BIDDING:
//                    holder.binding.llRehire.setVisibility(View.GONE);
//                    holder.binding.llDelete.setVisibility(View.VISIBLE);
//                    holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.yellow_border_5));
//                    holder.binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.yellow));
//                    holder.binding.tvRefunds.setVisibility(View.GONE);
//                    holder.binding.llEdit.setVisibility(View.VISIBLE);
                    break;
//                case Constants.WAITING_FOR_AGENT_ACCEPTANCE:
//                    holder.binding.llEdit.setVisibility(View.GONE);
//                    holder.binding.llDelete.setVisibility(View.GONE);
//                    holder.binding.llRehire.setVisibility(View.GONE);
//                    holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.lovender_border_5));
//                    holder.binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.lovender));
//                    holder.binding.tvRefunds.setVisibility(View.GONE);
//                    break;
                case Constants.IN_PROGRESS:
//                    holder.binding.llEdit.setVisibility(View.GONE);
//                    holder.binding.llRehire.setVisibility(View.GONE);
//                    holder.binding.llDelete.setVisibility(View.GONE);
//                    holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.blue_border_pro_5));
//                    holder.binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
//                    if (item.jr_id != null) {//refund case
//                        holder.binding.tvRefunds.setVisibility(View.VISIBLE);
//                    } else {
//                        holder.binding.tvRefunds.setVisibility(View.GONE);
//                    }
                    break;
                case Constants.WAITING_FOR_DEPOSIT:
//                    holder.binding.llRehire.setVisibility(View.GONE);
//                    holder.binding.llEdit.setVisibility(View.GONE);
//                    holder.binding.llDelete.setVisibility(View.GONE);
//                    holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.red_border_5));
//                    holder.binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.reset_pw_btn));
//                    holder.binding.tvRefunds.setVisibility(View.GONE);
                    break;
                case Constants.SUBMIT_WAITING_FOR_PAYMENT:
//                    holder.binding.llRehire.setVisibility(View.GONE);
//                    holder.binding.llEdit.setVisibility(View.GONE);
//                    holder.binding.llDelete.setVisibility(View.GONE);
//                    holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.green_border_5));
//                    holder.binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.greendark));
//                    if (item.jr_id != null) {//refund case
//                        holder.binding.tvRefunds.setVisibility(View.VISIBLE);
//                    } else {
//                        holder.binding.tvRefunds.setVisibility(View.GONE);
//                    }
                    break;
                case Constants.COMPLETED:
//                    holder.binding.llRehire.setVisibility(View.VISIBLE);
//                    holder.binding.llEdit.setVisibility(View.GONE);
//                    holder.binding.llDelete.setVisibility(View.GONE);
//                    holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.green_border_5));
//                    holder.binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.greendark));
//                    if (item.jr_id != null) {//refund case
//                        holder.binding.tvRefunds.setVisibility(View.VISIBLE);
//                    } else {
//                        holder.binding.tvRefunds.setVisibility(View.GONE);
//                    }
                    break;
                case Constants.CANCELLED:
//                    holder.binding.llRehire.setVisibility(View.VISIBLE);
//                    holder.binding.llEdit.setVisibility(View.GONE);
//                    holder.binding.llDelete.setVisibility(View.GONE);
//                    holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.black_gray_border_5));
//                    holder.binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.gray_text));
//                    holder.binding.tvRefunds.setVisibility(View.GONE);
                    break;
                case Constants.REFUNDED:
//                    holder.binding.llRehire.setVisibility(View.VISIBLE);
//                    holder.binding.llEdit.setVisibility(View.GONE);
//                    holder.binding.llDelete.setVisibility(View.GONE);
//                    holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.orange_border_5));
//                    holder.binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.orange_light));
//                    holder.binding.tvRefunds.setVisibility(View.GONE);
                    break;
//                case Constants.REMOVED:
//                    holder.binding.llRehire.setVisibility(View.GONE);
//                    holder.binding.llEdit.setVisibility(View.GONE);
//                    holder.binding.llDelete.setVisibility(View.GONE);
//                    holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.black_border_5));
//                    holder.binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.black));
//                    holder.binding.tvRefunds.setVisibility(View.GONE);
//                    break;
//                case Constants.UNDER_REVIEW:
//                    holder.binding.llRehire.setVisibility(View.GONE);
//                    holder.binding.llEdit.setVisibility(View.GONE);
//                    holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.pink_border_5));
//                    holder.binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.pink_dark));
//                    holder.binding.tvRefunds.setVisibility(View.GONE);
//                    holder.binding.swipe.setSwipeEnabled(false);
//                    break;
                case BANK_TRANSFER_REVIEW:
//                    holder.binding.llRehire.setVisibility(View.GONE);
//                    holder.binding.llEdit.setVisibility(View.GONE);
//                    holder.binding.llDelete.setVisibility(View.GONE);
//                    holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.orange_border_5));
//                    holder.binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.orange_light));
//                    holder.binding.tvRefunds.setVisibility(View.GONE);
                    break;
            }
        } else {

            if ((item.type).equals("paid")) {
//                holder.binding.llEdit.setVisibility(View.GONE);
//                holder.binding.llRehire.setVisibility(View.GONE);
//                holder.binding.llDelete.setVisibility(View.GONE);
//                holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.blue_border_pro_5));
//                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
            } else {
//                holder.binding.llRehire.setVisibility(View.VISIBLE);
//                holder.binding.llEdit.setVisibility(View.GONE);
//                holder.binding.llDelete.setVisibility(View.GONE);
//                holder.binding.tvStatus.setBackground(ContextCompat.getDrawable(activity, R.drawable.black_gray_border_5));
//                holder.binding.tvStatus.setTextColor(ContextCompat.getColor(activity, R.color.gray_text));
            }
        }


        if (item.campaignStatus != null && !TextUtils.isEmpty(item.campaignStatus)) {
//            holder.binding.tvStatus.setText(capitalizeWords(item.campaignStatus));
        } else if (item.name != null) {
//            holder.binding.tvStatus.setText(item.getStatusName(activity));

        }
    }

    @Override
    public int getItemCount() {
        return projectsList != null ? projectsList.size() : 0;
    }


//    private void showDeleteDialog(final int projectId, final SwipeLayout swipeLayout, final int position) {
//        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
//        dialog.setTitle(null);
//        dialog.setContentView(R.layout.dialog_delete_project);
//        dialog.setCancelable(true);
//
//        TextView tvMessage = dialog.findViewById(R.id.tv_message);
//        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
//        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);
//
//        tvMessage.setText(Utils.fromHtml(context.getString(R.string.delete_project_text)));
//
//        tvCancel.setText(context.getString(R.string.no));
//        tvChatnow.setText(context.getString(R.string.yes));
//        tvCancel.setOnClickListener(v -> dialog.dismiss());
//
//        tvChatnow.setOnClickListener(v -> {
//            dialog.dismiss();
//            deleteProject(projectId, swipeLayout, position);
//        });
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
//        lp.gravity = Gravity.CENTER;
//        dialog.show();
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().setAttributes(lp);
//    }


    public interface OnClickJobListener {
        void onClickJob(int jpId, int position, String jobType, String gigType, CampList campList);
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        ItemCampaignInprogressBinding binding;

        SimpleViewHolder(ItemCampaignInprogressBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            itemView.getRoot().setOnClickListener(view -> {
                if (onClickJobListener != null) {
                    onClickJobListener.onClickJob(0, getAdapterPosition(), "", "", projectsList.get(getAdapterPosition()));
                }
            });
        }
    }

    public static String capitalizeWords(String input) {
        // split the input string into an array of words
        String[] words = input.split("\\s");

        // StringBuilder to store the result
        StringBuilder result = new StringBuilder();

        // iterate through each word
        for (String word : words) {
            // capitalize the first letter, append the rest of the word, and add a space
            result.append(Character.toTitleCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }

        // convert StringBuilder to String and trim leading/trailing spaces
        return result.toString().trim();
    }
}
