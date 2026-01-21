package com.whosin.app.ui.activites.venue;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivitySmallVenueComponentSeeAllBinding;
import com.whosin.app.databinding.ItemVenueRecyclerBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.HomeObjectModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.Story.StoryViewActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SmallVenueComponentSeeAllActivity extends BaseActivity {

    private ActivitySmallVenueComponentSeeAllBinding binding;

    private SmallVenueBlockAdapter<VenueObjectModel> adapter = new SmallVenueBlockAdapter<>();

    private List<VenueObjectModel> venue = new ArrayList<>();

    private String name = "";


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        binding.tvName.setText(getValue("special_venue_picked"));

        String model = getIntent().getStringExtra( "venueModel" );

        venue = new Gson().fromJson( model, new TypeToken<List<VenueObjectModel>>() {}.getType() );

        name = getIntent().getStringExtra( "title" );


        binding.smallVenueRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
        binding.smallVenueRecycler.setAdapter( adapter );

        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        binding.tvTitle.setText( name );

        adapter.updateData( venue );


    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener( view -> {
            onBackPressed();
        } );

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivitySmallVenueComponentSeeAllBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class SmallVenueBlockAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy( parent, R.layout.item_venue_recycler );
            return new VenueViewHolder( view );
        }

        private boolean hasStory(String venueId) {
            HomeObjectModel homeObjectModel = SessionManager.shared.geHomeBlockData();
            if (homeObjectModel != null) {
                Optional<VenueObjectModel> tmpVenue = homeObjectModel.getStories().stream().filter( p -> Objects.equals( p.getId(), venueId ) ).findFirst();
                return tmpVenue.isPresent();
            }
            return false;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            VenueViewHolder viewHolder = (VenueViewHolder) holder;
            VenueObjectModel model = (VenueObjectModel) getItem( position );
            if (model != null) {
                viewHolder.mBinding.txtName.setText( model.getName() );
                viewHolder.mBinding.txtAddress.setText( model.getAddress() );
                Graphics.loadRoundImage(model.getLogo(), viewHolder.mBinding.image);

                viewHolder.mBinding.getRoot().setOnClickListener( v -> Graphics.openVenueDetail( activity, model.getId() ) );
                if (hasStory( model.getId() )) {
                    Graphics.setStoryRing( model.getId(), viewHolder.mBinding.roundLinear );
                }
                viewHolder.mBinding.roundLinear.setOnClickListener( view -> {
                    HomeObjectModel homeObjectModel = SessionManager.shared.geHomeBlockData();
                    List<VenueObjectModel> matchingStories = homeObjectModel.getStories().stream().filter( model1 -> model1.getId().equals( model.getId() ) ).collect( Collectors.toList() );
                    if (!matchingStories.isEmpty()) {
                        Intent intent = new Intent( activity, StoryViewActivity.class );
                        intent.putExtra( "stories", new Gson().toJson( matchingStories ) );
                        intent.putExtra( "selectedPosition", 0 );
                        activity.startActivity( intent );
                    }
                } );
            }
        }

        public class VenueViewHolder extends RecyclerView.ViewHolder {

            private final ItemVenueRecyclerBinding mBinding;

            public VenueViewHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = ItemVenueRecyclerBinding.bind( itemView );
                mBinding.btnContinue.setTxtTitle(getValue("view"));
            }
        }
    }


    // endregion
    // --------------------------------------
}