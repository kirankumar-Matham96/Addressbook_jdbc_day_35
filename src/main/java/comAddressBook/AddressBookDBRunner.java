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

/**
 * UC16: Connecting to database and retrieving data
 * UC17: Update the data in the database
 * UC18: Retrieve data from database that is added in certain date-time period
 * UC19: Retrieve number of contacts by city or state
 * UC20: Add new contact to the database
 * UC21: RestAssured: read or write to the json server
 * UC22: Ensure IO Operation is not blocking the main thread
 *       while doing CURD operation on any of the data source
 *       i.e. DB, CSV, File, JSON File, or JSONServer.
 * UC23: Save the AddressBook to Database and Ensure Open-Close
 *       Principle is not violated when new data source is
 *       added to already three data source
 *       i.e. CSV File, JSON File or JSONServer.
 */

public class AddressBookDBRunner {
    public static void main(String[] args) throws ClassNotFoundException, SQLException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        AddressBookDBRunner addressBookDBRunner = new AddressBookDBRunner();
        /**
         * register and load drivers
         */
        Class.forName("com.mysql.cj.jdbc.Driver");//deprecated in 2006

        /**
         * printing the drivers
         */
        listDrivers();

        /**
         * Prerequisites: url, user name, password and some queries to execute.
         */
        String url = "jdbc:mysql://localhost:3306/address_book_db";/*useSSL=false";*///127.0.0.1:3306//localhost:3306
        String userName = "root";
        String password = "Energy123@*/+";
        String selectAllQuery = "SELECT * FROM address_book;";
        String updateQuery = "UPDATE address_book SET email = 'updated_email@gmail.com' WHERE first_name = 'Charan' AND last_name = 'Thrivedi';";
        String getByDateRange = "SELECT * FROM address_book WHERE date_added BETWEEN CAST('2010-01-01' AS DATE) AND CAST('2020-12-31' AS DATE)";
        String numberByStateQuery = "SELECT state,count(*) AS count FROM address_book WHERE state = ?;";
        String numberByCityQuery = "SELECT city,count(*) AS count FROM address_book WHERE city = ?;";
        String addNewContactQuery = "insert into address_book " +
                                    "(first_name,last_name,address,city,state,zip,phonenumber,email,date_added)"+
                                    "values(" +
                                        "?,?,?,?,?,?,?,?,CAST(? AS DATE)"+
                                    ");";


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
            PreparedStatement preparedStatementState = connection.prepareStatement(numberByStateQuery);
            PreparedStatement preparedStatementCity = connection.prepareStatement(numberByCityQuery);
            PreparedStatement preparedStatementNewInsert = connection.prepareStatement(addNewContactQuery);

            /**
             * getting results from executing query(existing data)
             */
            ResultSet resultSet = statement.executeQuery(selectAllQuery);
            System.out.println("\nAll the data existed in the data base:\n");
            addressBookDBRunner.retrieveAllDataFromDatabase(resultSet);

            /**
             * UC17: Update the data in the database.
             */
            statement.executeUpdate(updateQuery);//updating email for user 'Charan'.

            /**
             * getting results from executing query(updated data)
             */
            resultSet = statement.executeQuery(selectAllQuery);
            System.out.println("\nAfter updating existing contact:\n");
            addressBookDBRunner.retrieveAllDataFromDatabase(resultSet);

            /**
             * UC18: Getting details of persons in a particular date range
             */
            resultSet = statement.executeQuery(getByDateRange);//date range: 2010-01-01 to 2020-12-31
            System.out.println("\npersons added in between 2010-01-01 and 2020-12-31: \n");
            addressBookDBRunner.retrieveAllDataFromDatabase(resultSet);

            /**
             * UC19: getting person count by city/state.
             * using prepared statement
             */
            //count by state:
            //passing parameter
            preparedStatementState.setString(1, "Telangana"); //Maharastra, Karnataka
            //executing query and populating resultSet
            resultSet = preparedStatementState.executeQuery();
            //printing resultSet(prints count by state)
            while(resultSet.next()) {
                System.out.println("\ncount by state:\n"+resultSet.getString("state") + " " + resultSet.getString("count"));
            }

            //count by city:
            //passing parameter
            preparedStatementCity.setString(1, "Pune"); //Hyderabad, Benguluru
            //executing query and populating resultSet
            resultSet = preparedStatementCity.executeQuery();
            //printing resultSet(prints count by state)
            while(resultSet.next()) {
                System.out.println("\ncount by city:\n"+resultSet.getString("city") + " " + resultSet.getString("count"));
            }

