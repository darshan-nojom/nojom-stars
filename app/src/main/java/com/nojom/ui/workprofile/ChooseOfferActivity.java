package com.nojom.ui.workprofile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.adapter.ChooseOfferAdapter;
import com.nojom.apis.GigDetailAPI;
import com.nojom.databinding.ActivityChooseOfferBinding;
import com.nojom.model.GigDeliveryTimeModel;
import com.nojom.model.GigDetails;
import com.nojom.model.GigList;
import com.nojom.model.GigPackages;
import com.nojom.model.RequiremetList;
import com.nojom.multitypepicker.filter.entity.ImageFile;
import com.nojom.ui.BaseActivity;
import com.nojom.ui.gigs.ActiveGigsFragmentVM;
import com.nojom.ui.gigs.OfferGigViewActivity;
import com.nojom.ui.gigs.StandardGigOfferViewActivity;
import com.nojom.util.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;

public class ChooseOfferActivity extends BaseActivity implements ChooseOfferAdapter.GigClickListener {

    private ActivityChooseOfferBinding binding;
    private ActiveGigsFragmentVM activeGigsFragmentVM;
    private EndlessRecyclerViewScrollListener scrollListener;
    private ChooseOfferAdapter offerAdapter;
    private int selectedAdapterPos = 0;
    private GigDetailAPI gigDetailAPI;
    private String cUsername;
    private long cUserid;
    public static ChooseOfferActivity chooseOfferActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chooseOfferActivity = this;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_offer);

        cUsername = getIntent().getStringExtra("cUsername");
        cUserid = getIntent().getLongExtra("cUserId", 0);

        activeGigsFragmentVM = ViewModelProviders.of(this).get(ActiveGigsFragmentVM.class);
        activeGigsFragmentVM.init(this);
        gigDetailAPI = new GigDetailAPI();
        gigDetailAPI.init(this);

        binding.llToolbar.tvTitle.setText(getString(R.string.offer_gig));

        binding.llToolbar.imgBack.setOnClickListener(v -> {
            finish();
        });

        binding.txtCreateOffer.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateOfferActivity.class);
            intent.putExtra("cUsername", "" + cUsername);
            intent.putExtra("cUserId", cUserid);
            startActivity(intent);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        //scroll more listener
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 9) {
                    activeGigsFragmentVM.getPageNoData().setValue(page);
                    activeGigsFragmentVM.getActiveGig(false, 1);
                }
            }
        };

        //swipe refresh
        binding.swipeRefreshLayout.setOnRefreshListener(this::refresh);

        setPlaceHolder(getString(R.string.no_active_gig), getString(R.string.all_active_gig_displayed_here));

        activeGigsFragmentVM.getIsShowProgress().observe(this, state -> {
            disableEnableTouch(true);
            if (state == 1) {
                binding.recyclerView.setVisibility(View.INVISIBLE);
                binding.noData.llNoData.setVisibility(View.GONE);
                binding.shimmerLayout.startShimmer();
                binding.shimmerLayout.setVisibility(View.VISIBLE);
            }
        });

        activeGigsFragmentVM.getIsHideProgress().observe(this, state -> {
            disableEnableTouch(false);
            if (state == 2) {//details
                updateAdapterState(false, "0");
            } else {
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.shimmerLayout.stopShimmer();
                binding.shimmerLayout.setVisibility(View.GONE);
                if (offerAdapter == null || offerAdapter.getItemCount() == 0) {
                    binding.noData.llNoData.setVisibility(View.VISIBLE);
                } else {
                    binding.noData.llNoData.setVisibility(View.GONE);
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

//        activeGigsFragmentVM.getIsHideProgress().observe(this, this::updateProgress);
        activeGigsFragmentVM.getGigList().observe(this, data -> bindAdapter(data.gigImagesPath));

        //call to get data from server
        activeGigsFragmentVM.getActiveGig(true, 1);

        activeGigsFragmentVM.getGigDetailsMutableLiveData().observe(this, this::detailsStep);

        gigDetailAPI.getIsHideProgress().observe(this, this::updateProgress);

        gigDetailAPI.getGigDetailsMutableLiveData().observe(this, this::detailsStep);
    }

    private void setPlaceHolder(String title, String desc) {
        binding.noData.tvNoTitle.setText(title);
        binding.noData.tvNoDescription.setText(desc);
    }

    public void refresh() {
        if (activeGigsFragmentVM != null) {
            activeGigsFragmentVM.getPageNoData().setValue(1);
            scrollListener.resetState();
            offerAdapter = null;
            activeGigsFragmentVM.getActiveGig(true, 1);
        }
    }

    private void updateProgress(Integer state) {
        disableEnableTouch(false);
        if (state == 2) {//details
            updateAdapterState(false, "0");
        }
    }

    private void updateAdapterState(boolean isShow, String isNew) {
        if (offerAdapter != null) {
            runOnUiThread(() -> {
                offerAdapter.getData().get(selectedAdapterPos).isShowProgress = isShow;
                offerAdapter.getData().get(selectedAdapterPos).isNew = isNew;
                offerAdapter.notifyItemChanged(selectedAdapterPos);
            });
        }
    }

    private void bindAdapter(String filePath) {

        if (activeGigsFragmentVM.getGigDataList().getValue() != null && activeGigsFragmentVM.getGigDataList().getValue().size() > 0) {
            binding.noData.llNoData.setVisibility(View.GONE);
            if (offerAdapter == null) {
                offerAdapter = new ChooseOfferAdapter(this, activeGigsFragmentVM.getGigDataList().getValue(), filePath, this);
                binding.recyclerView.setAdapter(offerAdapter);
            } else {
                offerAdapter.doRefresh(activeGigsFragmentVM.getGigDataList().getValue());
            }
        } else {
            if (offerAdapter == null || offerAdapter.getItemCount() == 0) {
                binding.noData.llNoData.setVisibility(View.VISIBLE);
            }
        }

        binding.swipeRefreshLayout.setRefreshing(false);
//        projectsListFragmentVM.getIsProgress().setValue(true);
//        ((MyProjectsActivity) activity).showHideHorizontalProgress(View.GONE);
    }

    @Override
    public void onClickGig(GigList.Data data, int pos) {
        selectedAdapterPos = pos;
        disableEnableTouch(true);

        if (data.gigType.equalsIgnoreCase("1") || data.gigType.equals("3")) {
            gigDetailAPI.getCustomGigDetails(data.id);
        } else {
            gigDetailAPI.getGigDetails(data.id);
        }
    }

    private void detailsStep(GigDetails gigDetails) {

        ArrayList<ImageFile> fileList = new ArrayList<>();
        //gig photos
        if (gigDetails.gigImages != null && gigDetails.gigImages.size() > 0) {

            for (GigDetails.GigImage img : gigDetails.gigImages) {
                ImageFile imageFile = new ImageFile();
                imageFile.setPath(gigDetails.gigImagesPath + img.imageName);
                imageFile.setId(img.id);
                imageFile.setIsServerUrl(1);
                fileList.add(imageFile);
            }
        }


        if (gigDetails.gigType.equalsIgnoreCase("1") || gigDetails.gigType.equals("3")) {//custom gig

            ArrayList<RequiremetList.Data> requirementByCatListBinding = new ArrayList<>();


            if (gigDetails.customPackages != null && gigDetails.customPackages.size() > 0) {
                for (GigDetails.CustomRequirements data : gigDetails.customPackages) {

                    RequiremetList.Data gigPack = new RequiremetList.Data();
                    gigPack.name = data.name;
                    gigPack.inputType = data.inputType;
                    gigPack.id = data.reqOrOtherReqID;
                    gigPack.dataReq = data.name;
                    gigPack.gigReqType = data.gigRequirementType;
                    gigPack.isSelected = false;
                    gigPack.isOther = data.isOther ? 1 : 0;
                    gigPack.isOtherReq = data.isOther;
                    gigPack.gigOtherInputType = data.inputType;
                    gigPack.reqDescription = data.description;

                    if (data.requirmentDetails.get(0).price != null) {
                        gigPack.dataValue = "" + data.requirmentDetails.get(0).price;
                    } else {
                        gigPack.dataValue = "";
                    }

                    if (data.requirmentDetails.get(0).featureName != null) {
                        gigPack.featureTitle = "" + data.requirmentDetails.get(0).featureName;
                        gigPack.dataReq = "" + data.requirmentDetails.get(0).featureName;
                    }

                    if (data.requirmentDetails.size() > 1 && data.gigRequirementType != 1) {//this is for custom requirement

                        ArrayList<RequiremetList.CustomData> customDataList = new ArrayList<>();

                        for (int i = 0; i < data.requirmentDetails.size(); i++) {
                            if (i != 0) {//skip zero index
                                GigDetails.ReqDetail custViewData = data.requirmentDetails.get(i);
                                RequiremetList.CustomData customData = new RequiremetList.CustomData();
                                customData.dataReq = custViewData.featureName;
                                customData.dataValue = "" + custViewData.price;
                                customDataList.add(customData);
                            }
                        }

                        if (gigPack.customData == null) {
                            gigPack.customData = new ArrayList<>();
                        }
                        gigPack.customData.addAll(customDataList);

                    }

                    requirementByCatListBinding.add(gigPack);
                }
            }

            Intent intent1 = new Intent(this, OfferGigViewActivity.class);
            intent1.putExtra("title", gigDetails.gigTitle);
            intent1.putExtra("desc", gigDetails.gigDescription);
            intent1.putExtra("deadDesc", gigDetails.deadlineDescription);
            intent1.putExtra("delivery", gigDetails.deadlines);
            intent1.putExtra("files", fileList);
            intent1.putExtra("packages", requirementByCatListBinding);
            intent1.putExtra("cUsername", "" + cUsername);
            intent1.putExtra("cUserId", cUserid);
            intent1.putExtra("gigDetail", gigDetails);
            startActivity(intent1);
        } else {//standard gig

            ArrayList<GigPackages.Data> packages = new ArrayList<>();
            for (GigDetails.Packages data : gigDetails.packages) {
                GigPackages.Data gigPack = new GigPackages.Data();
                gigPack.deliveryTimeID = data.deliveryTimeID;
                gigPack.deliveryTime = data.deliveryTitle;
                gigPack.packageName = data.packageName;
                gigPack.id = data.packageID;
                gigPack.price = data.price;
                gigPack.name = data.name;
                gigPack.packageDescription = data.description;
                gigPack.revisions = data.revisions;
                gigPack.requirements = data.requirements;

                GigDeliveryTimeModel.Data delData = new GigDeliveryTimeModel.Data();
                delData.deliveryTitle = data.deliveryTitle;
                delData.id = data.deliveryTimeID;
                delData.isSelected = true;
                gigPack.delivery = delData;
                packages.add(gigPack);
            }

            Intent intent1 = new Intent(this, StandardGigOfferViewActivity.class);
            intent1.putExtra("title", gigDetails.gigTitle);
            intent1.putExtra("desc", gigDetails.gigDescription);
            intent1.putExtra("files", fileList);
            intent1.putExtra("packages", packages);
            intent1.putExtra("cUsername", "" + cUsername);
            intent1.putExtra("cUserId", cUserid);
            intent1.putExtra("gigDetail", gigDetails);
            startActivity(intent1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (scrollListener != null)
            binding.recyclerView.addOnScrollListener(scrollListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scrollListener != null)
            binding.recyclerView.removeOnScrollListener(scrollListener);
    }
}
