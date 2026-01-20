package com.capstone.meetingmap.map.service;

import com.capstone.meetingmap.map.entity.PlaceCategoryDetail;
import com.capstone.meetingmap.map.repository.PlaceCategoryDetailRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PlaceCategoryDetailService {
    private final PlaceCategoryDetailRepository placeCategoryDetailRepository;

    public PlaceCategoryDetailService(PlaceCategoryDetailRepository placeCategoryDetailRepository) {
        this.placeCategoryDetailRepository = placeCategoryDetailRepository;
    }

    public PlaceCategoryDetail searchPlaceCategoryDetail(String contentTypeId, String cat1, String cat2, String cat3) {
        switch (cat1) {
            case "A01" -> {
                return placeCategoryDetailRepository.findByContentTypeIdAndCat1(contentTypeId, cat1)
                        .orElse(placeCategoryDetailRepository.findByPlaceCategoryDetailCode("other")
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다"))
                        );
            }
            case "A02" -> {
                switch (cat2) {
                    case "A0201", "A0206" -> {
                        return placeCategoryDetailRepository.findByContentTypeIdAndCat1AndCat2(contentTypeId, cat1, cat2)
                                .orElse(placeCategoryDetailRepository.findByPlaceCategoryDetailCode("other")
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다"))
                                );
                    }
                    case "A0202" -> {
                        return placeCategoryDetailRepository.findByContentTypeIdAndCat1AndCat2AndCat3(contentTypeId, cat1, cat2, cat3)
                                .orElse(placeCategoryDetailRepository.findByPlaceCategoryDetailCode("other")
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다"))
                                );
                    }
                    default -> {
                        return placeCategoryDetailRepository.findByContentTypeIdAndCat1AndCat2(contentTypeId, cat1, null)
                                .orElse(placeCategoryDetailRepository.findByPlaceCategoryDetailCode("other")
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다"))
                                );
                    }
                }
            }
            case "A04", "A05" -> {
                return placeCategoryDetailRepository.findByContentTypeIdAndCat1AndCat2AndCat3(contentTypeId, cat1, cat2, cat3)
                        .orElse(placeCategoryDetailRepository.findByPlaceCategoryDetailCode("other")
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다"))
                        );
            }
            default -> {
                return placeCategoryDetailRepository.findByPlaceCategoryDetailCode("other")
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다"));
            }
        }
    }
}
