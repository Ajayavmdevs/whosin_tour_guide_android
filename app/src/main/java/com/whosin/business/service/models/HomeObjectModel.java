package com.whosin.business.service.models;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HomeObjectModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("blocks")
    @Expose
    private List<HomeBlockModel> blocks;
    @SerializedName("stories")
    @Expose
    private List<VenueObjectModel> stories;
    @SerializedName("categories")
    @Expose
    private List<CategoriesModel> categories;
    @SerializedName("ticketCategories")
    @Expose
    private List<CategoriesModel> ticketCategories;

    @SerializedName("activities")
    @Expose
    private List<ActivityDetailModel> activities;

    @SerializedName("events")
    @Expose
    private List<EventModel> events;

    @SerializedName("offers")
    @Expose
    private List<OffersModel> offers;

    @SerializedName("venues")
    @Expose
    private List<VenueObjectModel> venues;
    @SerializedName("yachts")
    @Expose
    private List<YachtDetailModel> yachts;
    @SerializedName("yachtOffers")
    @Expose
    private List<YachtsOfferModel> yachtOffers;
    @SerializedName("users")
    @Expose
    private List<ContactListModel> users;
    @SerializedName("membershipPackages")
    @Expose
    private List<MemberShipModel> membershipPackages;
    @SerializedName("tickets")
    @Expose
    private List<RaynaTicketDetailModel> tickets;
    @SerializedName("customComponents")
    @Expose
    private List<CustomComponentModel> customComponents;
    @SerializedName("cities")
    @Expose
    private List<CategoriesModel> cities;
    @SerializedName("banners")
    @Expose
    private List<BannerModel> banners;


    public List<HomeBlockModel> getBlocks() {
        return (blocks == null) ? new ArrayList<>() : blocks;
    }

    public List<HomeBlockModel> getHomeBlocks() {
        List<HomeBlockModel> tmpList = new ArrayList<>();
        HomeBlockModel storyBlock = new HomeBlockModel();
        storyBlock.setId("1");
        List<VenueObjectModel> storyList = getStories();
//        Comparator<VenueObjectModel> dateComparator = Comparator.comparing((VenueObjectModel m) -> m.getStories().get(0).getCreatedAt()).reversed();
        Comparator<VenueObjectModel> dateComparator = Comparator.comparing(
                (VenueObjectModel m) -> {
                    if (m == null || m.getStories() == null || m.getStories().isEmpty()) {
                        return null;
                    }
                    return m.getStories().get(0).getCreatedAt();
                },
                Comparator.nullsLast(Comparator.naturalOrder())
        ).reversed();

        storyList.sort(dateComparator);
        List<VenueObjectModel> sortedVenueList = storyList.stream().sorted(Comparator.comparing(item -> Graphics.isStoryView(item.getId()))).collect(Collectors.toList());
        for (VenueObjectModel model : storyList){
            Log.d("Strory", model.getName());
        }
        if (!sortedVenueList.isEmpty()){
            storyBlock.setStories(sortedVenueList);
            storyBlock.setType("stories");
        }


        HomeBlockModel categoryBlock = new HomeBlockModel();
        categoryBlock.setId("2");
        categoryBlock.setHomeBlockCategory(getCategories());
        categoryBlock.setType("categories");

        tmpList.add(0, storyBlock);
        tmpList.add(1, categoryBlock);

        //UPDATE PROFILE
//        UserDetailModel user = SessionManager.shared.getUser();
//        if (user != null && !Utils.isGuestLogin()) {
//            if (TextUtils.isEmpty(user.getBio()) ||
//                    TextUtils.isEmpty(user.getEmail()) ||
//                    TextUtils.isEmpty(user.getFirstName()) ||
//                    TextUtils.isEmpty(user.getLastName()) || TextUtils.isEmpty(user.getPhone())|| TextUtils.isEmpty(user.getInstagram())
//            ) {
//                HomeBlockModel completeProfileBanner = new HomeBlockModel();
//                completeProfileBanner.setType("complete-profile");
//                tmpList.add(2, completeProfileBanner);
//            }
//        }

        if (!getBlocks().isEmpty()) {
            tmpList.addAll(getBlocks());
        }
        return tmpList;
    }

    public void setBlocks(List<HomeBlockModel> blocks) {
        this.blocks = blocks;
    }

    public List<VenueObjectModel> getStories() {
        return (stories == null) ? new ArrayList<>() : stories;
    }

    public void setStories(List<VenueObjectModel> stories) {
        this.stories = stories;
    }

    public List<CategoriesModel> getCategories() {
        return (categories == null) ? new ArrayList<>() : categories;

    }

    public void setCategories(List<CategoriesModel> categories) {
        this.categories = categories;
    }

    public List<CategoriesModel> getTicketCategories() {
        return (ticketCategories == null) ? new ArrayList<>() : ticketCategories;
    }

    public void setTicketCategories(List<CategoriesModel> ticketCategories) {
        this.ticketCategories = ticketCategories;
    }

    public List<ActivityDetailModel> getActivities() {
        return (activities == null) ? new ArrayList<>() : activities;
    }

    public void setActivities(List<ActivityDetailModel> activities) {
        this.activities = activities;
    }

    public List<EventModel> getEvents() {
        return (events == null) ? new ArrayList<>() : events;
    }

    public void setEvents(List<EventModel> events) {
        this.events = events;
    }

    public List<OffersModel> getOffers() {
        return (offers == null) ? new ArrayList<>() : offers;
    }

    public void setOffers(List<OffersModel> offers) {
        this.offers = offers;
    }

    public List<VenueObjectModel> getVenues() {
        return (venues == null) ? new ArrayList<>() : venues;
    }

    public void setVenues(List<VenueObjectModel> venues) {
        this.venues = venues;
    }

    public List<YachtDetailModel> getYachts() {
        return (yachts == null) ? new ArrayList<>() : yachts;
    }

    public void setYachts(List<YachtDetailModel> yachts) {this.yachts = yachts;
    }
    public List<YachtsOfferModel> getYachtOffers() {
        return (yachtOffers == null) ? new ArrayList<>() : yachtOffers;
    }

    public void setYachtOffers(List<YachtsOfferModel> yachtOffers) {
        this.yachtOffers = yachtOffers;
    }
    public List<ContactListModel> getUsers() {
        return (users == null) ? new ArrayList<>() : users;
    }

    public void setUsers(List<ContactListModel> users) {
        this.users = users;
    }

    public List<MemberShipModel> getMembershipPackages() {
        return membershipPackages;
    }

    public void setMembershipPackages(List<MemberShipModel> membershipPackages) {
        this.membershipPackages = membershipPackages;
    }

    public List<RaynaTicketDetailModel> getTickets() {
        return tickets;
    }

    public void setTickets(List<RaynaTicketDetailModel> tickets) {
        this.tickets = tickets;
    }

    public List<CategoriesModel> getCities() {
        return cities;
    }

    public void setCities(List<CategoriesModel> cities) {
        this.cities = cities;
    }

    public List<BannerModel> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerModel> banners) {
        this.banners = banners;
    }

    public List<CustomComponentModel> getCustomComponents() {
        return Utils.notEmptyList(customComponents);
    }

    public void setCustomComponents(List<CustomComponentModel> customComponents) {
        this.customComponents = customComponents;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

}
