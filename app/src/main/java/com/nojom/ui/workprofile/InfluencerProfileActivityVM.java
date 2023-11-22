package com.nojom.ui.workprofile;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.AndroidViewModel;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.adapter.ProfilePlatformAdapter;
import com.nojom.adapter.ReviewsAdapter;
import com.nojom.adapter.StoreAdapter;
import com.nojom.adapter.VerifiedAdapter;
import com.nojom.databinding.ActivityInfluencerProfileBinding;
import com.nojom.model.ClientReviews;
import com.nojom.model.ProfileResponse;
import com.nojom.model.SocialPlatformList;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;

import java.util.ArrayList;
import java.util.List;

class InfluencerProfileActivityVM extends AndroidViewModel implements View.OnClickListener{
    private final ActivityInfluencerProfileBinding binding;
    @SuppressLint("StaticFieldLeak")
    private final BaseActivity activity;

//    private AgentProfile agentData;
    private ProfileResponse clientData;
    private boolean isShowHire, isRehire, viewMoreService = true, viewMoreStore = true, viewMoreAgency = true, viewMoreReview = true;
//    private Proposals.Data proposalData;
    private VerifiedAdapter mVerifiedAdapter;
    private boolean isFromChatScreen;
    public List<SocialPlatformList.Data> socialPlatformList;

    private List<ClientReviews.Data> reviewsList, reviewsListAll;
    private List<SocialPlatformList.Data> socialListPage;
//    private List<AgentProfile.StoreList> storeList;
    private int gigId = 0, selectedPos, page = 1;

    public void setServiceList(List<SocialPlatformList.Data> socialPlatformList) {
        this.socialPlatformList = socialPlatformList;
    }

    InfluencerProfileActivityVM(Application application, ActivityInfluencerProfileBinding profileBinding, BaseActivity freelancerProfileActivity) {
        super(application);
        binding = profileBinding;
        activity = freelancerProfileActivity;
        reviewsList = new ArrayList<>();
        reviewsListAll = new ArrayList<>();
        socialPlatformList = new ArrayList<>();
        socialListPage = new ArrayList<>();
//        storeList = new ArrayList<>();
        initData();
    }

    private void initData() {
        binding.imgBack.setOnClickListener(this);
        binding.imgShare.setOnClickListener(this);
//        binding.tvHire.setOnClickListener(this);
        binding.tvChat.setOnClickListener(this);
        binding.tvSendOffer.setOnClickListener(this);
        binding.rlPortfolioView.setOnClickListener(this);
        binding.relServicesAll.setOnClickListener(this);
        binding.rlAgencyView.setOnClickListener(this);
        binding.relReviewsAll.setOnClickListener(this);
        binding.rlStoreView.setOnClickListener(this);
        binding.imgSave.setOnClickListener(this);

        clientData = Preferences.getProfileData(activity);

        if (clientData != null) {

            if (clientData.firstName != null && clientData.lastName != null) {
                binding.tvName.setTextColor(activity.getColor(R.color.black));
                binding.tvName.setText(clientData.firstName + " " + clientData.lastName);
            }
            if (clientData.username != null) {
                binding.tvUserName.setTextColor(activity.getColor(R.color.black));
                binding.tvUserName.setText("@" + clientData.username);
            }
            if (clientData.websites != null) {
                binding.tvLink.setTextColor(activity.getColor(R.color.black));
                binding.tvLink.setText(clientData.websites);
            }

            Glide.with(activity).load(activity.getImageUrl() + clientData.profilePic).placeholder(R.drawable.dp).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    binding.progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(binding.imgProfile);


//            if (clientData.profilePic != null) {
//                binding.imgProfile.setOnClickListener(v -> viewFile(getImageUrl() + clientData.profilePic));
//            }
        }



//        if (activity.getIntent() != null) {
//            agentData = (AgentProfile) activity.getIntent().getSerializableExtra(AGENT_PROFILE_DATA);
//            isShowHire = activity.getIntent().getBooleanExtra(Constants.SHOW_HIRE, false);
//            isRehire = activity.getIntent().getBooleanExtra(Constants.REHIRE, false);
//            isFromChatScreen = activity.getIntent().getBooleanExtra("from", false);
//            proposalData = (Proposals.Data) activity.getIntent().getSerializableExtra(Constants.USER_DATA);
//        }

//        if (clientData != null) {
//
////            getReviews(page, clientData.id);
////            getSocialPlatforms(clientData.id);
////            getAgency(clientData.id);
////            getMyPortfolios();
//
//            setUi();
//        } else {
//            activity.finish();
//            return;
//        }

        clientData = Preferences.getProfileData(activity);

//        if (isRehire) {
//            binding.tvHire.setText(activity.getString(R.string.rehire_me));
//        }


        binding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            Log.e("lll", "" + (abs(verticalOffset) - appBarLayout.getTotalScrollRange()));
            if (abs(verticalOffset) - appBarLayout.getTotalScrollRange() == 0) {
                //  Collapsed
                binding.toolbarTitle.setVisibility(VISIBLE);
            } else {
                //Expanded
                binding.toolbarTitle.setVisibility(View.INVISIBLE);
            }
        });

        binding.scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            // on scroll change we are checking when users scroll as bottom.
            if (viewMoreReview) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    page++;
