package it.angrybear.yagl.parsers;

import it.angrybear.yagl.ParserTestHelper;
import it.angrybear.yagl.SerializableFunction;
import it.fulminazzo.yamlparser.configuration.ConfigurationSection;
import it.fulminazzo.yamlparser.configuration.IConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class SerializableFunctionParserTest extends ParserTestHelper<SerializableFunctionParserTest.MockFunction> {

    @Test
    void testNullType() {
        IConfiguration configuration = prepareConfiguration(null);
        ConfigurationSection section = new ConfigurationSection(configuration, "section");
        when(configuration.getConfigurationSection(anyString())).thenReturn(section);
        assertThrowsExactly(IllegalArgumentException.class, () -> getLoader().apply(configuration, "section"));
    }

    @Test
    void testNullContent() {
        IConfiguration configuration = prepareConfiguration(null);
        ConfigurationSection section = new ConfigurationSection(configuration, "section");
        section.set("type", "MOCK_FUNCTION");
        when(configuration.getConfigurationSection(anyString())).thenReturn(section);
        Throwable throwable = assertThrowsExactly(IllegalArgumentException.class, () -> getLoader().apply(configuration, "section"));
        assertEquals("'content' cannot be null", throwable.getMessage());
    }

    @Override
    protected Class<?> getParser() {
        return MockFunctionParser.class;
    }

    static class MockFunctionParser extends SerializableFunctionParser<MockFunction> {

        public MockFunctionParser() {
            super(MockFunction.class);
        }

    }

    static class MockFunction implements SerializableFunction {

    }
}