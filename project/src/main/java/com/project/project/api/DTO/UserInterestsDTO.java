package com.project.project.api.DTO;
import java.util.List;

public class UserInterestsDTO {
    private List<String> interests;

    // Constructor
    public UserInterestsDTO() {
        // Default constructor for Spring to create the object
    }

    public UserInterestsDTO(List<String> interests) {
        this.interests = interests;
    }

    // Getter and Setter
    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }
}