package com.buildware.kbase.spi;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.With;

public interface ProjectInfoSPI {

    Optional<ProjectInfo> getByCode(String code);

    List<ProjectInfo> listAll();

    record ProjectInfo(UUID id, String code, String basePath) {

    }
}

