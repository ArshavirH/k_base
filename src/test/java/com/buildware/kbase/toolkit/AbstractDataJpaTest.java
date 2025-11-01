package com.buildware.kbase.toolkit;

import com.buildware.kbase.toolkit.containers.PostgresContainerSupport;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
public class AbstractDataJpaTest extends PostgresContainerSupport {

}
