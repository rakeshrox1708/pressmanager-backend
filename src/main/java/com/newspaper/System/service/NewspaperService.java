package com.newspaper.System.service;

import com.newspaper.System.dto.request.NewspaperRequestDTO;
import com.newspaper.System.dto.response.NewspaperResponseDTO;
import com.newspaper.System.model.Newspaper;
import com.newspaper.System.repository.NewspaperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewspaperService {

    @Autowired
    private NewspaperRepository newspaperRepo;

    public NewspaperResponseDTO add(NewspaperRequestDTO dto) {

        Newspaper paper = new Newspaper();
        paper.setName(dto.name);
        paper.setLanguage(dto.language);
        paper.setDailyPrice(dto.dailyPrice);
        paper.setMonthlyPrice(dto.monthlyPrice);

        paper = newspaperRepo.save(paper);

        NewspaperResponseDTO res = new NewspaperResponseDTO();
        res.newspaperId = paper.getNewspaperId();
        res.name = paper.getName();
        res.language = paper.getLanguage();
        res.dailyPrice = paper.getDailyPrice();
        res.monthlyPrice = paper.getMonthlyPrice();

        return res;
    }
}