//                    getReviews(page, agentData.id);
                }
            }
        });
    }

    private void setUi() {

        if (clientData != null) {
            if (clientData.firstName != null && clientData.lastName != null) {
                binding.tvName.setText(String.format("%s %s", clientData.firstName, clientData.lastName));
                binding.toolbarTitle.setText(String.format("%s %s", clientData.firstName, clientData.lastName));
            }

            binding.tvUserName.setText("@" + clientData.username);

            if (!TextUtils.isEmpty(clientData.websites)) {
                binding.tvLink.setText(String.format("%s", clientData.websites));
            }

//            if (clientData.saved == 1) {
//                binding.imgSave.setImageResource(R.drawable.ic_fav_fill);
//            } else {
//                binding.imgSave.setImageResource(R.drawable.ic_fav);
//            }

            if (clientData.trustRateStatus != null && clientData.trustRateStatus.verifyId != null && clientData.trustRateStatus.verifyId == 1) {
                binding.imgVerified.setVisibility(VISIBLE);
            }

//            binding.imgSave.setImageResource(R.drawable.ic_fav_fill);

//            activity.setImage(binding.imgProfile, TextUtils.isEmpty(clientData.profilePic) ? "" : /*clientData.path +*/ clientData.profilePic, 0, 0);

//            setServicesAdapter(socialListPage);
            updateBlockUnblockStatus();

//            if (agentData.store != null && agentData.store.size() > 0) {
//                storeList.add(agentData.store.get(0));
//            }
//            if (agentData.store != null && agentData.store.size() > 1) {
//                storeList.add(agentData.store.get(1));
//            }
//            setStoreAdapter(storeList);
        }
    }

    public void setPlatformAdapter(List<SocialPlatformList.Data> profilePlatformArrayList) {
        ProfilePlatformAdapter adapter = new ProfilePlatformAdapter();
        adapter.doRefresh(profilePlatformArrayList, activity);
        binding.rvPlatform.setAdapter(adapter);
    }

    StoreAdapter storeAdapter;

