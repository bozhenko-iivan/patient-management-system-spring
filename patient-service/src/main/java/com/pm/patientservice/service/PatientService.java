package com.pm.patientservice.service;

import com.pm.patientservice.exception.PatientNotExistException;
import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();

        return patients.stream().map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.email())) {
            throw new EmailAlreadyExistsException(String.format("A patient of this email %s already exists", patientRequestDTO.email()));
        }

        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotExistException(String.format("A patient with id %s not found", id))
        );

        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.email(), patient.getId())) {
            throw new EmailAlreadyExistsException(String.format("A patient of this email %s already exists", patientRequestDTO.email()));
        }

        patient.setName(patientRequestDTO.name());
        patient.setAddress(patientRequestDTO.address());
        patient.setEmail(patientRequestDTO.email());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.dateOfBirth()));

        patientRepository.save(patient);

        return PatientMapper.toDTO(patient);
    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }
}
