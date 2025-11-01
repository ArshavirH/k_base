package com.buildware.kbase.testsupport;

import com.buildware.kbase.testsupport.containers.PostgresContainerSupport;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Testcontainers;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
public class AbstractDataJpaTest extends PostgresContainerSupport {

}
