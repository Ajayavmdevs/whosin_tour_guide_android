package com.whosin.business.comman;

import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.BannerModel;
import com.whosin.business.service.models.CategoriesModel;
import com.whosin.business.service.models.ContactUsBlockModel;
import com.whosin.business.service.models.CustomComponentModel;
import com.whosin.business.service.models.newExploreModels.ExploreBlockModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ExploreFilterManager {


    public void filterInBackground(final List<ExploreBlockModel> originalList, final CommanCallback<List<ExploreBlockModel>> callback) {
        Thread thread = new Thread(() -> {
            List<ExploreBlockModel> filteredList = performFiltering(originalList);
            callback.onReceive(filteredList);
        });

        // Start the background thread
        thread.start();
    }

    private List<ExploreBlockModel> performFiltering(List<ExploreBlockModel> originalList) {
        List<String> removeTypes = Arrays.asList("nearby");
        originalList.removeIf(model -> removeTypes.contains(model.getType()));
        originalList.removeIf(model -> {
            switch (model.getType()) {
                case "ticket":
                    if (model.getTickets() == null || model.getTickets().isEmpty()) { return true; }
                    List<RaynaTicketDetailModel> tmpTicket = new ArrayList<>();
                    model.getTickets().forEach(p -> {
                        if (SessionManager.shared.geExploreBlockData() != null){
                            Optional<RaynaTicketDetailModel> ticket = SessionManager.shared.geExploreBlockData().getTickets().stream().filter(v -> v.getId().equals(p)).findFirst();
                            ticket.ifPresent(tmpTicket::add);
                        }
                    });
                    model.ticketList = tmpTicket;
                    return tmpTicket.isEmpty();
                case "juniper-hotel":
                    if (model.getHotels() == null || model.getHotels().isEmpty()) {
                        return true;
                    }
                    List<RaynaTicketDetailModel> tmpHotelList = new ArrayList<>();
                    model.getHotels().forEach(p -> {
                        if (SessionManager.shared.geExploreBlockData() != null) {
                            Optional<RaynaTicketDetailModel> ticket = SessionManager.shared.geExploreBlockData().getTickets().stream().filter(v -> v.getId().equals(p)).findFirst();
                            ticket.ifPresent(tmpHotelList::add);
                        }
                    });
                    model.ticketList = tmpHotelList;
                    return tmpHotelList.isEmpty();
                case "city":
                    if (model.getCities() == null || model.getCities().isEmpty()) {
                        return true;
                    }
                    List<CategoriesModel> tmpCity = new ArrayList<>();
                    model.getCities().forEach(p -> {
                        if (SessionManager.shared.geExploreBlockData() != null) {
                            Optional<CategoriesModel> ticket = SessionManager.shared.geExploreBlockData().getCities().stream().filter(v -> v.getId().equals(p)).findFirst();
                            ticket.ifPresent(tmpCity::add);
                        }
                    });
                    model.citiesList = tmpCity;
                    return tmpCity.isEmpty();
                case "category":
                    if (model.getCategories() == null || model.getCategories().isEmpty()) {
                        return true;
                    }
                    List<CategoriesModel> tmpCategory = new ArrayList<>();
                    model.getCategories().forEach(p -> {
                        if (SessionManager.shared.geExploreBlockData() != null) {
                            Optional<CategoriesModel> ticket = SessionManager.shared.geExploreBlockData().getCategories().stream().filter(v -> v.getId().equals(p)).findFirst();
                            ticket.ifPresent(tmpCategory::add);
                        }
                    });
                    model.categoryList = tmpCategory;
                    return tmpCategory.isEmpty();
                case "big-category":
                    if (model.getBanners() == null || model.getBanners().isEmpty()) {
                        return true;
                    }
                    List<BannerModel> tmpBigCategoryList = new ArrayList<>();
                    model.getBanners().forEach(p -> {
                        if (SessionManager.shared.geExploreBlockData() != null) {
                            Optional<BannerModel> ticket = SessionManager.shared.geExploreBlockData().getBanners().stream().filter(v -> v.getId().equals(p)).findFirst();
                            ticket.ifPresent(tmpBigCategoryList::add);
                        }
                    });
                    model.bigCategoryList = tmpBigCategoryList;
                    return tmpBigCategoryList.isEmpty();
                case "small-category":
                    if (model.getBanners() == null || model.getBanners().isEmpty()) {
                        return true;
                    }
                    List<BannerModel> tmpSmallCategoryList = new ArrayList<>();
                    model.getBanners().forEach(p -> {
                        if (SessionManager.shared.geExploreBlockData() != null) {
                            Optional<BannerModel> ticket = SessionManager.shared.geExploreBlockData().getBanners().stream().filter(v -> v.getId().equals(p)).findFirst();
                            ticket.ifPresent(tmpSmallCategoryList::add);
                        }
                    });
                    model.smallCategoryList = tmpSmallCategoryList;
                    return tmpSmallCategoryList.isEmpty();
                case "banner":
                    if (model.getBanners() == null || model.getBanners().isEmpty()) {
                        return true;
                    }
                    List<BannerModel> tmpBannerList = new ArrayList<>();
                    model.getBanners().forEach(p -> {
                        if (SessionManager.shared.geExploreBlockData() != null) {
                            Optional<BannerModel> ticket = SessionManager.shared.geExploreBlockData().getBanners().stream().filter(v -> v.getId().equals(p)).findFirst();
                            ticket.ifPresent(tmpBannerList::add);
                        }
                    });
                    model.bannerList = tmpBannerList;
                    return tmpBannerList.isEmpty();
                case "custom-component":
                    if (model.getCustomComponents() == null || model.getCustomComponents().isEmpty()) {
                        return true;
                    }
                    List<CustomComponentModel> tmpCustomComponentsList = new ArrayList<>();
                    model.getCustomComponents().forEach(p -> {
                        if (SessionManager.shared.geExploreBlockData() != null) {
                            Optional<CustomComponentModel> ticket = SessionManager.shared.geExploreBlockData().getCustomComponents().stream().filter(v -> v.getId().equals(p)).findFirst();
                            ticket.ifPresent(tmpCustomComponentsList::add);
                        }
                    });
                    model.customComponentModelList = tmpCustomComponentsList;
                    return tmpCustomComponentsList.isEmpty();
                case "contact-us":
                    List<ContactUsBlockModel> contactList = model.getContactUsBlock();
                    if (contactList == null || contactList.isEmpty()) {
                        return true;
                    }
                    boolean anyEnabled = contactList.stream().anyMatch(b -> b.isEnabled(ContactUsBlockModel.ContactBlockScreens.EXPLORE));
                    return !anyEnabled;
                default:
                    return true;
            }
        });
        return originalList;
    }

}
