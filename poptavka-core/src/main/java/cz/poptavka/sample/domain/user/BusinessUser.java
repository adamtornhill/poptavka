package cz.poptavka.sample.domain.user;

import cz.poptavka.sample.domain.address.Address;
import cz.poptavka.sample.domain.invoice.Invoice;
import cz.poptavka.sample.domain.product.UserService;
import cz.poptavka.sample.domain.settings.Settings;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

/**
 * Represents essential group users in application that have business relationship to us.
 * <p>
 * Three main categories of this users exist:
 * <ul>
 *  <li>Client - is searching for some services at most situations.</li>
 *  <li>Supplier - provides some services to clients.</li>
 *  <li>Partner - a subject that has partnership beneficial to both parts </li>
 * </ul>
 *
 * @author Juraj Martinka
 *         Date: 28.1.11
 */
@Entity
@Audited
public class BusinessUser extends User {

    @Enumerated(value = EnumType.STRING)
    private BusinessType businessType;

    /** Company is filled if this object represents the company. For private persons it is null at most situations. */
    @OneToOne
    @Cascade(value = CascadeType.ALL)
    private Company company;

    /** Represents person. It is either the business user itself or contact person for company. */
    @OneToOne
    @Cascade(value = CascadeType.ALL)
    private Person person;


    /** All user's addresses. */
    @OneToMany
    @NotAudited
    private List<Address> addresses;

    /** Flag if user has already been verified. If true then it is a "permanent" user, otherwise it is temporary user.*/
    private boolean verified;

    @OneToMany(mappedBy = "user")
    @NotAudited
    private List<UserService> userServices;


    @OneToMany
    @NotAudited
    private List<Invoice> invoices;


    @OneToOne
    @NotAudited
    private Settings settings;



    //-------------------------- GETTERS AND SETTERS -------------------------------------------------------------------
    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public List<UserService> getUserServices() {
        return userServices;
    }

    public void setUserServices(List<UserService> userServices) {
        this.userServices = userServices;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }


    //-------------------------- End of GETTERS AND SETTERS ------------------------------------------------------------


    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("BusinessUser");
        sb.append("{id=").append(getId());
        sb.append(", company=").append(company);
        sb.append(", person=").append(person);
        sb.append(", verified=").append(verified);
        sb.append('}');
        return sb.toString();
    }
}
