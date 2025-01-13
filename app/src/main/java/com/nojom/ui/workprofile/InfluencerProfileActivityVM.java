package com.nojom.ui.workprofile;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.nojom.R;
import com.nojom.adapter.InfluencerServiceAdapter;
import com.nojom.adapter.ReviewsAdapter;
import com.nojom.adapter.SelectedPlatformAdapter;
import com.nojom.adapter.SkillsListAdapter;
import com.nojom.adapter.StoreAdapter;
import com.nojom.adapter.VerifiedAdapter;
import com.nojom.adapter.VerifyFilesAdapter;
import com.nojom.databinding.ActivityInfluencerProfileBinding;
import com.nojom.fragment.profile.ReviewsProfileFragmentVM;
import com.nojom.model.ClientReviews;
import com.nojom.model.ConnectedSocialMedia;
import com.nojom.model.ProfileResponse;
import com.nojom.model.Skill;
import com.nojom.model.SocialPlatformList;
import com.nojom.model.VerifyID;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Preferences;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;

class InfluencerProfileActivityVM extends AndroidViewModel implements View.OnClickListener, InfluencerServiceAdapter.OnClickService {
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

    private ReviewsProfileFragmentVM reviewsProfileFragmentVM;
    private MyPlatformActivityVM nameActivityVM;
    private VerifyIDActivityVM verifyIDActivityVM;

