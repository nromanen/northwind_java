package database;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DBStructureTest {
    private static boolean allColumnsExists = false;
    private static Util util;

    @BeforeAll
    public static void beforeAll() throws SQLException, IOException {
        util = new Util();
        util.executeFile("init.sql");
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {"employees"})
    void existTable(String tableName) throws SQLException {
        boolean actual = util.isTableExist(tableName);
        assertEquals(true, actual, "Table " + tableName + " doesn't exists");
    }

    @ParameterizedTest(name = "PK `{0}` for `{1}`")
    @CsvSource({"employee_id,employees", "product_id,products"})
    void checkPK(String PKName, String tableName) throws SQLException {
        List<String> actual = util.getPKs(tableName);

        assertTrue(actual.contains(PKName), String.format("For table %s should be present PK named %s", tableName, PKName));
    }

    @ParameterizedTest(name = "FK `{1}` for `{0}`")
    @MethodSource
    void checkFKs(String tableName, List<String> expected) throws SQLException {
        List<String> actual = util.getFKs(tableName);

        SoftAssertions assertions = new SoftAssertions();
        expected.forEach(outer -> assertions.assertThat(actual.stream().anyMatch(e -> e.contains(outer)))
                .withFailMessage(String.format("Table %s should contains FK with %s", tableName, outer))
                .isTrue());
        assertions.assertAll();
    }

    private static Stream<Arguments> checkFKs() {
        return Stream.of(
                Arguments.of("products", List.of("REFERENCES categories"))
        );
    }

    @ParameterizedTest(name = "not null `{1}` for `{0}`")
    @CsvSource({"products,discontinued"})
    void checkNotNull(String tableName, String columnName) throws SQLException {
        List<String> actual = util.getNotNull(tableName);
        assertTrue(actual.contains(columnName), String.format("Column %s in table %s should be not null", columnName, tableName));
    }

    @ParameterizedTest(name = "unique `{1}` for `{0}`")
    @CsvSource({"customers, phone_number", "cars, number"})
    void checkUnique(String tableName, String columnName) throws SQLException {
        List<String> actual = util.getUnique(tableName);
        assertTrue(actual.contains(columnName), String.format("Column %s in table %s should be unique", columnName, tableName));
    }

    @DisplayName("`car_type` presents")
    @Test
    void checkType() throws SQLException {
        String typeName = "car_type";
        assertTrue(util.isTypeExist(typeName), String.format("Type %s should be presents", typeName));
    }

    @Test
    void checkCheckConstraint() throws SQLException {
        String tableName = "customers";
        String expected = "phone_number";
        List<String> actual = util.getCheckConstraints(tableName);
        assertTrue(actual.stream().anyMatch(e -> e.contains(expected)), String.format("Column %s in table %s should contains check constraint", expected, tableName));
    }
}