//    public void setStoreAdapter(List<AgentProfile.StoreList> profilePlatformArrayList) {
//        if (profilePlatformArrayList != null && profilePlatformArrayList.size() > 0) {
//            storeAdapter = new StoreAdapter();
//            storeAdapter.doRefresh(profilePlatformArrayList, activity);
//            binding.rvStore.setAdapter(storeAdapter);
//            binding.linStore.setVisibility(VISIBLE);
//        } else {
//            binding.linStore.setVisibility(GONE);
//        }
//    }

    private void updateBlockUnblockStatus() {
//        if (agentData.blockStatus == 0) {
//            binding.tvReportBlock.setText(activity.getString(R.string.report_amp_block));
//        } else {
//            binding.tvReportBlock.setText(activity.getString(R.string.unblock));
//        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                activity.onBackPressed();
                break;
            case R.id.img_save:
//                saveRemoveInfluencer();
                break;
            case R.id.img_share:
//                if (agentData.firebaseLink != null) {
//                    activity.shareApp(agentData.firebaseLink);
//                }
                break;
            case R.id.rl_portfolio_view:
//                Intent iP = new Intent(activity, InfluencerProfileAllActivity.class);
//                iP.putExtra(Constants.AGENT_PROFILE_DATA, agentData);
//                iP.putExtra("platform", (Serializable) socialPlatformList);
//                iP.putExtra("portfolios", portfolios);
//                iP.putExtra("screen", "portfolio");
//                activity.startActivity(iP);
                break;
            case R.id.rel_services_all:

                if (viewMoreService) {//view more case
//                    setServicesAdapter(socialPlatformList);
                    binding.txtServiceAll.setText(activity.getString(R.string.view_less));
                    viewMoreService = false;
                } else {//view less
                    binding.txtServiceAll.setText(activity.getString(R.string.view_all));
//                    setServicesAdapter(socialListPage);
                    viewMoreService = true;
                }

//                Intent iS = new Intent(activity, InfluencerProfileAllActivity.class);
//                iS.putExtra(Constants.AGENT_PROFILE_DATA, agentData);
//                iS.putExtra("platform", (Serializable) socialPlatformList);
//                iS.putExtra("screen", "services");
//                activity.startActivity(iS);
                break;
            case R.id.rl_agency_view:

                if (viewMoreAgency) {
                    binding.txtAgencyAll.setText(activity.getString(R.string.view_less));
                    binding.txtAbout.setVisibility(VISIBLE);
                    binding.tvAgencyAbout.setVisibility(VISIBLE);
                    binding.txtEmail.setVisibility(VISIBLE);
                    binding.tvAgencyEmail.setVisibility(VISIBLE);
                    binding.txtAddress.setVisibility(VISIBLE);
                    binding.tvAgencyAdd.setVisibility(VISIBLE);
                    binding.txtNote.setVisibility(VISIBLE);
                    binding.tvAgencyNote.setVisibility(VISIBLE);
                    viewMoreAgency = false;
                } else {
                    binding.txtAgencyAll.setText(activity.getString(R.string.view_all));
                    binding.txtAbout.setVisibility(GONE);
                    binding.tvAgencyAbout.setVisibility(GONE);
                    binding.txtEmail.setVisibility(GONE);
                    binding.tvAgencyEmail.setVisibility(GONE);
                    binding.txtAddress.setVisibility(GONE);
                    binding.tvAgencyAdd.setVisibility(GONE);
                    binding.txtNote.setVisibility(GONE);
                    binding.tvAgencyNote.setVisibility(GONE);
                    viewMoreAgency = true;
                }


//                Intent iA = new Intent(activity, InfluencerProfileAllActivity.class);
//                iA.putExtra(Constants.AGENT_PROFILE_DATA, agentData);
//                iA.putExtra("platform", (Serializable) socialPlatformList);
//                iA.putExtra("screen", "agency");
//                activity.startActivity(iA);
                break;
            case R.id.rel_reviews_all:

                if (viewMoreReview) {//view more case
//                    setReviewAdapter(reviewsListAll);
                    binding.txtReviewAll.setText(activity.getString(R.string.view_less));
                    viewMoreReview = false;
                } else {//view less
                    page = 1;
                    reviewsListAll = new ArrayList<>();
                    binding.txtReviewAll.setText(activity.getString(R.string.view_all));
//                    setReviewAdapter(reviewsList);
                    viewMoreReview = true;
                }


//                Intent iR = new Intent(activity, InfluencerProfileAllActivity.class);
//                iR.putExtra(Constants.AGENT_PROFILE_DATA, agentData);
//                iR.putExtra("platform", (Serializable) socialPlatformList);
//                iR.putExtra("screen", "review");
//                activity.startActivity(iR);
                break;
            case R.id.rl_store_view:

                if (viewMoreStore) {//view more case
//                    setStoreAdapter(agentData.store);
                    binding.txtStoreAll.setText(activity.getString(R.string.view_less));
                    viewMoreStore = false;
                } else {//view less
                    binding.txtStoreAll.setText(activity.getString(R.string.view_all));
//                    setStoreAdapter(storeList);
                    viewMoreStore = true;
                }

//                Intent iSt = new Intent(activity, InfluencerProfileAllActivity.class);
//                iSt.putExtra(Constants.AGENT_PROFILE_DATA, agentData);
//                iSt.putExtra("platform", (Serializable) socialPlatformList);
//                iSt.putExtra("screen", "store");
//                activity.startActivity(iSt);
                break;
            case R.id.tv_chat:
//                if (activity.isLogin()) {
//                    if (clientData != null) {
//                        if (isFromChatScreen) {
//                            activity.finish();
//                        } else {
//                            HashMap<String, String> chatMap = new HashMap<>();
//                            chatMap.put(Constants.RECEIVER_ID, agentData.id + "");
//                            chatMap.put(Constants.RECEIVER_NAME, agentData.username);
//                            chatMap.put(Constants.RECEIVER_PIC, agentData.path + agentData.profilePic);
//                            chatMap.put(Constants.SENDER_ID, clientData.id + "");
//                            chatMap.put(Constants.SENDER_NAME, clientData.username);
//                            chatMap.put(Constants.SENDER_PIC, clientData.filePath.pathProfilePicClient + clientData.profilePic);
//                            if (proposalData != null && proposalData.jobPostId != 0) {
//                                chatMap.put(Constants.PROJECT_ID, String.valueOf(proposalData.jobPostId));
//                            }
//
//                            Intent i = new Intent(activity, ChatMessagesActivity.class);
//                            i.putExtra(Constants.CHAT_ID, clientData.id + "-" + agentData.id);  // ClientId - AgentId
//                            i.putExtra(Constants.CHAT_DATA, chatMap);
//                            activity.startActivity(i);
//                        }
//                    }
//                } else {
//                    Preferences.writeString(activity, "influencerName", agentData.proUsername);
//                    activity.openLoginDialog();
//                }
                break;
            case R.id.tv_hire:
//                if (activity.isLogin()) {
//
//                    Intent i = new Intent(activity, HireDescribeActivity.class);
//                    i.putExtra(Constants.AGENT_PROFILE_DATA, agentData);
//                    activity.startActivity(i);
//                } else {
//                    Preferences.writeString(activity, "influencerName", agentData.proUsername);
//                    activity.openLoginDialog();
//                }
                break;
            case R.id.tv_report_block:
                if (activity.isLogin()) {
//                    if (clientData.blockStatus == 0) {
//                        refundPaymentReasonDialog();
//                    } else {
//                        showUnblockDialog();
//                    }
                } else {
                    activity.openLoginDialog();
                }
                break;
            case R.id.tv_sendOffer:
//                Intent i = new Intent(activity, HireDescribeActivity.class);
//                i.putExtra(Constants.AGENT_PROFILE_DATA, agentData);
//                activity.startActivity(i);
                break;
        }
    }

