package main.service;

import main.repository.MobRepository;
import org.springframework.stereotype.Service;

@Service
public class MobService {

    private final MobRepository mobRepository;

    public MobService(MobRepository mobRepository) {
        this.mobRepository = mobRepository;
    }



}