            /**
             * UC20: adding new contact/s
             */
            //adding single person
            preparedStatementNewInsert.setString(1,"New00"+7);//first_name
            preparedStatementNewInsert.setString(2,"Person00"+7);//last_name
            preparedStatementNewInsert.setString(3,"New.P Address");//address
            preparedStatementNewInsert.setString(4,"new city");//city
            preparedStatementNewInsert.setString(5,"new state");//state
            preparedStatementNewInsert.setInt(6,700000);//zip
            preparedStatementNewInsert.setString(7,"+66 1478523698");//phonenumber
            preparedStatementNewInsert.setString(8,"new.p@gmail.com");//email
            preparedStatementNewInsert.setString(9,"2015-11-25");

            preparedStatementNewInsert.executeUpdate();

            resultSet = statement.executeQuery(selectAllQuery);
            System.out.println("\nAfter inserting single new contact:\n");
            addressBookDBRunner.retrieveAllDataFromDatabase(resultSet);

            //adding multiple persons at the same time(using Batch execution)
            int numberOfPersonsAdding = 5;//adding 5 new contacts
            for(int i = 1;i<=numberOfPersonsAdding;i++){
                preparedStatementNewInsert.setString(1,"New00"+i);//first_name
                preparedStatementNewInsert.setString(2,"Person00"+i);//last_name
                preparedStatementNewInsert.setString(3,i+"New.P Address");//address
                preparedStatementNewInsert.setString(4,i+"new city");//city
                preparedStatementNewInsert.setString(5,i+"new state");//state
                preparedStatementNewInsert.setInt(6,700000+i);//zip
                preparedStatementNewInsert.setString(7,"+"+i+" 1478523698");//phonenumber
                preparedStatementNewInsert.setString(8,i+"new.p@gmail.com");//email
                preparedStatementNewInsert.setString(9,"2000-08-0"+i);

                preparedStatementNewInsert.addBatch();//adding statements to the batch.
            }
            preparedStatementNewInsert.executeBatch();//executing bunch of queries all together.

            //after inserting set of new contacts
            resultSet = statement.executeQuery(selectAllQuery);
            System.out.println("\nAfter inserting set of new contacts:\n");
            addressBookDBRunner.retrieveAllDataFromDatabase(resultSet);

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * UC16: retrieving data from database.
     * gathering data from database to write to the txt,csv,json files and print to console
     */
    public void retrieveAllDataFromDatabase(ResultSet resultSet) throws SQLException, IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        List<String[]> addressBookDataListForCSV = new ArrayList();
        List<String> addressBookDataListForJsonAndTxt = new ArrayList();
        ResultSetMetaData metaData = resultSet.getMetaData();
        StringBuffer heading = new StringBuffer();
        String[] stringHeading = new String[1];

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
        stringHeading[0] = heading.toString();
        addressBookDataListForCSV.add(stringHeading);

        System.out.println(heading);

        while (resultSet.next()) {
            StringBuffer dataFromDB = new StringBuffer();
            StringBuffer dataFromDBJson = new StringBuffer();

            //appending data to string buffer
            dataFromDB.append(resultSet.getInt(1)).append(", ")
                    .append(resultSet.getString("first_name")).append(", ")
                    .append(resultSet.getString("last_name")).append(", ")
                    .append(resultSet.getString("address")).append(", ")
                    .append(resultSet.getString("city")).append(", ")
                    .append(resultSet.getString("state")).append(", ")
                    .append(resultSet.getString("zip")).append(", ")
                    .append(resultSet.getString("phonenumber")).append(", ")
                    .append(resultSet.getString("email"));
            String[] stringData = new String[]{dataFromDB.toString()};

            //adding data to list
            addressBookDataListForCSV.add(stringData);//adding to the list
            addressBookDataListForJsonAndTxt.add(dataFromDB.toString());
        }
        System.out.println("######################################");
        System.out.println("Printing all the data to console:\n");
        addressBookDataListForJsonAndTxt.stream().forEach(System.out::println);
        System.out.println("######################################");

        writeToCSVFile(addressBookDataListForCSV);//writing to csv
        writeToJsonFile(addressBookDataListForJsonAndTxt);//writing to json
        writeToTextFile(addressBookDataListForJsonAndTxt);//writing to txt
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
     * writing data fetched from database to text file
     */
    public void writeToTextFile(List<String> addressBookData) throws IOException {
        NormalFileHandler normalFileHandler = new NormalFileHandler();
        normalFileHandler.writeToNormalTextFile(addressBookData);
    }

    /**
     * writing data fetched from database to csv file
     */
    public void writeToCSVFile(List<String[]> addressBookData) throws IOException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException {
        CSVFileHandler csvFileHandler = new CSVFileHandler();
        csvFileHandler.csvWriter(addressBookData);
    }

    /**
     * writing data fetched form database to json file
     */
    public void writeToJsonFile(List<String> addressBookData) {
        JsonFileHandler jsonFileHandler = new JsonFileHandler();
        jsonFileHandler.jsonWriter(addressBookData);
    }
}
