package com.nojom.fragment.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nojom.R;
import com.nojom.adapter.ReviewsAdapter;
import com.nojom.databinding.FragmentReviewsProfileBinding;
import com.nojom.fragment.BaseFragment;
import com.nojom.model.ProfileResponse;
import com.nojom.util.EndlessRecyclerViewScrollListener;

import java.util.List;

public class ReviewsProfileFragment extends BaseFragment {
    private FragmentReviewsProfileBinding binding;
    private ReviewsAdapter mAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private ReviewsProfileFragmentVM reviewsProfileFragmentVM;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reviews_profile, container, false);
        reviewsProfileFragmentVM = ViewModelProviders.of(this).get(ReviewsProfileFragmentVM.class);
        initData();
        return binding.getRoot();
    }

    private void initData() {

        binding.noData.tvNoTitle.setText(getString(R.string.no_reviews));
        binding.noData.tvNoDescription.setText(getString(R.string.no_reviews_desc));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        binding.rvReviews.setLayoutManager(linearLayoutManager);

        binding.rvReviews.setFocusable(false);

        //scroll more listener
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalItemsCount > 9) {
                    binding.hProgressBar.setVisibility(View.VISIBLE);
                    reviewsProfileFragmentVM.getAgentReviews(ReviewsProfileFragment.this, page);
                }
            }
        };

        reviewsProfileFragmentVM.getIsShowProgress().observe(activity, isShowProgress -> {
            if (isShowProgress) {
                binding.shimmerLayoutReviews.setVisibility(View.VISIBLE);
                binding.shimmerLayoutReviews.startShimmer();
            } else {
                binding.shimmerLayoutReviews.setVisibility(View.GONE);
                binding.shimmerLayoutReviews.stopShimmer();
                binding.hProgressBar.setVisibility(View.GONE);
            }
        });

        reviewsProfileFragmentVM.getListMutableLiveData().observe(this, this::setAdapter);

        reviewsProfileFragmentVM.getAgentReviews(this, 1);
    }


    private void setAdapter(List<ProfileResponse.ProjectReview> reviewsList) {
        if (reviewsList != null && reviewsList.size() > 0) {
            binding.noData.llNoData.setVisibility(View.GONE);
            if (mAdapter == null) {
                mAdapter = new ReviewsAdapter();
            }
            mAdapter.doRefresh(reviewsList);

            if (binding.rvReviews.getAdapter() == null) {
                binding.rvReviews.setAdapter(mAdapter);
            }
        } else {
            binding.noData.llNoData.setVisibility(View.VISIBLE);
            if (mAdapter != null) {
                mAdapter.doRefresh(reviewsList);
            }
        }
        binding.hProgressBar.setVisibility(View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (scrollListener != null)
            binding.rvReviews.addOnScrollListener(scrollListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scrollListener != null)
            binding.rvReviews.removeOnScrollListener(scrollListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
