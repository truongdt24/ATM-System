package ui;

import DAO.AccountDAO;
import DAO.TransactionDAO;
import model.Account;
import model.Transaction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ATMConsole {
    public void start() {
        try {
            Scanner sc = new Scanner(System.in);
            AccountDAO accountDAO = new AccountDAO();
             TransactionDAO transactionDAO = new TransactionDAO();

            System.out.println("Nhập số tài khoản / Enter card number: ");
            String cardNumber = sc.nextLine();
            System.out.println("Nhập mã pin / Enter pin number: ");
            String pin = sc.nextLine();
            Account account = accountDAO.login(cardNumber, pin);
            if (account != null) {
                System.out.println("Welcome " + account.getHolderName());
                while (true){
                    System.out.println("\n===ATM Menu ===");
                    System.out.println(" 1. Kiểm tra tài khoản / Check Balance");
                    System.out.println(" 2. Nạp tiền / Deposit");
                    System.out.println(" 3. Rút tiền / Withdraw");
                    System.out.println(" 4. Chuyển tiền / Transfer");
                    System.out.println(" 5. Lịch sử giao dịch / Transaction History");
                    System.out.println(" 0. Exit");
                    System.out.println("Choose : ");

                    int choice = sc.nextInt();
                    switch (choice){
                        case 1: // check
                            BigDecimal balance = accountDAO.getBalance(account.getId());
                            System.out.println("============================");
                            System.out.println("Số dư / Balance: " + account.getBalance());
                            System.out.println("============================");
                            break;

                        case 2: // nạp
                            System.out.println("Nhập số tiền nạp / Enter deposit amount");
                            BigDecimal depositAmount = sc.nextBigDecimal();
                            // validate
                            if (depositAmount.compareTo(BigDecimal.ZERO) <= 0){
                                System.out.println("Số tiền phải lớn hơn 0 / Amount must be greater than 0");
                            break;
                            }
                            accountDAO.deposit(account.getId(), depositAmount);
                            transactionDAO.save(account.getId(), "Nạp tiền / DEPOSIT", depositAmount);
                            System.out.println("============================");
                            System.out.println("Deposited: " + depositAmount + "VND");
                            System.out.println("============================");
                        break;

                        case 3: // rút
                            System.out.println("Nhập số tiền rút / Enter withdraw amount");
                            BigDecimal withdrawAmount = sc.nextBigDecimal();
                            if (withdrawAmount.compareTo(BigDecimal.ZERO) <= 0){
                                System.out.println("Số tiền phải lớn hơn 0 / Amount must be greater than 0");
                                break;
                            }

                             boolean success =  accountDAO.withdraw(account.getId(), withdrawAmount);
                            if (success) {

                                System.out.println("============================");
                                transactionDAO.save(account.getId(), "Rút tiền / Withdraw", withdrawAmount);
                                System.out.println("Withdraw: " + withdrawAmount + "VND");
                                System.out.println("============================");
                            }else {
                                System.out.println(" Số dư không đủ / insufficient balance");
                            }
                            break;

                        case 4: // chuyển
                            System.out.println("Nhập số tài khoản người nhận / Enter receiver card number");
                            String toCard = sc.next();

                            if (toCard.equals(account.getCardNumber())){
                                System.out.println("Không thể tự chuyển vào tài khoản / Cannot transfer to yourself");
                                break;
                            }

                            System.out.println("Nhập số tiền / Enter amount : ");
                            BigDecimal transferAmount = sc.nextBigDecimal();

                            if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
                                System.out.println("Số tiền phải lớn hơn 0 / Amount must be greater than 0");
                                break;
                            }

                            boolean transferred = accountDAO.transfer(
                                    account.getId(),
                                    toCard,
                                    transferAmount
                            );
                            if (transferred){
                                System.out.println("============================");
                                System.out.println("Đã chuyển số tiền / Transferred : " + transferAmount + "VND");
                                System.out.println("Tới tài khoản / To card: " + toCard);
                                System.out.println("============================");
                            }else {
                                System.out.println("Chuyển thất bại, kiểm tra tài khoản hoặc số dư / Transfer failed, check card number or balance");
                            }
                            break;

                        case 5: // lịch sử gd
                            List<Transaction> history = transactionDAO.getHistory(account.getId());
                            if (history.isEmpty()){
                                System.out.println("Lịch sử giao dịch trống / No transactions yet");
                                break;
                            }

                            System.out.println("==============HISTORY==============");
                            for (Transaction t : history){
                                System.out.printf("%-10s | %15s VND | %s%n",
                                        t.getType(),
                                        t.getAmount(),
                                        t.getCreatedAt()
                                        );
                            }
                            System.out.println("===================================");
                            break;

                        case 0:
                            System.out.println("Chào tạm biệt / Goodbye");
                            return;

                        default:
                            System.out.println(" Lựa chọn k hợp lệ / Invalid choice");
                            break;
                    }

                }

            } else {
                System.out.println(" Sai mật khẩu hoặc số tài khoản / Wrong password or card number");
            }
        }catch (SQLException e){
            System.out.println("Database error: " + e);
        }
    }
}
