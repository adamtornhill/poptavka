package cz.poptavka.sample.domain.demand;

import cz.poptavka.sample.domain.common.DomainObject;

import javax.persistence.Entity;

/**
 * @author Juraj Martinka
 *         Date: 10.4.11
 */
@Entity
public class Template extends DomainObject {

    // TODO: "co chtel basnik rici? - vid Dat. model"
    private String prefilledInputs;

    public String getPrefilledInputs() {
        return prefilledInputs;
    }

    public void setPrefilledInputs(String prefilledInputs) {
        this.prefilledInputs = prefilledInputs;
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Template");
        sb.append("{prefilledInputs='").append(prefilledInputs).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
