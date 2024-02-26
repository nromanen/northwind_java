//package database;
//
//import org.assertj.core.api.SoftAssertions;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvFileSource;
//import org.junit.jupiter.params.provider.CsvSource;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.junit.jupiter.api.Assumptions.assumeTrue;
//
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class DBStructureTest {
//    private static boolean allColumnsExists = false;
//    private static Util util;
//
//    @BeforeAll
//    public static void beforeAll() throws SQLException, IOException {
//        new Util().executeFile("init.sql");
//    }
//
//    @Test
//    void existTable() throws SQLException {
//        boolean actual = util.isTableExist("categories");
//        assertEquals(true, actual, "Table categories doesn't exists");
//    }
//
//    @Test
//    @Order(1)
//    void existColumn() throws SQLException {
//        boolean actual = util.isColumnInTableExist("categories", "avatar");
//        assertEquals(true, actual, "Column avatar in table categories doesn't exists");
//        allColumnsExists = true;
//    }
//
//    /*
//    Replace test existColumn() with this method existColumn(String, String).
//    Add two files (children.csv and club_child,csv) to directory resources
//    for testing presence of columns id, first_name, last_name, birth_date and club_id, child_id
//
//    @ParameterizedTest
//    @CsvFileSource(resources = {"/categories.csv", "/clubs.csv"}, numLinesToSkip = 1)
//    void existColumn(String tableName, String columnName) throws SQLException {
//        boolean actual = isColumnInTableExist(tableName, columnName);
//        assertEquals(true, actual, "Column " + columnName + " in table " + tableName + " doesn't exists");
//        allColumnsExists = true;
//    }*/
//
//    @Test
//    @Order(2)
//    void columnType() throws SQLException {
//        assumeTrue(allColumnsExists, "Skipping test for column type because the test for presence column failed.");
//
//        String expected = "character varying";
//        String actual = getColumnType("categories", "avatar");
//        assertEquals(expected, actual, String.format("Type for column %s should be %s", "avatar", expected));
//    }
//
//    @Test
//    void checkPK() throws SQLException {
//        String expected = "id";
//        String actual = getPK("categories");
//        assertEquals(expected, actual, String.format("PK for table %s should be named %s", "categories", expected));
//    }
//
//    @Test
//    void checkFKs() throws SQLException {
//        List<String> actual = getFKs("club_child");
//        List<String> expected = List.of("REFERENCES child", "REFERENCES clubs");
//        SoftAssertions assertions = new SoftAssertions();
//        expected.forEach(outer -> assertions.assertThat(actual.stream().anyMatch(e -> e.contains(outer)))
//                .withFailMessage(String.format("Table %s should contain FK with %s", "club_child", outer))
//                .isTrue());
//        assertions.assertAll();
//
//
///*        Instead of using Jupiter's assertAll for improved agility, it is advisable to utilize SoftAssertions from assertJ. So we should delete this code
//        assertAll("Check if FK present",
//                () -> assertTrue(actual.stream().anyMatch(e -> e.contains(expected.get(0))),
//                        String.format("Table %s should contain FK with %s", tableName, expected.get(0))),
//                () -> assertTrue(actual.stream().anyMatch(e -> e.contains(expected.get(1))),
//                       String.format("Table %s should contain FK with %s", tableName, expected.get(1))));
//*/
//    }
//
//    @Test
//    void checkNotNull() throws SQLException {
//        List<String> actual = getNotNull("categories");
//        assertTrue(actual.contains("title"), "Column title in table categories should be not null");
//    }
//
//    /*
//    Replace test checkNotNull() with this method checkNotNull(String, String).
//    Provide more values to @CsvSource for chеck all not null column in tables
//
//    @ParameterizedTest
//    @CsvSource({"categories,title", "categories, avatar", "children, first_name", "children, last_name", "clubs, title"})
//    void checkNotNull(String tableName, String columnName) throws SQLException {
//        List<String> actual = getNotNull(tableName);
//        assertTrue(actual.contains(columnName), String.format("Column %s in table %s should be not null", columnName, tableName));
//    }*/
//
//    @Test
//    void checkUnique() throws SQLException {
//        List<String> actual = getUnique("clubs");
//        assertTrue(actual.contains("title"), "Column title in table clubs should be unique");
//    }
//
//}