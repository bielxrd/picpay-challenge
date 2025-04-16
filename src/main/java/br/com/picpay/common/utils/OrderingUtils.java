package br.com.picpay.common.utils;

import br.com.picpay.application.dtos.deposit_balance_history.DepositBalanceHistoryResponse;

import java.util.List;

public class OrderingUtils {
    public static List<DepositBalanceHistoryResponse> applyOrdering(List<DepositBalanceHistoryResponse> depositBalanceHistoryResponseList,
                                                                    boolean isDateOrdering,
                                                                    boolean isValueOrdering) {
        if (isDateOrdering) {
            depositBalanceHistoryResponseList.sort((deposit1, deposit2) -> deposit1.getDepositDate().compareTo(deposit2.getDepositDate()));
        }

        if (isValueOrdering) {
            depositBalanceHistoryResponseList.sort((deposit1, deposit2) -> Double.compare(deposit1.getValue(), deposit2.getValue()));
        }

        return depositBalanceHistoryResponseList;
    }
}
