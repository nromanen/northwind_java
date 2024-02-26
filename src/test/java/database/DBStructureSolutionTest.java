package database;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.io.IOException;
import java.sql.SQLException;
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
    @ValueSource(strings = {"clubs", "children", "categories", "club_child"})
    void existTable(String tableName) throws SQLException {
        boolean actual = util.isTableExist(tableName);
        assertEquals(true, actual, "Table " + tableName + " doesn't exists");
    }


    @Order(1)
    @ParameterizedTest
    @CsvFileSource(resources = {"/categories.csv", "/clubs.csv", "/children.csv", "/club_child.csv"}, numLinesToSkip = 1)
    void existColumn(String tableName, String columnName) throws SQLException {
        boolean actual = util.isColumnInTableExist(tableName, columnName);
        assertEquals(true, actual, "Column " + columnName + " in table " + tableName + " doesn't exists");
        allColumnsExists = true;
    }

    @Order(2)
    @ParameterizedTest
    @CsvFileSource(resources = {"/categories.csv", "/clubs.csv", "/children.csv", "/club_child.csv"}, numLinesToSkip = 1)
    void columnType(String tableName, String columnName, String columnType) throws SQLException {
        assumeTrue(allColumnsExists, "Skipping test for columns type because some tests for presence columns failed.");

        String actual = util.getColumnType(tableName, columnName);
        assertEquals(columnType, actual, String.format("Type for column %s should be %s", columnName, columnType));
    }

    @ParameterizedTest
    @CsvSource({"id,categories", "id,clubs", "id,children"})
    void checkPK(String PKName, String tableName) throws SQLException {
        String actual = util.getPK(tableName);
        assertEquals(PKName, actual, String.format("PK for table %s should be named %s", tableName, PKName));
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
                Arguments.of("club_child", List.of("REFERENCES child", "REFERENCES clubs")),
                Arguments.of("clubs", List.of("REFERENCES categories")
                )
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
                Arguments.of("categories", List.of("title", "avatar", "id")),
                Arguments.of("children", List.of("first_name", "last_name")),
                Arguments.of("clubs", List.of("title", "description"))
        );
    }

    @ParameterizedTest
    @CsvSource({"categories,title", "categories,avatar",
            "children,first_name", "children,last_name",
            "clubs,title", "clubs,description"})
    void checkNotNull(String tableName, String columnName) throws SQLException {
        List<String> actual = util.getNotNull(tableName);
        assertTrue(actual.contains(columnName), String.format("Column %s in table %s should be not null", columnName, tableName));
    }

    @Test
    void checkUnique() throws SQLException {
        String tableName = "clubs";
        String expected = "title";
        List<String> actual = util.getUnique(tableName);
        assertTrue(actual.contains(expected), String.format("Column %s in table %s should be unique", expected, tableName));
    }
}