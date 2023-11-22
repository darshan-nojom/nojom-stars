package com.nojom.ui.gigs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.adapter.MyGigsAdapter;
import com.nojom.apis.GigDetailAPI;
import com.nojom.databinding.FragmentMyGigsListBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.GigDetails;
import com.nojom.model.GigList;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.EndlessRecyclerViewScrollListener;
import com.nojom.util.Utils;

import java.util.Objects;

public class ActiveGigsFragment extends BaseFragment implements MyGigsAdapter.GigClickListener {

    private FragmentMyGigsListBinding binding;
    private EndlessRecyclerViewScrollListener scrollListener;
    private ActiveGigsFragmentVM activeGigsFragmentVM;
    private MyGigsAdapter activeAdapter;
    private int selectedScreen;
    private boolean isClickOnEdit, isClickDuplicate;
    private GigDetailAPI gigDetailAPI;

    public static ActiveGigsFragment newInstance(int screen) {
        ActiveGigsFragment fragment = new ActiveGigsFragment();
        Bundle args = new Bundle();
        args.putInt("screen", screen);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_gigs_list, container, false);
        if (getArguments() != null) {
            selectedScreen = getArguments().getInt("screen", 1);
        }

        Log.e("Selected Screen", "================== " + selectedScreen);
        activeGigsFragmentVM = ViewModelProviders.of(this).get(ActiveGigsFragmentVM.class);
        activeGigsFragmentVM.init(ActiveGigsFragment.this);
        gigDetailAPI = new GigDetailAPI();
        gigDetailAPI.init(activity);

