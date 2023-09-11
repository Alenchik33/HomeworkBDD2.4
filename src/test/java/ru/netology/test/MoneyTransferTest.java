package ru.netology.test;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;


public class MoneyTransferTest {

    DashboardPage dashboardPage;

    @BeforeEach
    void setup() {
        Selenide.open("http://localhost:9999");
        var authInfo = getAuthInfo();
        var verificationPage = LoginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor();
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Test
    void shouldTransferFromFirstCardToSecond() {
        var firstCardInfo = getFirstCartInfo();
        var secondCardInfo = getSecondCartInfo();
        var firstCardBalance = dashboardPage.getCardBalance(getFirstCartInfo());
        var secondCardBalance = dashboardPage.getCardBalance(getSecondCartInfo());
        var amount = generateValidAmount(firstCardBalance);
        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCardBalance + amount;
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
        assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
        assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard);
    }
        @Test
        void shouldGetErrorMessageIfAmountMoreBalance() {
            var firstCardInfo = getFirstCartInfo();
            var secondCardInfo = getSecondCartInfo();
            var firstCardBalance = dashboardPage.getCardBalance(getFirstCartInfo());
            var secondCardBalance = dashboardPage.getCardBalance(getSecondCartInfo());
            var amount = generateInvalidAmount(secondCardBalance);
            var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
            transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
            transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте списания");
            var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
            var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
            assertEquals(firstCardBalance, actualBalanceFirstCard);
            assertEquals(secondCardBalance, actualBalanceSecondCard);
        }
    }