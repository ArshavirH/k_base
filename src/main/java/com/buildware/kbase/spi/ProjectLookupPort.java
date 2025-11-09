package com.buildware.kbase.spi;

import java.util.List;
import java.util.Optional;

public interface ProjectLookupPort {

    Optional<ProjectInfo> getByCode(String code);

    List<ProjectInfo> listAll();
}

