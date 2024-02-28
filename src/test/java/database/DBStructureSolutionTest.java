package database;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DBStructureSolutionTest {
    private static boolean allColumnsExists = false;
    private static Util util;

    @BeforeAll
    public static void beforeAll() throws SQLException, IOException {
        util = new Util();
        util.executeFile("init.sql");
    }

    @ParameterizedTest
    @ValueSource(strings = {"cars", "customers", "agreements"})
    void existTable(String tableName) throws SQLException {
        boolean actual = util.isTableExist(tableName);
        assertEquals(true, actual, "Table " + tableName + " doesn't exists");
    }


    @Order(1)
    @ParameterizedTest
    @CsvFileSource(resources = {"/cars.csv", "/customers.csv", "/agreements.csv"}, numLinesToSkip = 1)
    void existColumn(String tableName, String columnName) throws SQLException {
        boolean actual = util.isColumnInTableExist(tableName, columnName);
        assertEquals(true, actual, "Column " + columnName + " in table " + tableName + " doesn't exists");
        allColumnsExists = true;
    }

    @Order(2)
    @ParameterizedTest
    @CsvFileSource(resources = {"/cars.csv", "/customers.csv", "/agreements.csv"}, numLinesToSkip = 1)
    void columnType(String tableName, String columnName, String columnType) throws SQLException {
        assumeTrue(allColumnsExists, "Skipping test for columns type because some tests for presence columns failed.");

        String actual = util.getColumnType(tableName, columnName);
        assertEquals(columnType, actual, String.format("Type for column %s should be %s", columnName, columnType));
    }

    @ParameterizedTest
    @CsvSource({"id,customers", "id,cars", "customer_id,agreements", "car_id,agreements"})
    void checkPK(String PKName, String tableName) throws SQLException {
        List<String> actual = util.getPKs(tableName);

        assertTrue(actual.contains(PKName), String.format("For table %s should be present PK named %s", tableName, PKName));
    }

    @ParameterizedTest
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
                Arguments.of("agreements", List.of("REFERENCES cars", "REFERENCES customers"))
        );
    }

    @ParameterizedTest
    @MethodSource("forCheckNotNull")
    void checkNotNull(String tableName, List<String> columnNames) throws SQLException {
        List<String> actual = util.getNotNull(tableName);
        assertThat(actual).containsAnyElementsOf(columnNames);
    }

    private static Stream<Arguments> forCheckNotNull() {
        return Stream.of(
                Arguments.of("customers", List.of("first_name", "last_name")),
                Arguments.of("agreements", List.of("start_date")),
                Arguments.of("cars", List.of("brand", "manufacturer", "color", "seats_amount"))
        );
    }

    @ParameterizedTest
    @CsvSource({"customers,first_name", "customers,last_name", "agreements,start_date",
            "cars,brand", "cars,manufacturer", "cars,color", "cars,seats_amount"})
    void checkNotNull(String tableName, String columnName) throws SQLException {
        List<String> actual = util.getNotNull(tableName);
        assertTrue(actual.contains(columnName), String.format("Column %s in table %s should be not null", columnName, tableName));
    }

    @ParameterizedTest
    @CsvSource({"customers, phone_number", "cars, number"})
    void checkUnique(String tableName, String columnName) throws SQLException {
        List<String> actual = util.getUnique(tableName);
        assertTrue(actual.contains(columnName), String.format("Column %s in table %s should be unique", columnName, tableName));
    }

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