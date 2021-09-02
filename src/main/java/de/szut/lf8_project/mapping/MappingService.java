package de.szut.lf8_project.mapping;

import de.szut.lf8_project.hello.HelloCreateDto;
import de.szut.lf8_project.hello.HelloDto;
import de.szut.lf8_project.hello.HelloEntity;
import org.springframework.stereotype.Service;

@Service
public class MappingService {

    public HelloDto mapHelloEntitytoDto(HelloEntity entity) {
        return new HelloDto(entity.getId(), entity.getMessage());
    }

    public HelloEntity mapHelloDtotoEntity(HelloDto dto) {
        return new HelloEntity(dto.getId(), dto.getMessage());
    }

    public HelloEntity mapHelloCreateDtotoEntity(HelloCreateDto dto) {
        var entity = new HelloEntity();
        entity.setMessage(dto.getMessage());
        return entity;
    }
}
