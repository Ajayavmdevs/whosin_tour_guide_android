package com.whosin.app.comman;

import android.text.TextUtils;

import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ActivityDetailModel;
import com.whosin.app.service.models.BannerModel;
import com.whosin.app.service.models.CategoriesModel;
import com.whosin.app.service.models.ContactUsBlockModel;
import com.whosin.app.service.models.CustomComponentModel;
import com.whosin.app.service.models.EventModel;
import com.whosin.app.service.models.ExclusiveDealModel;
import com.whosin.app.service.models.HomeBlockModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.MemberShipModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.VideoComponentModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


public class FilterManager {


    public void filterInBackground(final List<HomeBlockModel> originalList, final CommanCallback<List<HomeBlockModel>> callback) {
        Thread thread = new Thread(() -> {
            List<HomeBlockModel> filteredList = performFiltering(originalList);
            callback.onReceive(filteredList);
        });

        // Start the background thread
        thread.start();
    }

    private List<HomeBlockModel> performFiltering(List<HomeBlockModel> originalList) {
        List<String> removeTypes = Arrays.asList("nearby");
        originalList.removeIf(model -> removeTypes.contains(model.getType()));
        originalList.removeIf(model -> {
            switch (model.getType()) {
                case "video":
//                    List<VideoComponentModel> videoList = model.getVideos().stream().filter(m -> {
//                        if (TextUtils.isEmpty(m.getTicketId()) && TextUtils.isEmpty(m.getVenueId())) {
//                            return false;
//                        } else {
//                            Optional<VenueObjectModel> venueObjectModel = SessionManager.shared.geHomeBlockData().getVenues().stream().filter(p -> p.getId().equals(m.getVenueId())).findFirst();
//                            venueObjectModel.ifPresent(m::setVenue);
//                            return true;
//                        }
//                    }).collect(Collectors.toList());
//                    return videoList.isEmpty();

                List<VideoComponentModel> videoList = model.getVideos().stream().filter(m -> {
                    boolean hasTicketId = !TextUtils.isEmpty(m.getTicketId());
                    boolean hasVenueId = !TextUtils.isEmpty(m.getVenueId());

                    if (!hasTicketId && !hasVenueId) {
                        return false;
                    }

                    if (hasTicketId) {
                        Optional<RaynaTicketDetailModel> ticket = SessionManager.shared.geHomeBlockData().getTickets().stream()
                                .filter(t -> t.getId().equals(m.getTicketId()))
                                .findFirst();
                        ticket.ifPresent(m::setTicketDetailModel);
                    } else {
                        Optional<VenueObjectModel> venue = SessionManager.shared.geHomeBlockData().getVenues().stream()
                                .filter(v -> v.getId().equals(m.getVenueId()))
                                .findFirst();
                        venue.ifPresent(m::setVenue);
                    }

                    return true;
                }).collect(Collectors.toList());

                return videoList.isEmpty();

                case "activity":
                    List<ActivityDetailModel> activityList = SessionManager.shared.geHomeBlockData().getActivities().stream().filter(p -> model.getActivities() != null && model.getActivities().contains(p.getId())).collect(Collectors.toList());
                    model.activityList = activityList;
                    return activityList.isEmpty();
                case "deal":
                    if (model.getDeals().isEmpty()) { return true; }
                    List<ExclusiveDealModel> dealList = new ArrayList<>();
                    model.getDeals().forEach( d -> {
                        Optional<VenueObjectModel> venue = SessionManager.shared.geHomeBlockData().getVenues().stream().filter(v -> v.getId().equalsIgnoreCase(d.getVenueId())).findFirst();
                        if (venue.isPresent()) {
                            d.setVenue(venue.get());
                            dealList.add(d);
                        }
                    });
                    model.setDeals(dealList);
                    return dealList.isEmpty();
                case "custom-offer":
                    if (SessionManager.shared.geHomeBlockData().getOffers().isEmpty()) { return true; }
                    if ((model.getCustomOffers() == null || model.getCustomOffers().isEmpty())) { return true; }
                    AtomicBoolean isVisible = new AtomicBoolean(true);
                    model.getCustomOffers().forEach( cOffer -> {
                        Optional<OffersModel> offer = SessionManager.shared.geHomeBlockData().getOffers().stream().filter(v -> v.getId().equalsIgnoreCase(cOffer.getOfferId())).findFirst();
                        if (offer.isPresent()) {
                            cOffer.setOffer(offer.get());
                            isVisible.set(false);
                        }
                    });
                    return isVisible.get();
                case "custom-venue":
                    if (SessionManager.shared.geHomeBlockData().getVenues().isEmpty()) { return true; }
                    if (model.getCustomVenues() == null || model.getCustomVenues().isEmpty()) { return true; }
                    AtomicBoolean isVisibleVenue = new AtomicBoolean(true);
                    model.getCustomVenues().forEach( cVenue -> {
                        if (SessionManager.shared.geHomeBlockData() != null) {
                            Optional<VenueObjectModel> venue = SessionManager.shared.geHomeBlockData().getVenues().stream().filter(v -> v.getId().equalsIgnoreCase(cVenue.getVenueId())).findFirst();
                            if (venue.isPresent()) {
                                cVenue.setVenue(venue.get());
                                isVisibleVenue.set(false);
                            }
                        }
                    });
                    return isVisibleVenue.get();
                case "event":
                    List<EventModel> events = SessionManager.shared.geHomeBlockData().getEvents().stream().filter(p -> model.getEvents().contains(p.getId()) && p.getCustomVenue() != null ).collect(Collectors.toList());
                    model.eventList = events;
                    return events.isEmpty();
                case "stories":
                    return (model.getStories() == null || model.getStories().isEmpty());
                case "suggested-venues":
                    return (model.getSuggestedVenue() == null || model.getSuggestedVenue().isEmpty());
                case "suggested-users":
                    return (model.getSuggestedUsers() == null || model.getSuggestedUsers().isEmpty());
                case "categories":
                    return (model.getHomeBlockCategory() == null || model.getHomeBlockCategory().isEmpty());
                case "my-outing":
                    if(model.getMyOuting().isEmpty()) { return true; }
                    List<InviteFriendModel> filterData = model.getMyOuting().stream().filter(p -> p.getVenue() != null && p.getUser() != null).collect(Collectors.toList());
                    return filterData.isEmpty();
                case "venue":
                    if (model.getVenues().isEmpty()) { return true; }
                    List<VenueObjectModel> venues = new ArrayList<>();
                    model.getVenues().forEach( p -> {
                        if (SessionManager.shared.geHomeBlockData() != null) {
                            Optional<VenueObjectModel> venue = SessionManager.shared.geHomeBlockData().getVenues().stream().filter(v -> v.getId().equalsIgnoreCase(p)).findFirst();
                            venue.ifPresent(venues::add);
                        }
                    });
                    model.venueList = venues;
                    return venues.isEmpty();
                case "custom-components":
                    model.assignCustomComponentObject();
                    if (model.getCustomComponentModelList() == null || model.getCustomComponentModelList().isEmpty())
                        return false;
                    List<CustomComponentModel> customComponent = model.getCustomComponentModelList().stream().filter(m -> {
                        if (m.getType().equals("ticket")) {
                            Optional<RaynaTicketDetailModel> ticketDetailModel = SessionManager.shared.geHomeBlockData().getTickets().stream().filter(p -> p.getId().equals(m.getTicketId())).findFirst();
                            ticketDetailModel.ifPresent(m::setRaynaTicketDetailModel);
                            return true;
                        } else {
                            Optional<VenueObjectModel> venueObjectModel = SessionManager.shared.geHomeBlockData().getVenues().stream().filter(p -> p.getId().equals(m.getVenueId())).findFirst();
                            venueObjectModel.ifPresent(m::setVenueObjectModel);
                            return true;
                        }
                    }).collect(Collectors.toList());
                    return customComponent.isEmpty();
                case "custom-component":
                    model.assignCustomComponentObject();
                    if (model.getStringCustomComponent() == null || model.getStringCustomComponent().isEmpty())
                        return false;
                    List<CustomComponentModel> fasf = new ArrayList<>();
                    model.getStringCustomComponent().forEach(p -> {
                        if (SessionManager.shared.geHomeBlockData() != null) {
                            Optional<CustomComponentModel> ticket = SessionManager.shared.geHomeBlockData().getCustomComponents().stream().filter(v -> v.getId().equals(p)).findFirst();
                            ticket.ifPresent(fasf::add);
                        }
                    });
                    model.exploreCustomComponent = fasf;
                    return fasf.isEmpty();
                case "offer":
                    if (model.getOffers().isEmpty()) { return true; }
                    List<OffersModel> offers = new ArrayList<>();
                    model.getOffers().forEach(p -> {
                        if (SessionManager.shared.geHomeBlockData() != null) {
                            Optional<OffersModel> offer = SessionManager.shared.geHomeBlockData().getOffers().stream().filter(v -> v.getId().equals(p)).findFirst();
                            offer.ifPresent(offers::add);
                        }
                    });
                    model.offerList = offers;
                    return offers.isEmpty();
                case "membership-package":
                    if (model.getMembershipPackages().isEmpty()) { return true; }
                    List<MemberShipModel> memberShipModel = new ArrayList<>();
                    model.getMembershipPackages().forEach(p -> {
                        if (SessionManager.shared.geHomeBlockData() != null) {
                            Optional<MemberShipModel> offer = SessionManager.shared.geHomeBlockData().getMembershipPackages().stream().filter(v -> v.getId().equalsIgnoreCase(p)).findFirst();
                            offer.ifPresent(memberShipModel::add);
                        }
                    });
                    model.memberShipList = memberShipModel;
                    return memberShipModel.isEmpty();

                case "apply-promoter":
                    return false;
                case "apply-ring":
                    return false;
                case "complete-profile" :
                    return false;
                case AppConstants.ADTYPE:
                    return false;
                case "promoter-events":
                    if (model.getPromoterEvents() == null && model.getPromoterEvents().isEmpty()) {
                        return true;
                    }else {
                        return false;
                    }
                case "ticket":
                    if (model.getTickets() == null || model.getTickets().isEmpty()) { return true; }
                    List<RaynaTicketDetailModel> tmpTicket = new ArrayList<>();
                    model.getTickets().forEach(p -> {
                        if (SessionManager.shared.geHomeBlockData() != null) {
                            Optional<RaynaTicketDetailModel> ticket = SessionManager.shared.geHomeBlockData().getTickets().stream().filter(v -> v.getId().equals(p)).findFirst();
                            ticket.ifPresent(tmpTicket::add);
                        }
                    });
                    model.ticketList = tmpTicket;
                    return tmpTicket.isEmpty();
                case "juniper-hotel":
                    if (model.getHotels() == null || model.getHotels().isEmpty()) {
                        return true;
                    }
                    List<RaynaTicketDetailModel> tmpHotel = new ArrayList<>();
                    model.getHotels().forEach(p -> {
                        if (SessionManager.shared.geHomeBlockData() != null) {
                            Optional<RaynaTicketDetailModel> ticket = SessionManager.shared.geHomeBlockData().getTickets().stream().filter(v -> v.getId().equals(p)).findFirst();
                            ticket.ifPresent(tmpHotel::add);
                        }
                    });
                    model.ticketList = tmpHotel;
                    return tmpHotel.isEmpty();
                case "favorite_ticket":
                    if (model.getFavoriteTicketIds() == null || model.getFavoriteTicketIds().isEmpty()) { return true; }
                    List<RaynaTicketDetailModel> tmpFavTicket = new ArrayList<>();
                    model.getFavoriteTicketIds().forEach(p -> {
                        if (SessionManager.shared.geHomeBlockData() != null) {
                            Optional<RaynaTicketDetailModel> ticket = SessionManager.shared.geHomeBlockData().getTickets().stream().filter(v -> v.getId().equals(p)).findFirst();
                            ticket.ifPresent(tmpFavTicket::add);
                        }
                    });
                    model.favTicketList = tmpFavTicket;
                    return tmpFavTicket.isEmpty();
                case "city":
                    if (model.getCities() == null || model.getCities().isEmpty()) {
                        return true;
                    }
                    List<CategoriesModel> tmpCity = new ArrayList<>();
                    model.getCities().forEach(p -> {
                        if (SessionManager.shared.geHomeBlockData() != null) {
                            Optional<CategoriesModel> ticket = SessionManager.shared.geHomeBlockData().getCities().stream().filter(v -> v.getId().equals(p)).findFirst();
                            ticket.ifPresent(tmpCity::add);
                        }
                    });
                    model.citiesList = tmpCity;
                    return tmpCity.isEmpty();
                case "ticket-category":
                    if (model.getTicketCategories() == null || model.getTicketCategories().isEmpty()) {
                        return true;
                    }
                    List<CategoriesModel> tmpCategory = new ArrayList<>();
                    model.getTicketCategories().forEach(p -> {
                        if (SessionManager.shared.geHomeBlockData() != null) {
                            Optional<CategoriesModel> ticket = SessionManager.shared.geHomeBlockData().getTicketCategories().stream().filter(v -> v.getId().equals(p)).findFirst();
                            ticket.ifPresent(tmpCategory::add);
                        }
                    });
                    model.ticketCategory = tmpCategory;
                    return tmpCategory.isEmpty();
                case "big-category":
                    if (model.getCategories() == null || model.getCategories().isEmpty()) {
                        return true;
                    }
                    List<BannerModel> tmpBigCategoryList = new ArrayList<>();
                    model.getCategories().forEach(p -> {
                        if (SessionManager.shared.geHomeBlockData() != null) {
                            Optional<BannerModel> ticket = SessionManager.shared.geHomeBlockData().getBanners().stream().filter(v -> v.getTypeId().equals(p)).findFirst();
                            ticket.ifPresent(tmpBigCategoryList::add);
                        }
                    });
                    model.bigCategoryList = tmpBigCategoryList;
                    return tmpBigCategoryList.isEmpty();
                case "banner":
                    if (model.getBanners() == null || model.getBanners().isEmpty()) {
                        return true;
                    }
                    List<BannerModel> tmpBannerList = new ArrayList<>();
                    model.getBanners().forEach(p -> {
                        if (SessionManager.shared.geHomeBlockData() != null) {
                            Optional<BannerModel> ticket = SessionManager.shared.geHomeBlockData().getBanners().stream().filter(v -> v.getId().equals(p)).findFirst();
                            ticket.ifPresent(tmpBannerList::add);
                        }
                    });
                    model.bannerList = tmpBannerList;
                    return tmpBannerList.isEmpty();
                case "contact-us":
                    List<ContactUsBlockModel> contactList = model.getContactUsBlock();
                    if (contactList == null || contactList.isEmpty()) {
                        return true;
                    }
                    boolean anyEnabled = contactList.stream().anyMatch(b -> b.isEnabled(ContactUsBlockModel.ContactBlockScreens.HOME));
                    return !anyEnabled;

//                case "yacht":
//                    if (model.getYachts().isEmpty()) { return true; }
//                    List<YachtDetailModel> yacht = new ArrayList<>();
//                    model.getYachts().forEach( p -> {
//                        if (SessionManager.shared.geHomeBlockData() != null) {
//                            Optional<YachtDetailModel> yacht1 = SessionManager.shared.geHomeBlockData().getYachts().stream().filter(v -> v.getId().equalsIgnoreCase(p)).findFirst();
//                            yacht1.ifPresent(yacht::add);
//                        }
//                    });
//                    model.yachtList = yacht;
//                    return yacht.isEmpty();
//
//
//                case "yacht-offer":
//                    if (model.getYachtOffers().isEmpty()) { return true; }
//                    List<YachtsOfferModel> yachtOfferList = new ArrayList<>();
//                    model.getYachtOffers().forEach( p -> {
//                        if (SessionManager.shared.geHomeBlockData() != null) {
//                            Optional<YachtsOfferModel> yacht1 = SessionManager.shared.geHomeBlockData().getYachtOffers().stream().filter(v -> v.getId().equalsIgnoreCase(p)).findFirst();
//                            yacht1.ifPresent(yachtOfferList::add);
//                        }
//                    });
//                    model.yachtOfferList = yachtOfferList;
//                    return yachtOfferList.isEmpty();
                default:
                    return true;
            }
        });
        return originalList;
    }
}

