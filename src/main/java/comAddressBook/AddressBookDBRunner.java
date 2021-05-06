package comAddressBook;

import java.sql.*;

/**
 * steps to follow to connect Java application with database
 * 1) Import java.sql
 * 2) Load and register the driver
 * 3) Create connection
 * 4) Create a statement
 * 5) Execute the query
 * 6) Process the results
 * 7) Close resources
 */

public class AddressBookDBRunner {
    public static void main(String[] args) throws ClassNotFoundException {
        /**
         * register and load drivers
         */
        Class.forName("com.mysql.cj.jdbc.Driver");//deprecated in 2006

        /**
         * connecting to database
         */
        String url = "jdbc:mysql://localhost:3306/address_book_db";//127.0.0.1:3306//localhost:3306
        String userName = "root";
        String password = "Energy123@*/+";
        String query = "SELECT * FROM address_book";

        /**
         * Creating Connection with in try-with resources
         * It will close the connection after the try block automatically
         */
        try (Connection connection = DriverManager.getConnection(url, userName, password)) {

            /**
             * creating a statement
             * NOTE: Statement is auto closable. When Connection is closed,
             *       it will automatically gets closed.
             */
            Statement statement = connection.createStatement();

            /**
             * getting results from executing query
             */
            ResultSet resultSet = statement.executeQuery(query);

            /**
             * printing results
             */
            while (resultSet.next()) {
                //to get the data from the table
                System.out.print(resultSet.getInt(1) + " ");
                System.out.println(
                        resultSet.getString("first_name") + " " +
                                resultSet.getString("last_name") + " " +
                                resultSet.getString("address") + " " +
                                resultSet.getString("city") + " " +
                                resultSet.getString("state") + " " +
                                resultSet.getString("zip") + " " +
                                resultSet.getString("phonenumber") + " " +
                                resultSet.getString("email"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
