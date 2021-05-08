package comAddressBook;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

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
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        /**
         * UC16: Connecting to database and retrieving data
         */

        /**
         * register and load drivers
         */
        Class.forName("com.mysql.cj.jdbc.Driver");//deprecated in 2006

        /**
        * printing the drivers
        */
        listDrivers();

        /**
         * connecting to database
         */
        String url = "jdbc:mysql://localhost:3306/address_book_db?useSSL=false";//127.0.0.1:3306//localhost:3306
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
             * gathering data from database to write to the csv file
             */
            List<Object> addressBookDataList = new ArrayList();
            ResultSetMetaData metaData = resultSet.getMetaData();
            StringBuffer heading = new StringBuffer();

            //adding headings of the columns
            heading.append(metaData.getColumnName(1)).append(", ")
                    .append(metaData.getColumnName(2)).append(", ")
                    .append(metaData.getColumnName(3)).append(", ")
                    .append(metaData.getColumnName(4)).append(", ")
                    .append(metaData.getColumnName(5)).append(", ")
                    .append(metaData.getColumnName(6)).append(", ")
                    .append(metaData.getColumnName(7)).append(", ")
                    .append(metaData.getColumnName(8)).append(", ")
                    .append(metaData.getColumnName(9));
            addressBookDataList.add(heading);
            System.out.println(heading);

            while(resultSet.next()) {
                StringBuffer dataFromDB = new StringBuffer();
                dataFromDB.append(resultSet.getInt(1)).append(", ")
                          .append(resultSet.getString("first_name")).append(", ")
                          .append(resultSet.getString("last_name")).append(", ")
                          .append(resultSet.getString("address")).append(", ")
                          .append(resultSet.getString("city")).append(", ")
                          .append(resultSet.getString("state")).append(", ")
                          .append(resultSet.getString("zip")).append(", ")
                          .append(resultSet.getString("phonenumber")).append(", ")
                          .append(resultSet.getString("email"));
                addressBookDataList.add(dataFromDB);//adding to the list
            }

            //calling method to write the data to the CSV file
            AddressBookDBRunner addressBookDBRunner = new AddressBookDBRunner();
            addressBookDBRunner.writeToCSVFile(addressBookDataList);

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
        } catch (CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        } catch (CsvDataTypeMismatchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * gets the drivers that loaded
     */
    private static void listDrivers() {
        Enumeration<Driver> driverList = DriverManager.getDrivers();
        while (driverList.hasMoreElements()) {
            Driver driverClass = driverList.nextElement();
            System.out.println("Driver loaded:  " + driverClass.getClass().getName());
        }
    }

    /**
     * writing data fetched from database to csv file
     */
    public void writeToCSVFile(List<Object> addressBookData) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, CsvDataTypeMismatchException {
        CSVFileHandler csvFileHandler = new CSVFileHandler();
        csvFileHandler.csvWriter(addressBookData);
    }
}