//    public void getReviews(int pageNo, int profileId) {
//        activity.isClickableView = true;
//
//        HashMap<String, String> map = new HashMap<>();
//        map.put("agent_profile_id", profileId + "");
//        map.put("page_no", pageNo + "");
//
//        ApiRequest apiRequest = new ApiRequest();
//        apiRequest.apiRequest(this, activity, API_GET_AGENT_REVIEW, true, map);
//    }
//
//    public void getSocialPlatforms(int profileId) {
//        activity.isClickableView = true;
//
//
//        ApiRequest apiRequest = new ApiRequest();
//        apiRequest.apiRequest(this, activity, API_GET_SOCIAL_PLATFORM_LIST +
//                /*"456696"*/profileId, false, null);
//
//    }
//
//    public void getAgency(int profileId) {
//        activity.isClickableView = true;
//
//
//        ApiRequest apiRequest = new ApiRequest();
//        apiRequest.apiRequest(this, activity, API_GET_AGENCY +
//                /*"456696"*/profileId, false, null);
//
//    }
//
//    private void getGigDetails() {
//        if (!activity.isNetworkConnected()) {
//            return;
//        }
//
//        ApiRequest apiRequest = new ApiRequest();
//
//        apiRequest.apiRequest(this, activity, API_GET_CUSTOM_GIG_DETAILS + "/" + gigId, false, null);
//    }
//
//    public void getMyPortfolios() {
//        if (!activity.isNetworkConnected()) return;
//
//        HashMap<String, String> map = new HashMap<>();
//        map.put("agent_profile_id", agentData.id + "");
//
//        ApiRequest apiRequest = new ApiRequest();
//        apiRequest.apiRequest(this, activity, API_GET_PORTFOLIO, true, map);
//    }
//
//    public void saveRemoveInfluencer() {
//        if (!activity.isNetworkConnected()) return;
//
//        HashMap<String, String> map = new HashMap<>();
//        map.put("agentID", agentData.id + "");
//
//        ApiRequest apiRequest = new ApiRequest();
//        apiRequest.apiRequest(this, activity, API_SAVE_INFLU, true, map);
//    }

    ReviewsAdapter reviewsAdapter;

