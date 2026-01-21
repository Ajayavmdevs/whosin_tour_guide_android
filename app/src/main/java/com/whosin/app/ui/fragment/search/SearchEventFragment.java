package com.whosin.app.ui.fragment.search;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentSearchEventBinding;
import com.whosin.app.databinding.IteamSearchEventBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.SearchEventModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.fragment.comman.BaseFragment;


public class SearchEventFragment extends BaseFragment {

    private FragmentSearchEventBinding binding;
    private String search = "";

    private SearchEventAdapter<SearchEventModel> searchEventAdapter = new SearchEventAdapter<>();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    public SearchEventFragment(String search){

        this.search = search;
    }

    @Override
    public void initUi(View view) {

        binding = FragmentSearchEventBinding.bind( view );
        setSearchEventAdapter();
        requestSearchEvent();
    }

    @Override
    public void setListeners() {

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_search_event;
    }




    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setSearchEventAdapter(){
        binding.searchEventRecycler.setLayoutManager( new LinearLayoutManager( requireActivity(),LinearLayoutManager.VERTICAL,false ) );
        binding.searchEventRecycler.setAdapter( searchEventAdapter );

    }




    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestSearchEvent(){
        DataService.shared( requireActivity() ).requestEventSearch( search, "1", "30", new RestCallback<ContainerListModel<SearchEventModel>>(this) {
            @Override
            public void result(ContainerListModel<SearchEventModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( requireActivity(), R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                searchEventAdapter.updateData(model.data);
            }
        } );
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class SearchEventAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder>{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.iteam_search_event ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            SearchEventModel model = (SearchEventModel) getItem( position );
            viewHolder.binding.eventTitle.setText( model.getTitle() );
            Graphics.loadImage( model.getImage(),viewHolder.binding.ivCover );
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            private IteamSearchEventBinding binding;
            public ViewHolder(@NonNull View itemView) {
                super( itemView );

                binding = IteamSearchEventBinding.bind( itemView );
            }
        }
    }

}