        initData();
        Utils.trackFirebaseEvent(activity, "Gig_Active_Screen");
        return binding.getRoot();
    }

    private void initData() {

        if (selectedScreen == 1) {
            setPlaceHolder(getString(R.string.no_active_gig), getString(R.string.all_active_gig_displayed_here));
        } else if (selectedScreen == 2) {
            setPlaceHolder(getString(R.string.no_draft_gig), getString(R.string.all_draft_gig));
        } else {
            setPlaceHolder(getString(R.string.no_pause_gig), getString(R.string.all_pause_gig));
        }

        binding.rvGigs.setLayoutManager(new LinearLayoutManager(activity));

        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        binding.rvGigs.setLayoutManager(linearLayoutManager);

        //scroll more listener
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 9) {
//                    ((MyProjectsActivity) activity).showHideHorizontalProgress(View.VISIBLE);
                    activeGigsFragmentVM.getPageNoData().setValue(page);
                    activeGigsFragmentVM.getActiveGig(false, selectedScreen);
                }
            }
        };
        //swipe refresh
        binding.swipeRefreshLayout.setOnRefreshListener(this::refresh);

        activeGigsFragmentVM.getIsShowProgress().observe(activity, state -> {
            activity.disableEnableTouch(true);
            if (state == 1) {
                binding.rvGigs.setVisibility(View.INVISIBLE);
                binding.noData.llNoData.setVisibility(View.GONE);
                binding.shimmerLayout.startShimmer();
                binding.shimmerLayout.setVisibility(View.VISIBLE);
            } else if (state == 7) {//gig delete
                if (selectedAdapterPos != -1) {
                    String isNew = activeAdapter.getData().get(selectedAdapterPos).isNew;
                    updateAdapterState(true, isNew);
                }
            }
        });

        activeGigsFragmentVM.getIsHideProgress().observe(activity, state -> {
            activity.disableEnableTouch(false);
            if (state == 2) {//details
                updateAdapterState(false, "0");
            } else if (state == 7) {//gig delete
                if (selectedAdapterPos != -1) {
                    try {
                        if (activeAdapter.getData() != null && activeAdapter.getData().size() > 0) {
                            String isNew = activeAdapter.getData().get(selectedAdapterPos).isNew;
                            updateAdapterState(false, isNew);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                binding.rvGigs.setVisibility(View.VISIBLE);
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
                if (activeAdapter == null || activeAdapter.getItemCount() == 0) {
                    binding.noData.llNoData.setVisibility(View.VISIBLE);
                } else {
                    binding.noData.llNoData.setVisibility(View.GONE);
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        activeGigsFragmentVM.getIsHideProgress().observe(activity, this::updateProgress);

        gigDetailAPI.getIsHideProgress().observe(activity, this::updateProgress);

        activeGigsFragmentVM.getGigList().observe(activity, data -> bindAdapter(data.gigImagesPath));

        //call to get data from server
        activeGigsFragmentVM.getActiveGig(true, selectedScreen);

        activeGigsFragmentVM.getGigDetailsMutableLiveData().observe(activity, this::detailsStep);

        gigDetailAPI.getGigDetailsMutableLiveData().observe(activity, this::detailsStep);

        activeGigsFragmentVM.getIsDeleteGigSuccess().observe(activity, aBoolean -> adapterItemRemoved());

    }

    private void updateProgress(Integer state) {
        activity.disableEnableTouch(false);
        if (state == 2) {//details
            updateAdapterState(false, "0");
        }
    }

    private void detailsStep(GigDetails gigDetails) {
        if (isClickOnEdit) {
            Intent i;
            if (gigDetails.gigType.equalsIgnoreCase("1")) {//custom gig
                i = new Intent(activity, CreateCustomGigsActivityCopy.class);
//                i = new Intent(activity, CreateCustomGigsActivityCopy2.class);
            } else {//standard gig
                i = new Intent(activity, CreateGigsActivity.class);
            }
            i.putExtra(Constants.SCREEN_TAB, selectedScreen);
            i.putExtra(Constants.GIG_DETAILS, gigDetails);
            startActivityForResult(i, 121);
            isClickOnEdit = false;
        } else if (isClickDuplicate) {
            Intent i;
            if (gigDetails.gigType.equalsIgnoreCase("1")) {//custom gig
                i = new Intent(activity, CreateCustomGigsActivityCopy.class);
//                i = new Intent(activity, CreateCustomGigsActivityCopy2.class);
            } else {//standard gig
                i = new Intent(activity, CreateGigsActivity.class);
            }
            i.putExtra(Constants.SCREEN_TAB, selectedScreen);
            i.putExtra(Constants.GIG_DETAILS, gigDetails);
            i.putExtra("isDuplicate", true);
            startActivityForResult(i, 121);
            isClickDuplicate = false;
        } else {
            Intent i = new Intent(getActivity(), MyGigJobsActivity.class);
            i.putExtra(Constants.SCREEN_TAB, selectedScreen);
            i.putExtra(Constants.GIG_DETAILS, gigDetails);
            startActivity(i);
        }
    }

    private void setPlaceHolder(String title, String desc) {
        binding.noData.tvNoTitle.setText(title);
        binding.noData.tvNoDescription.setText(desc);
    }

    private void updateAdapterState(boolean isShow, String isNew) {
        if (activeAdapter != null) {
            activity.runOnUiThread(() -> {
                activeAdapter.getData().get(selectedAdapterPos).isShowProgress = isShow;
                activeAdapter.getData().get(selectedAdapterPos).isNew = isNew;
                activeAdapter.notifyItemChanged(selectedAdapterPos);
            });
        }
    }

    private void adapterItemRemoved() {
        if (activeAdapter != null) {
            activity.runOnUiThread(() -> {
                activeAdapter.getData().remove(selectedAdapterPos);
                activeAdapter.notifyItemRemoved(selectedAdapterPos);
            });
        }
    }

    public void refresh() {
        if (activeGigsFragmentVM != null) {
            activeGigsFragmentVM.getPageNoData().setValue(1);
            scrollListener.resetState();
            activeAdapter = null;
            activeGigsFragmentVM.getActiveGig(true, selectedScreen);
        }
    }

    private void bindAdapter(String filePath) {

        if (activeGigsFragmentVM.getGigDataList().getValue() != null && activeGigsFragmentVM.getGigDataList().getValue().size() > 0) {
            binding.noData.llNoData.setVisibility(View.GONE);
            if (activeAdapter == null) {
                activeAdapter = new MyGigsAdapter(activity, activeGigsFragmentVM.getGigDataList().getValue(), filePath, this);
                binding.rvGigs.setAdapter(activeAdapter);
            } else {
                activeAdapter.doRefresh(activeGigsFragmentVM.getGigDataList().getValue());
            }
        } else {
            if (activeAdapter == null || activeAdapter.getItemCount() == 0) {
                binding.noData.llNoData.setVisibility(View.VISIBLE);
            }
        }

        binding.swipeRefreshLayout.setRefreshing(false);
//        projectsListFragmentVM.getIsProgress().setValue(true);
//        ((MyProjectsActivity) activity).showHideHorizontalProgress(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (scrollListener != null)
            binding.rvGigs.addOnScrollListener(scrollListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scrollListener != null)
            binding.rvGigs.removeOnScrollListener(scrollListener);
    }

    private int selectedAdapterPos = 0;

    @Override
    public void onClickGig(GigList.Data data, int pos) {
        selectedAdapterPos = pos;
        activity.disableEnableTouch(true);

        if (data.gigType.equalsIgnoreCase("1") || data.gigType.equals("3")) {
            gigDetailAPI.getCustomGigDetails(data.id);
        } else {
            gigDetailAPI.getGigDetails(data.id);
        }

    }

    @Override
    public void onClickDeleteGig(GigList.Data data, int pos) {
        selectedAdapterPos = pos;
        deleteGigDialog(activity, data.id);
    }

    @Override
    public void onClickEditGig(GigList.Data data, int pos) {
        selectedAdapterPos = pos;
        activity.disableEnableTouch(true);
        isClickOnEdit = true;
        if (data.gigType.equalsIgnoreCase("1") || data.gigType.equals("3")) {
            gigDetailAPI.getCustomGigDetails(data.id);
        } else {
            gigDetailAPI.getGigDetails(data.id);
        }

    }

    @Override
    public void onClickDuplicateGig(GigList.Data data, int pos) {
        selectedAdapterPos = pos;
        activity.disableEnableTouch(true);
        isClickDuplicate = true;
        if (data.gigType.equalsIgnoreCase("1") || data.gigType.equals("3")) {
            gigDetailAPI.getCustomGigDetails(data.id);
        } else {
            gigDetailAPI.getGigDetails(data.id);
        }
    }

    void deleteGigDialog(BaseActivity activity, int gigID) {
        final Dialog dialog = new Dialog(activity, R.style.Theme_AppCompat_Dialog);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.setCancelable(true);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        TextView tvCancel = dialog.findViewById(R.id.tv_cancel);
        TextView tvChatnow = dialog.findViewById(R.id.tv_chat_now);

        String s = activity.getString(R.string.gig_delete_msg);
        String[] words = {getString(R.string.delete_)};
        String[] fonts = {Constants.SFTEXT_BOLD};
        tvMessage.setText(Utils.getBoldString(activity, s, fonts, null, words));

        tvCancel.setOnClickListener(v -> {
            dialog.dismiss();
            selectedAdapterPos = -1;
        });

        tvChatnow.setOnClickListener(v -> {
            dialog.dismiss();
            activeGigsFragmentVM.deleteGig(gigID);
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.gravity = Gravity.CENTER;
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);
    }
}
