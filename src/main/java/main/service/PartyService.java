package main.service;

import main.repository.PartyRepository;
import org.springframework.stereotype.Service;

@Service
public class PartyService {

    private final PartyRepository partyRepository;

    public PartyService(PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }


}
