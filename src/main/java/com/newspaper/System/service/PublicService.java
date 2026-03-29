package com.newspaper.System.service;

import com.newspaper.System.dto.response.*;
import com.newspaper.System.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicService {

    @Autowired
    private StateRepository stateRepo;

    @Autowired
    private CityRepository cityRepo;

    @Autowired
    private AreaRepository areaRepo;

    // ======================
    // STATES
    // ======================
    public List<StateResponseDTO> getStates() {

        return stateRepo.findAll()
                .stream()
                .map(state -> {

                    StateResponseDTO dto = new StateResponseDTO();
                    dto.setStateId(state.getStateId());
                    dto.setName(state.getName());

                    return dto;
                })
                .toList();
    }

    // ======================
    // CITIES
    // ======================
    public List<CityResponseDTO> getCities(int stateId) {

        return cityRepo.findByState_StateId(stateId)
                .stream()
                .map(city -> {

                    CityResponseDTO dto = new CityResponseDTO();
                    dto.setCityId(city.getCityId());
                    dto.setName(city.getName());
                    dto.setStateId(city.getState().getStateId());
                    dto.setStateName(city.getState().getName());

                    return dto;
                })
                .toList();
    }

    // ======================
    // AREAS
    // ======================
    public List<AreaResponseDTO> getAreas(int cityId) {

        return areaRepo.findByCity_CityId(cityId)
                .stream()
                .map(area -> {

                    AreaResponseDTO dto = new AreaResponseDTO();
                    dto.setAreaId(area.getAreaId());
                    dto.setName(area.getName());
                    dto.setPincode(area.getPincode());

                    dto.setCityId(area.getCity().getCityId());
                    dto.setCityName(area.getCity().getName());

                    dto.setStateId(area.getCity().getState().getStateId());
                    dto.setStateName(area.getCity().getState().getName());

                    return dto;
                })
                .toList();
    }
}