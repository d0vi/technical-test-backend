package com.playtomic.tests.wallet.infrastructure.adapter.driver.rest.controller;

import com.playtomic.tests.wallet.domain.model.wallet.exception.InvalidDepositAmountException;
import com.playtomic.tests.wallet.domain.model.wallet.exception.InvalidWalletIdException;
import com.playtomic.tests.wallet.domain.model.wallet.exception.UnknownWalletIdException;
import com.playtomic.tests.wallet.infrastructure.adapter.driven.provider.stripe.StripeAmountTooSmallException;
import java.time.Instant;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class WalletExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(InvalidDepositAmountException.class)
  public ProblemDetail handleInvalidAmountException(InvalidDepositAmountException e) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    problemDetail.setTitle("Invalid deposit amount");
    problemDetail.setProperty("code", "INVALID_AMOUNT");
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(InvalidWalletIdException.class)
  public ProblemDetail handleInvalidWalletIdException(InvalidWalletIdException e) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    problemDetail.setTitle("Invalid wallet id");
    problemDetail.setProperty("code", "INVALID_WALLET_ID");
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(UnknownWalletIdException.class)
  public ProblemDetail handleUnknownWalletIdException(UnknownWalletIdException e) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    problemDetail.setTitle("Wallet not found");
    problemDetail.setProperty("code", "NOT_FOUND");
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(StripeAmountTooSmallException.class)
  public ProblemDetail handleStripeAmountTooSmallException(StripeAmountTooSmallException e) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY, "Stripe could not process the payment");
    problemDetail.setTitle("Payment error");
    problemDetail.setProperty("code", "PAYMENT_ERROR");
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }

  @ExceptionHandler(OptimisticLockingFailureException.class)
  public ProblemDetail handleOptimisticLockingFailureException(
      OptimisticLockingFailureException e) {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "The wallet was modified by another request. Please retry your operation.");
    problemDetail.setTitle("Concurrent modification detected");
    problemDetail.setProperty("code", "OPTIMISTIC_LOCK_ERROR");
    problemDetail.setProperty("timestamp", Instant.now());
    return problemDetail;
  }
}
