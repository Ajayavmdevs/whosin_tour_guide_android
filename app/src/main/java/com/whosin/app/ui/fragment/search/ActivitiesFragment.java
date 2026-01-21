package com.whosin.app.ui.fragment.search;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentActivitiesBinding;
import com.whosin.app.databinding.ItemSearchActivityBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ActivityDetailModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.home.activity.ActivityListDetail;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class ActivitiesFragment extends BaseFragment {

    private FragmentActivitiesBinding binding;


    private String search = "", page = "1";

    private SearchActivityAdapter<ActivityDetailModel> searchActivityAdapter = new SearchActivityAdapter<>();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    public void initUi(View view) {
        binding = FragmentActivitiesBinding.bind( view );
        setSearchActivityAdapter();
        requestActivitySearch();
    }

    @Override
    public void setListeners() {

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_activities;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void setSearchActivityAdapter() {
        binding.activitySearchRecycleView.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.VERTICAL, false ) );
        binding.activitySearchRecycleView.setAdapter( searchActivityAdapter );
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestActivitySearch() {
        DataService.shared( requireActivity() ).requestActivitySearch( search, page, "30", new RestCallback<ContainerListModel<ActivityDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<ActivityDetailModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( requireActivity(), R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                searchActivityAdapter.updateData( model.data );
            }
        } );
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class SearchActivityAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_search_activity ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            ActivityDetailModel model = (ActivityDetailModel) getItem( position );
            viewHolder.binding.tvName.setText( model.getName() );
            viewHolder.binding.tvAddress.setText( model.getDescription() );
            viewHolder.binding.tvPrice.setText( String.valueOf( model.getPrice() ) );
            Graphics.loadRoundImage( model.getProvider().getLogo(), viewHolder.binding.iconImg );
            viewHolder.binding.tvTitle.setText( model.getProvider().getName() );
            viewHolder.binding.tvAddress.setText( model.getProvider().getAddress() );

            Graphics.loadImage( model.getGalleries().get( 0 ), viewHolder.binding.ivCover );
//            Graphics.applyBlurEffect( requireActivity(), viewHolder.binding.blurView );

            try {
                viewHolder.binding.tvStartTime.setText( new SimpleDateFormat( "dd/MM/yyyy", Locale.ENGLISH).format( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault() ).parse( model.getStartDate() ) ) );
                viewHolder.binding.tvEndDate.setText( new SimpleDateFormat( "dd/MM/yyyy", Locale.ENGLISH ).format( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault() ).parse( model.getEndDate() ) ) );

            } catch (ParseException e) {
                e.printStackTrace();
            }

            viewHolder.itemView.setOnClickListener( view -> {
                Intent intent = new Intent( getActivity(), ActivityListDetail.class );
                intent.putExtra( "activityId", model.getId() ).putExtra( "type", "activities" )
                        .putExtra( "name", model.getName() )
                        .putExtra( "image", model.getProvider().getLogo() )
                        .putExtra( "title", model.getProvider().getName() )
                        .putExtra( "address", model.getProvider().getAddress()
                        );
                startActivity( intent );
            } );
//            getActivity().overridePendingTransition( R.anim.slide_up, R.anim.fade_out );

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemSearchActivityBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );

                binding = ItemSearchActivityBinding.bind( itemView );
            }
        }
    }

}