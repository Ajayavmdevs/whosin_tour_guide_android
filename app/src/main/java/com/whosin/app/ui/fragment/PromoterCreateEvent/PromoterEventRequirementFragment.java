package com.whosin.app.ui.fragment.PromoterCreateEvent;

import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.databinding.FragmentPromoterEventRequirementBinding;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.ui.controller.promoter.PromoterRequirementsView;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PromoterEventRequirementFragment extends BaseFragment {

    private FragmentPromoterEventRequirementBinding binding;

    private final PromoterProfileManager promoterManager = PromoterProfileManager.shared;

    private final JsonObject promoterEventObject = promoterManager.promoterEventObject;

    private final PromoterEventModel promoterEventModel = promoterManager.promoterEventModel;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {

       binding = FragmentPromoterEventRequirementBinding.bind(view);

       setupLayouts(promoterEventModel);

    }

    @Override
    public void setListeners() {

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_promoter_event_requirement;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setupLayouts(PromoterEventModel promoterEventModel) {
        boolean check = promoterManager.isEventEdit || promoterManager.isEventSaveToDraft || promoterManager.isEventRepost;

        if (promoterEventModel != null && check) {
            setupLayout(binding.reqrimentLayout, promoterEventModel.getRequirementsAllowed(), true);
            setupLayout(binding.benefitsLayout, promoterEventModel.getRequirementsNotAllowed(), false);
            setupLayout(binding.benfitsIncludedLayout, promoterEventModel.getBenefitsIncluded(), true);
            setupLayout(binding.benfitNotIncludedlayout, promoterEventModel.getBenefitsNotIncluded(), false);
        } else {
            binding.reqrimentLayout.setUpData(null, requireActivity(), getChildFragmentManager(), true);
            binding.benefitsLayout.setUpData(null, requireActivity(), getChildFragmentManager(), false);
            binding.benfitsIncludedLayout.setUpData(null, requireActivity(), getChildFragmentManager(), true);
            binding.benfitNotIncludedlayout.setUpData(null, requireActivity(), getChildFragmentManager(), false);
        }

        binding.reqrimentLayout.setTitles(getValue("requirements"),getValue("add_the_requirements_allowed"));
        binding.benefitsLayout.setTitles(getValue(""),getValue("add_the_requirements_that_are_not_allowed"));
        binding.benfitsIncludedLayout.setTitles(getValue("benefits"),getValue("add_the_benefits_that_are_included"));
        binding.benfitNotIncludedlayout.setTitles(getValue(""),getValue("add_the_requirements_that_are_not_allowed"));

    }

    private void setupLayout(PromoterRequirementsView view, List<String> data, boolean flag) {
        if (data != null && !data.isEmpty()) {
            view.setUpData(data, requireActivity(), getChildFragmentManager(), flag);
        } else {
            view.setUpData(null, requireActivity(), getChildFragmentManager(), flag);
        }
    }


    private void addRequirementsToEventObject(List<String> requirementList, String key) {
        JsonArray jsonArray = new JsonArray();
        if (requirementList != null && !requirementList.isEmpty()) {
            requirementList.forEach(jsonArray::add);
        }
        promoterEventObject.add(key, jsonArray);
    }


    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------


    public boolean isDataValid() {

        if (binding.reqrimentLayout.requirementList.isEmpty()){
            Toast.makeText(requireActivity(), getValue("add_requirements_allowed"), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.benfitsIncludedLayout.requirementList.isEmpty()){
            Toast.makeText(requireActivity(), getValue("add_benefits"), Toast.LENGTH_SHORT).show();
            return false;
        }

        addRequirementsToEventObject(binding.reqrimentLayout.requirementList, "requirementsAllowed");
        addRequirementsToEventObject(binding.benefitsLayout.requirementList, "requirementsNotAllowed");
        addRequirementsToEventObject(binding.benfitsIncludedLayout.requirementList, "benefitsIncluded");
        addRequirementsToEventObject(binding.benfitNotIncludedlayout.requirementList, "benefitsNotIncluded");


        return true;
    }


    public void saveToDraft(){

        if (!binding.reqrimentLayout.requirementList.isEmpty()) {
            addRequirementsToEventObject(binding.reqrimentLayout.requirementList, "requirementsAllowed");
        }

        if (!binding.benefitsLayout.requirementList.isEmpty()) {
            addRequirementsToEventObject(binding.benefitsLayout.requirementList, "requirementsNotAllowed");
        }

        if (!binding.benfitNotIncludedlayout.requirementList.isEmpty()) {
            addRequirementsToEventObject(binding.benfitNotIncludedlayout.requirementList, "benefitsNotIncluded");
        }


        if (!binding.benfitsIncludedLayout.requirementList.isEmpty()) {
            addRequirementsToEventObject(binding.benfitsIncludedLayout.requirementList, "benefitsIncluded");
        }


    }



    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
}