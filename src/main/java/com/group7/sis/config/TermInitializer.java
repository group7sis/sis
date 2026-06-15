package com.group7.sis.config;

import java.time.LocalDate;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.group7.sis.entity.academic.TermEntity;
import com.group7.sis.entity.academic.TermEntity.TermStatus;
import com.group7.sis.repository.TermRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TermInitializer implements ApplicationRunner{
    private final TermRepository termRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (termRepository.count() == 0){
            TermEntity term = new TermEntity();

            int startYear = LocalDate.now().getYear();
            int endYear = LocalDate.now().getYear()+1;

            String currentYear = String.valueOf(startYear) + "-" + String.valueOf(endYear);
            term.setAcademicYear(currentYear);
            term.setSemester(1);
            term.setStatus(TermStatus.CLOSED);

            termRepository.save(term);
        }
	}
}
