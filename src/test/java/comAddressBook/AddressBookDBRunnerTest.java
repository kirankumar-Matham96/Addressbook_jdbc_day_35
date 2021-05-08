package comAddressBook;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static org.junit.Assert.*;

public class AddressBookDBRunnerTest {

    String url = "jdbc:mysql://localhost:3306/address_book_db";//127.0.0.1:3306//localhost:3306
    String userName = "root";
    String password = "Energy123@*/+";
    AddressBookDBRunner addressBookDBRunner;

    @Before
    public void setUp() throws Exception {
        addressBookDBRunner = new AddressBookDBRunner();
    }

    @Test
    public void whenConnectionEstablished_returnsTrue() throws SQLException {
        Connection connection = DriverManager.getConnection(url, userName, password);
        Assert.assertTrue(connection != null);
    }

    @Test
    public void whenExecuteQueryCurrect_returnProperData() throws SQLException {
        String first_name = "";
        try (Connection connection = DriverManager.getConnection(url, userName, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from address_book where id = 1");
            while (resultSet.next()) {
                first_name = resultSet.getString("first_name");
            }
        }
        Assert.assertEquals("Kiran Kumar", first_name);
    }

    @Test
    public void whenExecuteQueryWrong_returnImproperData() throws SQLException {
        String first_name = "";
        try (Connection connection = DriverManager.getConnection(url, userName, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from address_book where id = 2");
            while (resultSet.next()) {
                first_name = resultSet.getString("first_name");
            }
        }
        Assert.assertNotEquals("Kiran Kumar",first_name);
    }
}