//    private void setReviewAdapter(List<ClientReviews.Data> reviewsList) {
//        if (reviewsList != null && reviewsList.size() > 0) {
//            if (reviewsAdapter == null) {
//                reviewsAdapter = new ReviewsAdapter(activity);
//                reviewsAdapter.doRefresh(reviewsList);
//                binding.rvLinkedin.setAdapter(reviewsAdapter);
//            } else {
//                reviewsAdapter.doRefresh(reviewsList);
//            }
//
//            binding.rvLinkedin.setVisibility(View.VISIBLE);
//            binding.linReviews.setVisibility(VISIBLE);
//        } else {
//            binding.linReviews.setVisibility(View.GONE);
//        }
//    }

//    InfluencerServiceAdapter influencerServiceAdapter;
//
//    private void setServicesAdapter(List<SocialPlatformList.Data> serviceList) {
//        if (serviceList != null && serviceList.size() > 0) {
//            influencerServiceAdapter = new InfluencerServiceAdapter(activity, serviceList, InfluencerProfileActivityCopyVM.this);
//            binding.rvServices.setAdapter(influencerServiceAdapter);
//            binding.rvServices.setVisibility(View.VISIBLE);
//            binding.linServices.setVisibility(VISIBLE);
//        } else {
//            binding.linServices.setVisibility(GONE);
//        }
//    }
//
//    Portfolios portfolios;

    /*@Override
    public void successResponse(String responseBody, String url, String message, String data) {
        if (url.equalsIgnoreCase(API_GET_AGENT_REVIEW)) {
            ClientReviews agentReviews = ClientReviews.getClientReviews(responseBody);
            if (agentReviews != null && agentReviews.data != null) {
                if (page == 1) {
                    reviewsList.add(agentReviews.data.get(0));
                    reviewsList.add(agentReviews.data.get(1));
                }
                reviewsListAll.addAll(agentReviews.data);
            }
//            binding.shimmerLayoutReview.setVisibility(View.GONE);
//            binding.shimmerLayoutReview.stopShimmer();
            setReviewAdapter(page == 1 ? reviewsList : reviewsListAll);
        } else if (url.equalsIgnoreCase(API_GET_AGENCY + *//*"456696"*//*agentData.id)) {

            AgencyList socialList = AgencyList.getAgencyList(responseBody);
            agentData.agent_agency = socialList.data;

            activity.runOnUiThread(() -> {
                if (agentData.agent_agency != null && agentData.agent_agency.size() > 0) {
                    binding.linAgency.setVisibility(VISIBLE);
                    binding.tvAgencyName.setText(agentData.agent_agency.get(0).name);
                    binding.tvAgencyContact.setText(agentData.agent_agency.get(0).phone);
                    binding.tvAgencyWebsite.setText(agentData.agent_agency.get(0).website);
                    binding.tvAgencyEmail.setText(agentData.agent_agency.get(0).email);
                    binding.tvAgencyAdd.setText(agentData.agent_agency.get(0).address);
                    binding.tvAgencyNote.setText(agentData.agent_agency.get(0).note);
                    binding.tvAgencyAbout.setText(agentData.agent_agency.get(0).about);
                }
            });

        } else if (url.equalsIgnoreCase(API_GET_CUSTOM_GIG_DETAILS + "/" + gigId)) {
            ExpertGigDetail expertGigDetail = ExpertGigDetail.getGigDetail(responseBody);
            Preferences.writeString(activity, "gigID", null);

            if (expertGigDetail != null) {

                activity.runOnUiThread(() -> {
                    if (influencerServiceAdapter != null) {
                        influencerServiceAdapter.getData().get(selectedPos).isShowProgress = false;
                        influencerServiceAdapter.notifyItemChanged(selectedPos);
                    }
                });

                Intent intent = new Intent(activity, GigDetailActivity.class);
                intent.putExtra(Constants.PROJECT_DETAIL, expertGigDetail);
//                intent.putExtra("gigID", gigId);
                activity.startActivity(intent);
            }
            gigId = 0;
            selectedPos = 0;
        } else if (url.equalsIgnoreCase(API_GET_PROFILE_INFO)) {
            AgentProfile profile = AgentProfile.getProfileInfo(responseBody);
            if (profile != null) {
                agentData = profile;

                setUi();
            }
        } else if (url.equalsIgnoreCase(API_UNBLOCK_USER)) {
            activity.failureError(message);
            agentData.blockStatus = 0;
            updateBlockUnblockStatus();
        } else if (url.equalsIgnoreCase(API_BLOCK_USER)) {
            activity.failureError(message);
            agentData.blockStatus = 1;
            updateBlockUnblockStatus();
        } else if (url.equalsIgnoreCase(API_GET_PORTFOLIO)) {
            portfolios = Portfolios.getPortfolios(responseBody);
            if (portfolios != null && portfolios.data != null && portfolios.data.size() > 0) {
                binding.linPortfolio.setVisibility(VISIBLE);
                setPortfolioAdapter(portfolios.data, portfolios.path);
            } else {
                binding.linPortfolio.setVisibility(GONE);
            }
        } else if (url.equalsIgnoreCase(API_SAVE_INFLU)) {
            activity.toastMessage(message);
            SavedInfluencer savedInf = SavedInfluencer.getData(responseBody);
            if (savedInf.saved == 1) {
                binding.imgSave.setImageResource(R.drawable.ic_fav_fill);
            } else {
                binding.imgSave.setImageResource(R.drawable.ic_fav);
            }
        } else {
            SocialPlatformList socialList = SocialPlatformList.getSocialPlatforms(responseBody);
            socialPlatformList = socialList.data;
            //setServiceList(socialPlatformList);
            if (socialList.data.size() > 0) {
                socialListPage.add(socialList.data.get(0));
            }
            if (socialList.data.size() > 1) {
                socialListPage.add(socialList.data.get(1));
            }
            setPlatformAdapter(socialPlatformList);
            setServicesAdapter(socialListPage);
        }
        activity.isClickableView = false;
    }
*/
    /*private void setPortfolioAdapter(List<Portfolios.Data> data, String filePath) {
        InfluPortfolioAdapter portfolioFileAdapter = new InfluPortfolioAdapter(activity, data, filePath);
        binding.rvPortfolio.setAdapter(portfolioFileAdapter);
    }*/

    /*@Override
    public void failureResponse(Throwable throwable, String url, String message) {
        activity.isClickableView = false;
        if (url.equalsIgnoreCase(API_GET_CUSTOM_GIG_DETAILS + "/" + gigId)) {
            activity.runOnUiThread(() -> {
                if (influencerServiceAdapter != null) {
                    influencerServiceAdapter.getData().get(selectedPos).isShowProgress = false;
                    influencerServiceAdapter.notifyItemChanged(selectedPos);
                }
            });
            gigId = 0;
            selectedPos = 0;
        } else if (url.equalsIgnoreCase(API_GET_PROFILE_INFO)) {
            activity.finish();
        } else if (url.equalsIgnoreCase(API_GET_AGENCY + *//*"456696"*//*agentData.id)) {
            binding.linAgency.setVisibility(GONE);
        } else if (url.equalsIgnoreCase(API_GET_PORTFOLIO)) {
            binding.linPortfolio.setVisibility(GONE);
        } else if (url.equalsIgnoreCase(API_SAVE_INFLU)) {
            activity.toastMessage(message);
        }
//        binding.shimmerLayoutReview.setVisibility(View.GONE);
//        binding.shimmerLayoutReview.stopShimmer();
        setReviewAdapter(reviewsList);
        setServicesAdapter(socialListPage);
    }*/

    /*@Override
    public void onClickService(SocialPlatformList.Data data, int pos) {
        gigId = data.id;
        selectedPos = pos;
        getGigDetails();
    }*/

}
