package com.pm.patientservice.mapper;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;




@Mapper(componentModel = "spring") // Позволяет внедрять маппер через @Autowired/final
public interface PatientMapper {

    // MapStruct сам поймет, как превратить UUID в String и обратно
    PatientResponseDTO toDTO(Patient patient);

    // MapStruct сам вызовет LocalDate.parse(), если имена полей совпадают
    Patient toModel(PatientRequestDTO patientRequestDTO);

    @Mapping(target = "id", ignore = true) // Don't overwrite the ID
    void updatePatientFromDto(PatientRequestDTO dto, @MappingTarget Patient entity);

}