import java.sql.*;
import java.util.Scanner;

public class MysqlConnection {
  private Connection connection;
  public MysqlConnection() {

    Connection conn = null;

    try {
      // MySQL 연결 정보
      String url = "jdbc:mysql://localhost:3306/book";
      String username = "root";
      String password = "1234";

      // JDBC 드라이버 로딩
      Class.forName("com.mysql.cj.jdbc.Driver");

      // MySQL 연결
      connection = DriverManager.getConnection(url, username, password);
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws SQLException {
    MysqlConnection program = new MysqlConnection();
    Scanner scanner = new Scanner(System.in);

    while (true) {
      System.out.println("====== 도서 관리 프로그램 ======");
      System.out.println("1. 도서 목록 조회");
      System.out.println("2. 고객 목록 조회");
      System.out.println("3. 주문 내역 조회");
      System.out.println("4. 종료");
      System.out.print("메뉴 선택: ");
      int choice = scanner.nextInt();

      switch (choice) {
        case 1:
          program.printBookList();
          break;
        case 2:
          program.printCustomerList();
          break;
        case 3:
          break;
        case 4:
          System.out.println("프로그램을 종료합니다.");
          System.exit(0);
          break;
      }
      System.out.println();
    }
  }

  public void printBookList() throws SQLException {
    PreparedStatement statement = null;
    String sql = "SELECT bookId, bookName, publisher, price FROM bookList";

    statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();

    while (resultSet.next()) {
      int bookId  = resultSet.getInt("bookId");
      String bookName = resultSet.getString("bookName");
      String publisher = resultSet.getString("publisher");
      int price = resultSet.getInt("price");

      System.out.print(bookId +". ");
      System.out.print("bookName: " + bookName);
      System.out.print(" / publisher: " + publisher);
      System.out.print(" / price: " + price);
      System.out.println();
    }
  }

  public void printCustomerList() throws SQLException {
    PreparedStatement statement = null;
    String sql = "SELECT customerId, name, phone, address FROM customerList";

    statement = connection.prepareStatement(sql);
    ResultSet resultSet = statement.executeQuery();

    while (resultSet.next()) {
      int customerId  = resultSet.getInt("customerId");
      String name = resultSet.getString("name");
      String phone = resultSet.getString("phone");
      String address = resultSet.getString("address");

      System.out.print(customerId +". ");
      System.out.print("name: " + name);
      System.out.print(" / phone: " + phone);
      System.out.print(" / address: " + address);
      System.out.println();
    }
  }
}
