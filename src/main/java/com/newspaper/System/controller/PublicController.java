package com.newspaper.System.controller;

import com.newspaper.System.dto.response.AreaResponseDTO;
import com.newspaper.System.dto.response.CityResponseDTO;
import com.newspaper.System.dto.response.StateResponseDTO;
import com.newspaper.System.service.PublicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private PublicService publicService;

    @GetMapping("/states")
    public List<StateResponseDTO> states() {
        return publicService.getStates();
    }

    @GetMapping("/cities/{stateId}")
    public List<CityResponseDTO> cities(@PathVariable int stateId) {
        return publicService.getCities(stateId);
    }

    @GetMapping("/areas/{cityId}")
    public List<AreaResponseDTO> areas(@PathVariable int cityId) {
        return publicService.getAreas(cityId);
    }
}