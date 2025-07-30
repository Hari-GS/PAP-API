package com.example.PAP_API.services;

import com.example.PAP_API.model.HRManager;
import com.example.PAP_API.repository.HRManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HRManagerService {

    private final HRManagerRepository hrManagerRepository;

    @Autowired
    public HRManagerService(HRManagerRepository hrManagerRepository) {
        this.hrManagerRepository = hrManagerRepository;
    }

    public List<HRManager> getAllHRManagers() {
        return hrManagerRepository.findAll();
    }

    public Optional<HRManager> getHRManagerById(Long id) {
        return hrManagerRepository.findById(id);
    }

    public HRManager createHRManager(HRManager hrManager) {
        return hrManagerRepository.save(hrManager);
    }

    public Optional<HRManager> updateHRManager(Long id, HRManager updatedManager) {
        return hrManagerRepository.findById(id).map(existing -> {
            existing.setName(updatedManager.getName());
            existing.setEmail(updatedManager.getEmail());
            existing.setPassword(updatedManager.getPassword());
            existing.setOrganization(updatedManager.getOrganization());
            return hrManagerRepository.save(existing);
        });
    }

    public boolean deleteHRManager(Long id) {
        if (hrManagerRepository.existsById(id)) {
            hrManagerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
