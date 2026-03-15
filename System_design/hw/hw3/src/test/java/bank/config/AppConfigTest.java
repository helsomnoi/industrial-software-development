package bank.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class AppConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void objectMapperBean_ShouldExist() {
        assertNotNull(context.getBean(ObjectMapper.class));
    }

    @Test
    void yamlMapperBean_ShouldExist() {
        assertNotNull(context.getBean(YAMLMapper.class));
    }
}