    InfluencerProfileActivityVM(Application application, ActivityInfluencerProfileBinding profileBinding, BaseActivity freelancerProfileActivity) {
        super(application);
        binding = profileBinding;
        activity = freelancerProfileActivity;
        reviewsList = new ArrayList<>();
        reviewsListAll = new ArrayList<>();
        socialPlatformList = new ArrayList<>();
        socialListPage = new ArrayList<>();
//        storeList = new ArrayList<>();
        reviewsProfileFragmentVM = ViewModelProviders.of(activity).get(ReviewsProfileFragmentVM.class);
        nameActivityVM = ViewModelProviders.of(activity).get(MyPlatformActivityVM.class);
        nameActivityVM.getConnectedPlatform(activity);
        verifyIDActivityVM = ViewModelProviders.of(activity).get(VerifyIDActivityVM.class);
        verifyIDActivityVM.getMawthooqList(activity, "maw");
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
        binding.tvMawId.setOnClickListener(this);

        clientData = Preferences.getProfileData(activity);

        if (clientData != null) {

            if (clientData.firstName != null && clientData.lastName != null) {
                binding.tvName.setTextColor(activity.getColor(R.color.black));
                binding.tvName.setText(clientData.firstName + " " + clientData.lastName);
                binding.toolbarTitle.setText(clientData.firstName + " " + clientData.lastName);
            }
            if (clientData.username != null) {
                binding.tvUserName.setTextColor(activity.getColor(R.color.black));
                binding.tvUserName.setText("@" + clientData.username);
            }
            if (clientData.website != null) {
                binding.tvLink.setTextColor(activity.getColor(R.color.black));
                binding.tvLink.setText(clientData.website);
            }

            if (!TextUtils.isEmpty(clientData.about_me)) {
                binding.tvAboutme.setText(String.format("%s", clientData.about_me));
            } else {
                binding.tvAboutme.setText("-");
            }

            binding.tvGender.setText(clientData.gender != null && clientData.gender == 1 ? activity.getString(R.string.female) : activity.getString(R.string.male));
            if (clientData.show_age != null && clientData.show_age == 1) {
                binding.linAge.setVisibility(VISIBLE);
                if (!TextUtils.isEmpty(clientData.birth_date)) {
                    int age = Utils.calculateAge(clientData.birth_date.split("T")[0]);
                    binding.tvAge.setText("" + age);
                } else {
                    binding.tvAge.setText("-");
                }
            } else {
                binding.linAge.setVisibility(GONE);
            }

            ArrayList<Skill> skillList = new ArrayList<>();
            if (clientData.skills != null && clientData.skills.size() > 0) {
                for (ProfileResponse.Skill data : clientData.skills) {
                    skillList.add(new Skill(data.getName(activity.language), Utils.getRatingLevel(data.rating)));
                }
            }
            setTagsAdapter(skillList);

//            verifyIDActivityVM.getListMutableLiveData().observe(activity, this::setMaqData);
            setMaqData();
//            if (clientData.trustRateStatus != null && clientData.trustRateStatus.verifyId != null && clientData.trustRateStatus.verifyId == 1) {
//            binding.imgVerified.setVisibility(GONE);
//            }

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

            activity.runOnUiThread(() -> {
                if (clientData != null && clientData.profile_agencies != null) {
                    binding.linAgency.setVisibility(VISIBLE);
                    binding.tvAgencyName.setText(clientData.profile_agencies.name);
                    binding.tvAgencyContact.setText(clientData.profile_agencies.phone);
                    binding.tvAgencyWebsite.setText(clientData.profile_agencies.website);
                    binding.tvAgencyEmail.setText(clientData.profile_agencies.email);
                    binding.tvAgencyAdd.setText(clientData.profile_agencies.address);
                    binding.tvAgencyNote.setText(clientData.profile_agencies.note);
                    binding.tvAgencyAbout.setText(clientData.profile_agencies.about);
                }
            });
        }

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
                    reviewsProfileFragmentVM.getAgentReviews(activity, page);
                }
            }
        });

        reviewsProfileFragmentVM.getListMutableLiveData().observe(activity, this::setAdapter);

        reviewsProfileFragmentVM.getAgentReviews(activity, page);
        reviewsProfileFragmentVM.getServiceList(activity);

        nameActivityVM.getConnectedMediaDataList().observe(activity, data -> {

            if (data != null && data.size() > 0) {
                setConnectedMediaAdapter(data);
            }
        });

        reviewsProfileFragmentVM.getServiceListMutableData().observe(activity, data -> {
            if (data != null && data.size() > 0) {
                setServicesAdapter(data);
            }
        });
    }

    List<VerifyID.Data> mawData;

    private void setMaqData() {

//        if (verifyIdsList != null && verifyIdsList.size() > 0) {
//            mawData = verifyIdsList;
//            if (verifyIdsList.get(0).data != null) {
//                binding.tvMawId.setText(String.format("%s", verifyIdsList.get(0).data));
//                binding.tvMawId.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show_password, 0);
//            } else {
//                mawData = null;
//                binding.tvMawId.setText("-");
//            }
//
//        } else {
//            mawData = null;
//            binding.tvMawId.setText("-");
//        }

        if (clientData.mawthooq_status != null) {
            if (clientData.mawthooq_status.data != null) {
                binding.tvMawId.setText(String.format("%s", clientData.mawthooq_status.data));
                binding.tvMawId.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show_password, 0);
            } else {
                binding.tvMawId.setText("-");
            }
        }
    }

    private void setTagsAdapter(List<Skill> data) {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(activity);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        binding.chipView.setLayoutManager(layoutManager);
        SkillsListAdapter skillsListAdapter = new SkillsListAdapter(activity, data, false);
        binding.chipView.setAdapter(skillsListAdapter);
    }

    private void setConnectedMediaAdapter(ArrayList<ConnectedSocialMedia.Data> data) {
        SelectedPlatformAdapter mAdapter = new SelectedPlatformAdapter(activity, data, null);
//        ItemTouchHelper.Callback callback =
//                new ItemMoveCallback(mAdapter);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
//        touchHelper.attachToRecyclerView(binding.rvSelection);
        mAdapter.isView(true);
        binding.rvPlatform.setAdapter(mAdapter);
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
                if (clientData != null && clientData.firebaseLink != null) {
                    String fLink = clientData.firebaseLink.replaceAll("https://", "");
                    activity.shareApp(fLink);
                }
                break;
            case R.id.tv_mawId:
                if (mawData != null && mawData.size() > 0) {
                    String url;
                    if (mawData.get(0).is_number.equals("1")) {
                        url = "https://elaam.gamr.gov.sa/gcam-licenses/gcam-celebrity-check/" + mawData.get(0).data;
                    } else {
                        url = activity.getMawthooqIdUrl() + mawData.get(0).data;
                    }
                    activity.viewFile(url);
                }

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
                setServicesAdapter(reviewsProfileFragmentVM.getServiceListMutableData().getValue());

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

                break;
            case R.id.rel_reviews_all:

                if (viewMoreReview) {//view more case
                    viewMoreReview = false;
                    setAdapter(reviewsProfileFragmentVM.getListMutableLiveData().getValue());
                    binding.txtReviewAll.setText(activity.getString(R.string.view_less));
                } else {//view less
                    page = 1;
                    viewMoreReview = true;
                    reviewsListAll = new ArrayList<>();
                    binding.txtReviewAll.setText(activity.getString(R.string.view_all));
                    setAdapter(reviewsProfileFragmentVM.getListMutableLiveData().getValue());
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

    ReviewsAdapter mAdapter;

    private void setAdapter(List<ProfileResponse.ProjectReview> reviewsList) {
        if (reviewsList != null && reviewsList.size() > 0) {
            binding.rvLinkedin.setVisibility(View.VISIBLE);
            binding.linReviews.setVisibility(VISIBLE);
            if (mAdapter == null) {
                mAdapter = new ReviewsAdapter();
            }
            mAdapter.setMore(viewMoreReview);
            mAdapter.doRefresh(reviewsList);

            if (binding.rvLinkedin.getAdapter() == null) {
                binding.rvLinkedin.setAdapter(mAdapter);
            }
        } else {
            if (page == 1) {
                binding.rvLinkedin.setVisibility(GONE);
                binding.linReviews.setVisibility(GONE);
            }
            if (mAdapter != null) {
                mAdapter.setMore(viewMoreReview);
                mAdapter.doRefresh(reviewsList);
            }
        }

    }

    InfluencerServiceAdapter influencerServiceAdapter;

    private void setServicesAdapter(List<SocialPlatformList.Data> serviceList) {
        if (serviceList != null && serviceList.size() > 0) {
            influencerServiceAdapter = new InfluencerServiceAdapter(activity, serviceList, InfluencerProfileActivityVM.this);
            influencerServiceAdapter.setMore(viewMoreService);
            binding.rvServices.setAdapter(influencerServiceAdapter);
            binding.rvServices.setVisibility(View.VISIBLE);
            binding.linServices.setVisibility(VISIBLE);
        } else {
            binding.linServices.setVisibility(GONE);
        }
    }

    @Override
    public void onClickService(SocialPlatformList.Data data, int pos) {

    }
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
