package madang;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Scanner;

public class MainApp {
  public static int publicBookid = 0, publicBookPrice = 0;
  static Connection conn = DBConn.makeConnection();

  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    int menu = 0;
    if (conn != null) {
      while (true) {
        System.out.println("메뉴를 선택해주세요(종료하려면 -1) ");
        System.out.println("1:도서리스트, 2:고객리스트, 3:주문리스트 ");
        System.out.println("11:도서추가등록, 12:고객추가등록, 13:주문주가등록 ");
        System.out.print("21:도서삭제, 22:고객삭제, 23:주문삭제 : ");
        menu = in.nextInt();
        if (menu < 0) {
          System.out.println("프로그램을 종료합니다.");
          break;
        }
        try {
          switch (menu) {
            case 1:
              bookList();
              break;
            case 2:
              customerList();
              break;
            case 3:
              orderList();
              break;
            case 11:
              addBook();
              break;
            case 12:
              addCustomer();
              break;
            case 13:
              addOrder();
              break;
            case 21:
              changeBook();
              break;
            case 22:
              changeCustomer();
              break;
            case 23:
              changeOrder();
              break;
          }
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  //  1 : 도서 리스트 확인
  static void bookList() throws SQLException {
    String sql = "SELECT * FROM book;";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery();
    if (rs != null) {
      System.out.println("---------------------------------------------------");
      System.out.println("bookid| price |         bookname      |  publisher  ");
      System.out.println("---------------------------------------------------");
      while (rs.next()) {
        System.out.printf(" %2d ", rs.getInt(1));
        System.out.printf(" %7d ", rs.getInt("price"));
        System.out.printf("  %-20s ", rs.getString("bookname"));
        System.out.printf(" %-20s \n", rs.getString("publisher"));

      }
      System.out.println("---------------------------------------------------");
    }
  }

  // 2 : 고객 리스트 확인
  static void customerList() throws SQLException {
    String sql = "SELECT * FROM customer;";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery();
    if (rs != null) {
      System.out.println("--------------------------------------------");
      System.out.println("고객id|  고객명  |  전화번호    |     주   소   ");
      System.out.println("--------------------------------------------");
      while (rs.next()) {
        System.out.printf(" %2d ", rs.getInt(1));
        System.out.printf(" %5s  ", rs.getString("name"));
        System.out.printf(" %-15s ", rs.getString("phone"));
        System.out.printf(" %-20s \n", rs.getString("address"));

      }
      System.out.println("-------------------------------------------");
    }
  }

  //  3 : 주문 리스트 확인
  static void orderList() throws SQLException {
    String sql = "SELECT * FROM vorders;";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery();
    if (rs != null) {
      System.out.println("---------------------------------------------------------------");
      System.out.println("주문id|  고객정보  | 판매가 |  판매일자  |          도서정보     ");
      System.out.println("---------------------------------------------------------------");
      while (rs.next()) {
        System.out.printf(" %2d ", rs.getInt("orderid"));
        System.out.printf(" %2d ", rs.getInt("custid"));
        System.out.printf(" %5s ", rs.getString("name"));
        System.out.printf(" %6d ", rs.getInt("saleprice"));
        System.out.printf(" %s ", rs.getDate("orderdate"));
        System.out.printf(" %2d ", rs.getInt("bookid"));
        System.out.printf(" %-20s \n", rs.getString("bookname"));
      }
      System.out.println("---------------------------------------------------------------");
    }
  }

  //  11 : 도서 추가 등록
  static void addBook() throws SQLException {
    Scanner scanner = new Scanner(System.in);
    String[] bookInfo = new String[3];

    System.out.println("bookname/publisher/price 형식으로 작성하시오.");
    bookInfo = scanner.nextLine().split("/");

    String sql = "INSERT INTO book VALUES(?,?,?,?)";

    PreparedStatement pstmt = conn.prepareStatement(sql);
    getMaxBookId();
    pstmt.setInt(1, publicBookid + 1);
    pstmt.setString(2, bookInfo[0]);
    pstmt.setString(3, bookInfo[1]);
    pstmt.setInt(4, Integer.parseInt(bookInfo[2]));

    pstmt.executeUpdate();

    pstmt.close();
  }

  // -> 가장 마지막 bookId 찾기
  static void getMaxBookId() throws SQLException {
    String sql = "SELECT Max(bookid) FROM book;";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery();

    while (rs.next()) {
      publicBookid = rs.getInt(1);
    }
  }

  // 12 : 고객 추가 등록
  static void addCustomer() throws SQLException {
    Scanner scanner = new Scanner(System.in);
    String[] custInfo = new String[4];

    System.out.println("custid/name/address/phone 형식으로 작성하시오.");
    custInfo = scanner.nextLine().split("/");

    String sql = "INSERT INTO customer VALUES(?,?,?,?)";

    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setInt(1, Integer.parseInt(custInfo[0]));
    pstmt.setString(2, custInfo[1]);
    pstmt.setString(3, custInfo[2]);
    pstmt.setString(4, custInfo[3]);

    pstmt.executeUpdate();

    pstmt.close();
  }

  //  13 : 주문 추가 등록
  static void addOrder() throws SQLException {
    Scanner scanner = new Scanner(System.in);
    String[] orderInfo = new String[2];

    System.out.println("custid/bookid 형식으로 작성하시오.");
    orderInfo = scanner.nextLine().split("/");

    String sql = "INSERT INTO orders(custid, bookid, saleprice,orderdate) VALUES(?,?,?,?)";

    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setString(1, orderInfo[0]);
    pstmt.setString(2, orderInfo[1]);

    bookPrice(orderInfo[1]);
    pstmt.setInt(3, publicBookPrice);

    LocalDateTime regDateTime = LocalDateTime.now();
    pstmt.setString(4, regDateTime.toString());

    pstmt.executeUpdate();

    pstmt.close();
  }

  // -> bookId에 따른 price값 찾기
  static void bookPrice(String bookid) throws SQLException {
    String sql = "SELECT price FROM book WHERE bookid=?;";

    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setInt(1, Integer.parseInt(bookid));
    ResultSet rs = pstmt.executeQuery();

    while (rs.next()) {
      publicBookPrice = rs.getInt(1);
    }
  }

  // 21 : 도서 정보 삭제
  private static void changeBook() throws SQLException {
    // 수정하고 싶은 책id 입력받아서 내용 확인하기
    Scanner in = new Scanner(System.in);

    System.out.print("수정할 책의 id를 입력하세요.");
    int bookId = in.nextInt();
    String sql = "SELECT bookname, publisher, price FROM book where bookid = ?;";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    pstmt.setInt(1, bookId);
    ResultSet rs = pstmt.executeQuery();
    if (rs != null && rs.next()) {
      System.out.println(rs.getString(1) + ","
          + rs.getString(2) + ","
          + rs.getInt(3));
    }
    // 수정할 도서정보 입력받아서 db에 업데이트하기
    System.out.print("책의 가격을 수정하려면 입력하세요. 수정하지 않으려면 !");
    String price = in.next();
    if (!price.equals("!")) {
      sql = "update book set price = ? where bookid = ? ;";
      pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, Integer.parseInt(price));
      pstmt.setInt(2, bookId);
      int res = pstmt.executeUpdate();
      if (res == 1) System.out.println("수정완료!");
    }
  }

  //  22 : 고객 정보 삭제
  private static void changeCustomer() throws SQLException {

  }

  //  23 : 주문 정보 삭제
  private static void changeOrder() throws SQLException {

  }
}