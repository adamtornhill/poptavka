package cz.poptavka.sample.shared.domain;

import java.io.Serializable;
import java.util.ArrayList;


public class SupplierDetail implements Serializable {

    /**
     * Generated serialVersionUID.
     */
    private static final long serialVersionUID = -8271479725303195283L;

    private Long supplierId;
    private int overallRating;
    private boolean certified;
    private String description;
    private ArrayList<String> localities;
    private ArrayList<String> categories;
    private ArrayList<Integer> services = new ArrayList<Integer>();

    public SupplierDetail() {
    }

    public int getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(int overallRating) {
        this.overallRating = overallRating;
    }

    public boolean isCertified() {
        return certified;
    }

    public void setCertified(boolean certified) {
        this.certified = certified;
    }

    public ArrayList<String> getLocalities() {
        return localities;
    }

    public void setLocalities(ArrayList<String> localities) {
        this.localities = localities;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Integer> getServices() {
        return services;
    }

    public void setServices(ArrayList<Integer> serviceList) {
        this.services = serviceList;
    }

    public void addService(int selectedService) {
        services.add(Integer.valueOf(selectedService));
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getSupplierId() {
        return supplierId;
    }


}
