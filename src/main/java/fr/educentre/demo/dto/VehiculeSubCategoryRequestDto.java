package fr.educentre.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class VehiculeSubCategoryRequestDto {
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
