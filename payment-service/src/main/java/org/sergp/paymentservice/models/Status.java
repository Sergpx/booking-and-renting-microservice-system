package org.sergp.paymentservice.models;

public enum Status {
    PENDING_PAYMENT, // (Ожидание оплаты)
    CONFIRMED, // (Подтверждено)
    CANCELLED, // (Отменено)
    COMPLETED, // (Завершено)
    EXPIRED, // (Истекло)
    REFUND_PROCESS // (Процесс возврата)
}
