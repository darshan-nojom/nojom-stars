package com.nojom.ui.clientprofile;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nojom.R;
import com.nojom.adapter.ReviewsAdapter;
import com.nojom.adapter.VerifiedAdapter;
import com.nojom.databinding.ActivityEmployerProfileBinding;
import com.nojom.model.ClientReviews;
import com.nojom.model.ProfileClient;
import com.nojom.model.ProfileResponse;
import com.nojom.ui.BaseActivity;
import com.nojom.util.Constants;
import com.nojom.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class EmployerProfileActivity extends BaseActivity {
    private ActivityEmployerProfileBinding binding;
    private EmployerProfileActivityVM employerProfileActivityVM;
    private ReviewsAdapter mAdapter;
    private VerifiedAdapter mVerifiedAdapter;
    private ProfileClient clientData;
    private int visibleItemCount, totalItemCount, pastVisiblesItems, page = 1;
    private boolean isLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(true);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_employer_profile);
        employerProfileActivityVM = ViewModelProviders.of(this).get(EmployerProfileActivityVM.class);
        employerProfileActivityVM.init(this);
        initData();
    }

    private void initData() {
        if (getIntent() != null) {
            clientData = (ProfileClient) getIntent().getSerializableExtra(Constants.CLIENT_PROFILE_DATA);
        }

        if (clientData == null) {
            finish();
            return;
        }

        setUi();

        binding.noData.tvNoTitle.setText(getString(R.string.no_reviews));
        binding.noData.tvNoDescription.setText(getString(R.string.no_reviews_desc));


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvReviews.setLayoutManager(linearLayoutManager);
        binding.rvVerified.setLayoutManager(new GridLayoutManager(this, 2));

        employerProfileActivityVM.getClientReview(page, clientData.id);

        binding.imgBack.setOnClickListener(view -> onBackPressed());

        binding.tvReportBlock.setOnClickListener(v -> {
            if (clientData.blockStatus == 0) {
                employerProfileActivityVM.refundPaymentReasonDialog(clientData.id);
            } else {
                employerProfileActivityVM.showUnblockDialog(clientData.id);
            }
        });

        employerProfileActivityVM.getIsBlockStatus().observe(this, blockStatus -> {
            clientData.blockStatus = blockStatus;
            setBlockStatus();
        });

        employerProfileActivityVM.getIsShowProgress().observe(this, isShowProgress -> {
            disableEnableTouch(isShowProgress);
            if (isShowProgress) {
                binding.shimmerLayoutReviews.setVisibility(View.VISIBLE);
                binding.shimmerLayoutReviews.startShimmer();
            } else {
                binding.shimmerLayoutReviews.setVisibility(View.GONE);
                binding.shimmerLayoutReviews.stopShimmer();
            }
        });

        binding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (v.getChildAt(v.getChildCount() - 1) != null) {
                if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                        scrollY > oldScrollY) {

                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoadData) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            isLoadData = true;
                            page++;
                            employerProfileActivityVM.getClientReview(page, clientData.id);
                        }
                    }
                }
            }
        });

        employerProfileActivityVM.getReviewsList().observe(this, this::setReviewsAdapter);
    }

    private void setReviewsAdapter(List<ClientReviews.Data> data) {
        if (data != null && data.size() > 0) {
            binding.noData.llNoData.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new ReviewsAdapter();
            }
            mAdapter.doRefresh(employerProfileActivityVM.getReviewsList().getValue(), true);

            if (binding.rvReviews.getAdapter() == null) {
                binding.rvReviews.setAdapter(mAdapter);
            }
            isLoadData = false;
        } else {
            if (mAdapter == null || mAdapter.getItemCount() == 0) {
                binding.noData.llNoData.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setVerifiedAdapter(ArrayList<ProfileResponse.VerifiedWith> verifiedList) {
        if (verifiedList != null && verifiedList.size() > 0) {
            binding.tvNoVerified.setVisibility(View.GONE);
            if (mVerifiedAdapter == null) {
                mVerifiedAdapter = new VerifiedAdapter();
            }
            mVerifiedAdapter.doRefresh(verifiedList);

            if (binding.rvVerified.getAdapter() == null) {
                binding.rvVerified.setAdapter(mVerifiedAdapter);
            }
        } else {
            binding.tvNoVerified.setVisibility(View.VISIBLE);
            if (mVerifiedAdapter != null) {
                mVerifiedAdapter.doRefresh(verifiedList);
            }
        }
    }

    private void setUi() {
        if (clientData != null) {
            binding.tvUserName.setText(getProperName(clientData.firstName, clientData.lastName, clientData.username));

            String rate = Utils.numberFormat(clientData.averageRate, 1);
            binding.tvReviews.setText(String.format("(%s)", rate));
            try {
                binding.ratingbar.setRating(Float.parseFloat(rate));
            } catch (NumberFormatException e) {
                binding.ratingbar.setRating(0);
            }

            if (clientData.img != null) {
                Glide.with(this).load(getClientImageUrl() + clientData.img)
                        .placeholder(R.drawable.dp)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                binding.progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                binding.progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(binding.imgProfile);
            }

            ArrayList<ProfileResponse.VerifiedWith> verifiedList = new ArrayList<>();

            if (clientData.trustVerificationPoints != null) {
                if (clientData.trustVerificationPoints.email > 0) {
                    verifiedList.add(new ProfileResponse.VerifiedWith(getString(R.string.email_address), clientData.trustVerificationPoints.email));
                }
                if (clientData.trustVerificationPoints.facebook > 0) {
                    verifiedList.add(new ProfileResponse.VerifiedWith(getString(R.string.facebook), clientData.trustVerificationPoints.facebook));
                }
                if (clientData.trustVerificationPoints.payment > 0) {
                    verifiedList.add(new ProfileResponse.VerifiedWith(getString(R.string.payment), clientData.trustVerificationPoints.payment));
                }
                if (clientData.trustVerificationPoints.phoneNumber > 0) {
                    verifiedList.add(new ProfileResponse.VerifiedWith(getString(R.string.phonenumber), clientData.trustVerificationPoints.phoneNumber));
                }
                if (clientData.trustVerificationPoints.verifyId > 0) {
                    verifiedList.add(new ProfileResponse.VerifiedWith(getString(R.string.government_id), clientData.trustVerificationPoints.verifyId));
                }
            }
            setVerifiedAdapter(verifiedList);

            setBlockStatus();
        }
    }

    private void setBlockStatus() {
        if (clientData.blockStatus == 0) {
            binding.tvReportBlock.setText(getString(R.string.report_amp_block));
        } else {
            binding.tvReportBlock.setText(getString(R.string.unblock));
        }
    }
}
