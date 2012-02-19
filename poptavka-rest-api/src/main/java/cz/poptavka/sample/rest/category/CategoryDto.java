/*
 * Copyright (C) 2007-2011, GoodData(R) Corporation. All rights reserved.
 */
package cz.poptavka.sample.rest.category;

import java.util.Map;

public class CategoryDto {
    private String code;
    private String name;
    private String description;
    private Map<String, String> links;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "CategoryDto{"
                + "code='" + code + '\''
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", links=" + links
                + '}';
    }
}
